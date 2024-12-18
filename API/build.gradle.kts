plugins {
    java
    `maven-publish`
}

group = "tororo1066"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations-java5:24.1.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    publications {
        create<MavenPublication>("plugin") {
            groupId = project.group.toString()
            artifactId = "man10-discord-authentication"
            version = System.getenv("VERSION")
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/tororo1066/Man10DiscordAuthentication")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}