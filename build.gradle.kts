import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask
import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask.JarUrl
import groovy.lang.Closure

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.ben-manes.versions") version "0.41.0"
    id("dev.s7a.gradle.minecraft.server") version "1.2.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jmailen.kotlinter") version "3.8.0"
}

val gitVersion: Closure<String> by extra

val pluginVersion: String by project.ext
val apiVersion: String by project.ext

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    maven(url = "https://libraries.minecraft.net")
    maven(url = "https://jitpack.io")
    maven {
        url = uri("https://maven.pkg.github.com/tororo1066/TororoPluginAPI")
        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:$pluginVersion-R0.1-SNAPSHOT")
    compileOnly("tororo1066:commandapi:$apiVersion")
    compileOnly("tororo1066:base:$apiVersion")
    implementation("tororo1066:tororopluginapi:$apiVersion")
    compileOnly("com.mojang:brigadier:1.0.18")

    implementation(project(":API"))

    compileOnly("net.dv8tion:JDA:5.2.1")
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
}

tasks.named("build") {
    dependsOn("shadowJar")
}

task<LaunchMinecraftServerTask>("buildAndLaunchServer") {
    val dir = layout.buildDirectory.get().asFile
    dependsOn("shadowJar")
    doFirst {
        copy {
            from(dir.resolve("libs/${project.name}.jar"))
            into(dir.resolve("MinecraftServer/plugins"))
        }
    }

    jarUrl.set(JarUrl.Paper(pluginVersion))
    jarName.set("server.jar")
    serverDirectory.set(dir.resolve("MinecraftServer"))
    nogui.set(true)
    agreeEula.set(true)
}