import junit.framework.Assert
import org.junit.BeforeClass
import org.junit.Test
import unibo.basicomm23.interfaces.IApplMessage
import unibo.basicomm23.interfaces.Interaction
import unibo.basicomm23.msg.ProtocolType
import unibo.basicomm23.utils.CommUtils
import unibo.basicomm23.utils.ConnectionFactory

class TestRequestAcceptedFull {
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
    fun testRequestAccepted() {
        val conn: Interaction = ConnectionFactory.createClientSupport23(
            ProtocolType.tcp, "localhost", "9990")
        println("Sending store request of 30 kg")
        val storeRequest: IApplMessage = CommUtils.buildRequest("testApplication", "storerequest", "storerequest(30)", "fridgeservice")
        val storeReply: IApplMessage? = conn.request(storeRequest)
        val ticket: String = getPayloadArgs(storeReply?.msgContent())[0]
        println("Simulating going to indoor")
        CommUtils.delay(4000)
        val ticketRequest: IApplMessage = CommUtils.buildRequest("testApplication", "sendticket", "sendticket("+ticket+")", "fridgeservice")
        val ticketReply: IApplMessage? = conn.request(ticketRequest)

        //If everything goes as it should, it will be chargetaken
        Assert.assertEquals("chargetaken", ticketReply?.msgId())
    }
}