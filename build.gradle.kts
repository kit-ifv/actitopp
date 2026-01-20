plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.9.10"
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
kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

repositories {
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("edu.kit.ifv.mobitopp:kotlin-units:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    api("edu.kit.ifv.mobitopp:discrete-choice:0.1.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

}



tasks.test {
    useJUnitPlatform()
}
tasks.dokkaHtml {
    outputDirectory.set(buildDir.resolve("dokka"))

    dokkaSourceSets {
        named("main") {
            includes.from("src/main/kotlin/com/example/mypackage/package.md") // optional
            perPackageOption {
                matchingRegex.set("com\\.example\\.internal.*")
                suppress.set(true) // Skip internal packages
            }
        }
    }
}


allprojects {
    apply(plugin = "maven-publish")

    project.group = "edu.kit.ifv.mobitopp"

    afterEvaluate {

        if (checkProperty("doPublish")) {
            /* mobiTopp publishing process (see .gitlab-ci.yml)
             * Parameters such as "doPublish" must be passed in gradle command:
             *  - ./gradlew <TASKS> publish -PdoPublish=true -Pparam=value...
             * Lookup of parameters doPublish and isRelease returns true if they are specified and their value reads "true".
             * Other required parameters must be specified, otherwise an error is thrown.
             *
             * The pipeline build version is used as the published artifacts version string.
             *  - uses parameter: "buildVersion"
             *
             * Every merge on main is published to local repo: see deploy-job
             *  - checks: doPublish=true, isRelease=false
             *  - requires parameters: "localUrl", "localRepoUser" and "localRepoPassword"
             *
             * Public releases must be published manually:
             *  - checks: doPublish=true, isRelease=true
             *  - requires parameters: "publicUrl", "publicRepoUser" and "publicRepoPassword"
             */

            project.version = requireProperty("buildVersion")
            println("Setup publishing configuration for ${group}:${project.name}:${version}.")

            publishing {

                publications {
                    register("mavenData", MavenPublication::class) {
                        from(components["kotlin"]) // For Kotlin projects
                        groupId = group.toString()
                        artifactId = project.name
                        version = project.version.toString()
                    }
                }

                repositories {
                    if (checkProperty("isRelease")) {
                        println("Activate: publish public release!")
                        println("WARNING: Public release still deactivated!")

                        //  Keep for first public release of reengineered mobitopp
                        //maven {
                        //    name = "PublicRepo"
                        //    url = uri(requireProperty("publicUrl"))
                        //    credentials {
                        //        username = requireProperty("publicRepoUser")
                        //        password = requireProperty("publicRepoPassword")
                        //    }
                        //}

                    } else {
                        println("Activate: publish local build!")
                        maven {
                            name = "LocalRepo"
                            url = uri(requireProperty("localUrl"))
                            credentials {
                                username = requireProperty("localRepoUser")
                                password = requireProperty("localRepoPassword")
                            }
                        }
                    }
                }

            }

        }

    }

}

fun requireProperty(property: String, orElse: String? = null): String =
    requireNotNull(project.findProperty(property) as? String ?: orElse) {
        "Could not find property '$property'. Please check the gradle command args. It should contain:\n" +
                "    ./gradlew ... -P$property=<VALUE> ..."
    }

fun checkProperty(property: String): Boolean = project.hasProperty(property) && project.property(property) == "true"

