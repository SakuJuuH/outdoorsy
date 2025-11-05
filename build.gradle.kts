// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    id("com.google.devtools.ksp") version "2.2.20-2.0.3" apply false
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        debug.set(false)
        android.set(true)
        verbose.set(true)
        ignoreFailures.set(false)
        filter {
            exclude("**/generated/**")
            include("**/*.kt")
        }
    }
}
