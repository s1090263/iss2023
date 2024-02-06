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

class TestRequestAcceptedFull : CoapHandler {
    lateinit var client : CoapClient
    lateinit var obsRel : CoapObserveRelation
    val host : String = "localhost"
    val port : Int = 9990
    val path : String = "ctxcoldstorageservice/transporttrolley"

    val contentList = mutableListOf<String>() //list of the content received by the observable resource


    override fun onLoad(response: CoapResponse?) {
        var content: String? = response?.responseText
        var payload = getPayloadArgs(content)[0]
        contentList.add(payload)
    }

    override fun onError() {
        println("TestRequestAcceptedFull - ERROR")
    }

    fun createObserver(host: String, port: Int, path: String){
        val url = host + ":" + port + "/" + path
        client = CoapClient("coap://" + url)
        obsRel = client.observe(this)
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
        createObserver(host, port, path)
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
        conn.forward("msg(sendticket,request,testApplication,fridgeservice,sendticket("+ticket+"),1)")

        //wait for the "at home" response of the robot
        var limit = 0
        while ( "atHome" !in contentList && limit < 15){
            limit += 1
            CommUtils.delay(2000)
        }

        //if everything goes as it should, the robot must reach home when it finishes
        assertTrue("atHome" in contentList)
    }
}