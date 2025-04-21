plugins {
    `java-library`
    `maven-publish`
    id("io.github.0ffz.github-packages") version "1.2.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.13"
    id("io.github.goooler.shadow") version "8.1.7"
}

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")

    ivy {
        name="Github Releases" // GitHub Releases
        url=uri("https://github.com")

        patternLayout {
            artifact("[organisation]/[module]/releases/download/MC[revision]/[module]-[revision].[ext]")
        }

        metadataSources { artifact() }
    }

    ivy {
        name="Github Releases" // GitHub Releases (ExtraEvents API)
        url=uri("https://github.com")

        patternLayout {
            artifact("[organisation]/[module]/releases/download/[revision]/[module]-api-[revision].[ext]")
        }

        metadataSources { artifact() }
    }
}

dependencies {
    annotationProcessor("org.jetbrains:annotations-java5:24.1.0")
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    compileOnly("TTE-DevTeam:Movecraft:1.21.x-8.4.2-TTE@jar")
    compileOnly("it.unimi.dsi:fastutil:8.5.11")
    api("TTE-DevTeam:extraevents:1.0.0@jar")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

group = "net.countercraft.movecraft.combat"
version = "2.2.2"
description = "Movecraft-Combat-TTE"
java.toolchain.languageVersion = JavaLanguageVersion.of(21)


tasks.jar {
    archiveBaseName.set("Movecraft-Combat")
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.processResources {
    from(rootProject.file("LICENSE.md"))
    filesMatching("*.yml") {
        expand(mapOf("projectVersion" to project.version))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "net.countercraft.movecraft.combat"
            artifactId = "movecraft-combat"
            version = "${project.version}"

            artifact(tasks.jar)
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/apdevteam/movecraft-combat")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String)
        channel.set("Release")
        id.set("Airship-Pirates/Movecraft-Combat")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms {
            register(io.papermc.hangarpublishplugin.model.Platforms.PAPER) {
                jar.set(tasks.jar.flatMap { it.archiveFile })
                platformVersions.set(listOf("1.21.1-1.21.4"))
                dependencies {
                    hangar("Movecraft") {
                        required.set(true)
                    }
                }
            }
        }
    }
}
