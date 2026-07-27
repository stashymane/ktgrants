package dev.stashy.ktgrants

internal val componentRegex = Regex("[a-zA-Z0-9!@#$+\\-_/]|[*]")

internal inline fun verifyValue(value: String, message: () -> String) {
    require(componentRegex.containsMatchIn(value)) { "${message()} Received: \"$value\". Allowed pattern: ${componentRegex.pattern}" }
}
