pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // Valor por defecto de Android Studio. El paso 4 de la integración lo
    // cambia a PREFER_SETTINGS y agrega el repo del engine de Flutter.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "HostFinanciero"
include(":app")
