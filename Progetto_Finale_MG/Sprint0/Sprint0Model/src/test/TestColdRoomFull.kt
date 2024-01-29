import junit.framework.Assert
import org.junit.BeforeClass
import org.junit.Test
import unibo.basicomm23.interfaces.IApplMessage
import unibo.basicomm23.interfaces.Interaction
import unibo.basicomm23.msg.ProtocolType
import unibo.basicomm23.utils.CommUtils
import unibo.basicomm23.utils.ConnectionFactory

class TestColdRoomFull {
    fun getPayloadArgs(input: String?): List<String> {
        val regex = Regex("\\(([^)]+)\\)")
        val matchResult = input?.let { regex.find(it) }

        return matchResult?.groupValues?.get(1)?.split(',') ?: emptyList()
    }
    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            it.unibo.ctxcoldstorageservice.main()
            CommUtils.delay(5000)
        }
    }

    @Test
    fun testColdRoomFull() {
        val conn: Interaction = ConnectionFactory.createClientSupport23(
            ProtocolType.tcp, "localhost", "9990")
        println("Sending store request of 200 kg")
        val storeRequest: IApplMessage = CommUtils.buildRequest("testApplication", "storerequest", "storerequest(200)", "fridgeservice")
        val storeReply: IApplMessage? = conn.request(storeRequest)

        //If the coldroom can't fit the load, it has to return loadrefused
        Assert.assertEquals("loadrefused", storeReply?.msgId())
    }
}