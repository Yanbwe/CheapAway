group = "org.yanbwe"
version = "1.0.0"

subprojects {
    apply(plugin = "java")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.minecraftforge.net/")
    }
}
