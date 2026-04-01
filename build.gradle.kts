plugins {
    id("java")
    kotlin("jvm")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
// Optional: SLF4J (SQLite often looks for a logger to avoid warnings)
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.mnode.ical4j:ical4j:4.0.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(25)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}