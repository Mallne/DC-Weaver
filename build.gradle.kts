import nl.littlerobots.vcu.plugin.resolver.VersionSelectors

import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin

plugins {
    id("maven-publish")
    alias(libs.plugins.kmp) apply false
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.ben.manes.versions)
    alias(libs.plugins.dependency.analysis)
}

allprojects {
    apply {
        plugin("com.autonomousapps.dependency-analysis")
    }
}

versionCatalogUpdate {
    versionSelector(VersionSelectors.STABLE)
}

allprojects {
    plugins.withType<NodeJsPlugin> {
        extensions.configure<NodeJsEnvSpec> {
            download = false
        }
    }
}
