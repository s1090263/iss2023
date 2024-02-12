import SystemUtilities.Companion.extractStringBeforeBracket
import SystemUtilities.Companion.getPayloadArgs
import junit.framework.Assert
import junit.framework.Assert.assertTrue
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapObserveRelation
import org.eclipse.californium.core.CoapResponse
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import unibo.basicomm23.interfaces.IApplMessage
import unibo.basicomm23.interfaces.Interaction
import unibo.basicomm23.msg.ProtocolType
import unibo.basicomm23.utils.CommUtils
import unibo.basicomm23.utils.ConnectionFactory

class TestStopResume : CoapHandler {
    lateinit var client : CoapClient
    lateinit var obsRel : CoapObserveRelation
    lateinit var client2 : CoapClient
    lateinit var obsRel2 : CoapObserveRelation
    val host : String = "localhost"
    val port : Int = 9990
    val path : String = "ctxcoldstorageservice/transporttrolley"
    val path2: String = "ctxcoldstorageservice/fridgeservice"

    val trolleyContentList = mutableListOf<String>() //list of the content received by the observable resource
    val fridgeContentList = mutableListOf<String>() //list of the content received by the observable resource


    override fun onLoad(response: CoapResponse?) {
        var content: String? = response?.responseText

        if (content?.let { extractStringBeforeBracket(it) } == "transporttrolley"){
        var payload = getPayloadArgs(content)[0]
        trolleyContentList.add(payload)
        }
        if (content?.let { extractStringBeforeBracket(it) } == "fridgeservice"){
            var payload = getPayloadArgs(content)[0]
            fridgeContentList.add(payload)
        }
    }

    override fun onError() {
        println("TestRequestAcceptedFull - ERROR")
    }

    fun createObserverTrolley(host: String, port: Int, path: String){
        val url = host + ":" + port + "/" + path
        client = CoapClient("coap://" + url)
        obsRel = client.observe(this)
    }

    fun createObserverFridge(host: String, port: Int, path: String){
        val url = host + ":" + port + "/" + path
        client2 = CoapClient("coap://" + url)
        obsRel2 = client2.observe(this)
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            it.unibo.ctxcoldstorageservice.main()
            CommUtils.delay(5000)

        }
    }

    @Before
    fun initTest() {
        createObserverTrolley(host, port, path)
        createObserverFridge(host, port, path2)
    }

    @Test
    fun testRequestAccepted() {
        val conn: Interaction = ConnectionFactory.createClientSupport23(
            ProtocolType.tcp, "localhost", "9990")

        println("TestApplication - Sending store request of 30 kg")
        val storeRequest: IApplMessage = CommUtils.buildRequest("testApplication", "storerequest", "storerequest(30)", "fridgeservice")
        val storeReply: IApplMessage? = conn.request(storeRequest)
        val ticket: String = getPayloadArgs(storeReply?.msgContent())[0]

        //If everything goes as it should, the reply to the storerequest will be loadaccepted
        Assert.assertEquals("loadaccepted", storeReply?.msgId())

        println("TestApplication - Simulating going to indoor to send ticket")
        CommUtils.delay(4000)
        val ticketRequest: IApplMessage = CommUtils.buildRequest("testApplication", "sendticket", "sendticket("+ticket+")", "fridgeservice")
        val ticketReply: IApplMessage? = conn.request(ticketRequest)
        //conn.forward("msg(sendticket,request,testApplication,fridgeservice,sendticket("+ticket+"),1)")

        //If everything goes as it should, the reply to the sendticket will be ticketaccepted
        Assert.assertEquals("ticketaccepted", ticketReply?.msgId())

        //Send stop message
        CommUtils.delay(1000)
        conn.forward("msg(stoptrolley,dispatch,testApplication,fridgeservice,stoptrolley(stop),1)")

        //if got stopped correctly the service sent a stop message and the trolley is in stop state
        CommUtils.delay(1000)
        assertTrue("stoppingTrolley" in fridgeContentList)
        assertTrue("stopped" in trolleyContentList)

        //send resume
        CommUtils.delay(1000)
        conn.forward("msg(resumetrolley,dispatch,testApplication,fridgeservice,resumetrolley(resume),1)")
        //if got resumed correctly the service sent a resume message
        CommUtils.delay(1000)
        assertTrue("resumingTrolley" in fridgeContentList)

        //wait for the "at home" response of the robot
        var limit = 0
        while ( "atHome" !in trolleyContentList && limit < 15){
            limit += 1
            CommUtils.delay(2000)
        }

        //if everything goes as it should, the robot must reach home when it finishes
        assertTrue("chargeTaken" in trolleyContentList)
        assertTrue("atHome" in trolleyContentList)
    }
}