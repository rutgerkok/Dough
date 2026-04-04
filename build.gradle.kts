plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

group = "nl.rutgerkok"
version = "0.1-SNAPSHOT"
description = "DoughWorldGenerator"
java.sourceCompatibility = JavaVersion.VERSION_25

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/") // Paper
    }
}

dependencies {
    paperweight.paperDevBundle("26.1.1.build.14-alpha")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.processResources {
    filesMatching("**/*.yml") {
        expand("version" to project.version)
    }
}

tasks.test {
    useJUnitPlatform()
}