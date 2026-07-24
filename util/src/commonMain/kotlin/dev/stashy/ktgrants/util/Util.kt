package dev.stashy.ktgrants.util

private val uppercaseRegex = Regex("[A-Z]")
fun String.pascalToKebab(): String = uppercaseRegex.replace(this) {
    if (it.range.first == 0) it.value.lowercase()
    else "-${it.value.lowercase()}"
}

val componentRegex = Regex("[a-zA-Z0-9!@#$+\\-_/]|[*]")
inline fun verifyValue(value: String, message: () -> String) {
    require(componentRegex.containsMatchIn(value)) { "${message()} Received: \"$value\". Allowed pattern: ${componentRegex.pattern}" }
}
