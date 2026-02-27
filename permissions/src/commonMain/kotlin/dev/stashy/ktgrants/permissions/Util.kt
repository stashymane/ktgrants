package dev.stashy.ktgrants.permissions

private val uppercaseRegex = Regex("[A-Z]")
internal fun String.pascalToKebab(): String = uppercaseRegex.replace(this) {
    if (it.range.first == 0) it.value.lowercase()
    else "-${it.value.lowercase()}"
}

private val componentRegex = Regex("[a-zA-Z0-9!@#$+\\-_/]")
internal inline fun verifyValue(value: String, message: () -> String) {
    require(componentRegex.containsMatchIn(value)) { "${message()} Allowed pattern: ${componentRegex.pattern}" }
}
