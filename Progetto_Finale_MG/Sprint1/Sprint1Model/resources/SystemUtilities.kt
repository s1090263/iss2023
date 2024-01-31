enum class MoveType {
    MOVETOINDOOR,
    MOVETOCR,
    MOVETOHOME
}

class SystemUtilities {
    companion object {
        fun getPayloadArgs(input: String?): List<String> {
            val regex = Regex("\\(([^)]+)\\)")
            val matchResult = input?.let { regex.find(it) }
            return matchResult?.groupValues?.get(1)?.split(',') ?: emptyList() //argomenti del payload divisi dalla virgola
        }
    }
}