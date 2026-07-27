package dev.stashy.ktgrants.internal

private val uppercaseRegex = Regex("[A-Z]")

@KtgrantsInternalApi
fun String.pascalToKebab(): String = uppercaseRegex.replace(this) {
    if (it.range.first == 0) it.value.lowercase()
    else "-${it.value.lowercase()}"
}
