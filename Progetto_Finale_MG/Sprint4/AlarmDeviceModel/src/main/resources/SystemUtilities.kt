import org.json.simple.parser.JSONParser
import java.io.FileReader

enum class MoveType {
    MOVETOINDOOR,
    MOVETOCR,
    MOVETOHOME,
	WAITREQUEST,
	LOADCHARGE,
	STORECHARGE
}

class SystemUtilities {
    companion object {
        fun getPayloadArgs(input: String?): List<String> {
            val regex = Regex("\\(([^)]+)\\)")
            val matchResult = input?.let { regex.find(it) }
            return matchResult?.groupValues?.get(1)?.split(',') ?: emptyList() //argomenti del payload divisi dalla virgola
        }
        fun extractStringBeforeBracket(input: String): String {
            val indexOfBracket = input.indexOf("(")
            return if (indexOfBracket != -1) {
                input.substring(0, indexOfBracket)
            } else {
                input // Return the whole string if no bracket is found
            }
        }
        fun readJsonVariable(file: String, variable: String): Any?{
            val parser = JSONParser()
            val reader = FileReader(file)
            val jsonObject = parser.parse(reader) as org.json.simple.JSONObject

            val variable = jsonObject[variable]
            return variable
        }
    }
}