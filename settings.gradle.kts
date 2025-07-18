rootProject.name = "Weaver"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(":core")
include(":tokenizer")
val apiDest = file("../polyfill/library")
if (apiDest.exists()) {
    include("polyfill")
    project(":polyfill").projectDir = apiDest
} else {
    println("This Project seems to be running without the Monorepo Context, please consider using the Monorepo")
}