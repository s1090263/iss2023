import SystemUtilities.Companion.extractStringBeforeBracket
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

class TestStopResumeAlarm : CoapHandler {
    lateinit var client : CoapClient
    lateinit var obsRel : CoapObserveRelation
    lateinit var client2 : CoapClient
    lateinit var obsRel2 : CoapObserveRelation
    val host : String = "localhost"
    val port : Int = 9980
    val path : String = "ctxalarmdevice/alarmdevice"
    val path2: String = "ctxalarmdevice/warningdevice"

    val alarmContentList = mutableListOf<String>() //list of the content received by the observable resource
    val warningContentList = mutableListOf<String>() //list of the content received by the observable resource


    override fun onLoad(response: CoapResponse?) {
        var content: String? = response?.responseText

        if (content?.let { extractStringBeforeBracket(it) } == "alarmdevice"){
            var payload = getPayloadArgs(content)[0]
            alarmContentList.add(payload)
        }
        if (content?.let { extractStringBeforeBracket(it) } == "warningdevice"){
            var payload = getPayloadArgs(content)[0]
            warningContentList.add(payload)
        }
    }

    override fun onError() {
        println("TestRequestAcceptedFull - ERROR")
    }

    fun createObserverAlarm(host: String, port: Int, path: String){
        val url = host + ":" + port + "/" + path
        client = CoapClient("coap://" + url)
        obsRel = client.observe(this)
    }

    fun createObserverWarning(host: String, port: Int, path: String){
        val url = host + ":" + port + "/" + path
        client2 = CoapClient("coap://" + url)
        obsRel2 = client2.observe(this)
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            it.unibo.ctxalarmdevice.main()
            CommUtils.delay(5000)

        }
    }

    @Before
    fun initTest() {
        createObserverAlarm(host, port, path)
        createObserverWarning(host, port, path2)
    }

    @Test
    fun testStartStopAlarm() {

        //wait for the "nostop" response of the alarm
        var limit = 0
        while ( "nostop" !in alarmContentList && limit < 15){
            limit += 1
            CommUtils.delay(2000)
        }

        Assert.assertTrue("stop" in alarmContentList)
        Assert.assertTrue("resume" in alarmContentList)
        Assert.assertTrue("on" in warningContentList)
        Assert.assertTrue("nostop" in alarmContentList)

        //if everything goes as it should, the alarmContentlist should contain 1 time the stop message
        var count1 = alarmContentList.count { it == "stop" }
        Assert.assertEquals(1, count1)

        //if everything goes as it should, the alarmContentlist should contain 1 time the nostop message
        var count2 = alarmContentList.count { it.contains("nostop") }
        Assert.assertEquals(1, count2)

        //if everything goes as it should, the alarmContentlist should contain 1 time the resume message
        var count3 = alarmContentList.count { it.contains("resume") }
        Assert.assertEquals(1, count3)

        //if everything goes as it should, the warningContentlist should contain 1 time the on message
        var count4 = warningContentList.count { it.contains("on") }
        Assert.assertEquals(1, count4)

    }
}