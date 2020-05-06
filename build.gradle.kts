import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.internal.impldep.org.joda.time.Instant
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.3.21"
val versionNo = "1.1"

project.group = "com.github.excitement-engineer"
project.version = versionNo


plugins {
    kotlin("jvm") version "1.3.71"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.1"

}

repositories {
    mavenCentral()
    jcenter()
    maven {
        setUrl("https://dl.bintray.com/excitement-engineer/ktor-graphql")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.7")

    testImplementation("com.github.excitement-engineer:ktor-graphql:0.2.1")
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-jetty:1.1.3")
    testImplementation("com.graphql-java:graphql-java-tools:3.2.0")
    testImplementation("junit:junit:4.11")
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.9.1")

}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val publicationName = "PubGraphQLKlient"

publishing {
    publications.invoke {
        create<MavenPublication>(publicationName) {
            from(components["java"])
        }
    }
}

fun findProperty(name: String) = project.findProperty(name) as String?

bintray {
    user = findProperty("bintrayUser")
    key = findProperty("bintrayApiKey")
    setPublications(publicationName)

    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = project.name
        name = project.name
        setLicenses("MIT")
        vcsUrl = "https://github.com/excitement-engineer/graphql-klient.git"

        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = versionNo
            desc = "An easy to use GraphQL client for Kotlin"
            vcsTag = versionNo
        })

    })
}
