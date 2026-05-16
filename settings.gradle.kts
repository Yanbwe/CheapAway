pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases/")
    }
}

rootProject.name = "CheapAway"

include(
    "common",
    "forge-1.20.1",
    "neoforge-1.21.1",
    "neoforge-26.1"
)
