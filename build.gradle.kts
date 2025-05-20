plugins {
    kotlin("jvm") version "2.1.20"
}

group = "edu.kit.ifv.mobitopp"
version = if (project.hasProperty("next-version")) {
    project.property("next-version") as String
} else {
    "0.0-SNAPSHOT"
}

tasks.wrapper {
    gradleVersion = "6.3"
}

repositories {
    maven { url = uri("https://nexus.ifv.kit.edu/repository/maven-releases/") }
    maven { url = uri("https://nexus.ifv.kit.edu/repository/maven-central/") }
    maven { url = uri("https://nexus.ifv.kit.edu/repository/maven-snapshots/") }
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("edu.kit.ifv.mobitopp:kotlin-units:1.2.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
