plugins {
    java
}

group = "de.purplekush"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Paper 26.3 (Minecraft Java 26.3)
    compileOnly("io.papermc.paper:paper-api:26.3.build.+")
}

java {
    // Paper 26.x benötigt Java 25
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.jar {
    archiveBaseName.set("PurpleKush-EliteMobs")
}
