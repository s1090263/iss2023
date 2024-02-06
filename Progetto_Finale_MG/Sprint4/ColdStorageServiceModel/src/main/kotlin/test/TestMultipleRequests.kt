import SystemUtilities.Companion.getPayloadArgs
import junit.framework.Assert
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

class TestMultipleRequests : CoapHandler {
    lateinit var client : CoapClient
    lateinit var obsRel : CoapObserveRelation
    val host : String = "localhost"
    val port : Int = 9990
    val path : String = "ctxcoldstorageservice/transporttrolley"

    val contentList = mutableListOf<String>() //list of the content received by the observable resource


    override fun onLoad(response: CoapResponse?) {
        var content : String? = response?.responseText
        var payload = getPayloadArgs(content)[0]
        contentList.add(payload)
    }

    override fun onError() {
        println("TestMultipleRequests - ERROR")
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

        //first request
        println("TestApplication - Sending first store request of 30 kg")
        val storeRequest1: IApplMessage = CommUtils.buildRequest("testApplication", "storerequest", "storerequest(30)", "fridgeservice")
        val storeReply1: IApplMessage? = conn.request(storeRequest1)
        val ticket1: String = getPayloadArgs(storeReply1?.msgContent())[0]

        //If everything goes as it should, the reply to the storerequest will be loadaccepted
        Assert.assertEquals("loadaccepted", storeReply1?.msgId())

        //second request
        println("TestApplication - Sending second store request of 50 kg")
        val storeRequest2: IApplMessage = CommUtils.buildRequest("testApplication", "storerequest", "storerequest(50)", "fridgeservice")
        val storeReply2: IApplMessage? = conn.request(storeRequest2)
        val ticket2: String = getPayloadArgs(storeReply2?.msgContent())[0]

        //If everything goes as it should, the reply to the storerequest will be loadaccepted
        Assert.assertEquals("loadaccepted", storeReply2?.msgId())

        //third request
        println("TestApplication - Sending third store request of 40 kg")
        val storeRequest3: IApplMessage = CommUtils.buildRequest("testApplication", "storerequest", "storerequest(40)", "fridgeservice")
        val storeReply3: IApplMessage? = conn.request(storeRequest3)

        //If the coldroom can't fit the third load, it has to return loadrefused
        Assert.assertEquals("loadrefused", storeReply3?.msgId())

        //sending first ticket
        CommUtils.delay(4000)
        println("TestApplication - Inserting first ticket")
        conn.forward("msg(sendticket,request,testApplication,fridgeservice,sendticket("+ticket1+"),1)")

        //sending second ticket
        CommUtils.delay(3000)
        println("TestApplication - Inserting second ticket")
        conn.forward("msg(sendticket,request,testApplication,fridgeservice,sendticket("+ticket2+"),1)")

        //wait for the "at home" response of the robot
        var limit = 0
        while ( "atHome" !in contentList && limit < 35){
            limit += 1
            CommUtils.delay(4000)
        }

        //if everything goes as it should, the robot must reach home when it finishes
        Assert.assertTrue("atHome" in contentList)

        //if everything goes as it should, the contentlist should containt 2 times the charge taken message
        var count = contentList.count { it.contains("chargeTaken") }
        Assert.assertEquals(2, count)
    }
}