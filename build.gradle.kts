import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
    application
}

group = "de.zordid"
version = "1.0-SNAPSHOT"

val kotest = "5.7.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    //implementation("org.jetbrains.kotlinx:multik-core:0.2.0")
    //implementation("org.jetbrains.kotlinx:multik-default:0.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.reflections:reflections:0.9.12")
    implementation("com.github.ajalt.mordant:mordant:2.0.0-beta7")
    implementation("guru.nidi:graphviz-kotlin:0.18.1")
    implementation("org.slf4j:slf4j-nop:2.0.5")
    //implementation("ch.qos.logback", "logback-classic", "1.2.3")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotest")
    testImplementation("io.kotest:kotest-runner-junit5:$kotest")
    testImplementation("io.kotest:kotest-property:$kotest")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

application {
    mainClass.set("AdventOfCodeKt")
}
