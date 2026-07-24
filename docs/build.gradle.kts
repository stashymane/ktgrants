plugins {
    alias(libs.plugins.dokka)
}

dependencies {
    dokka(projects.permissions)
    dokka(projects.authorization)
}

dokka {
    moduleName = "ktgrants"
}
