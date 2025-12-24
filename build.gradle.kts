plugins {
    id("java")
}

group = "nl.rutgerkok"
version = "0.1-SNAPSHOT"
description = "DoughWorldGenerator"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/") // Paper
    }
}

dependencies {
    implementation("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
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