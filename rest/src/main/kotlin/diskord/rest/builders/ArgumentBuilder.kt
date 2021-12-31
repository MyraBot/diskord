package diskord.rest.builders

class ArgumentBuilder {
    val args = mutableMapOf<String, Any>()

    fun arg(key: String, value: Any) {
        args[key] = value
    }
}