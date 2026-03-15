plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.13.0"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")

    intellijPlatform {
        val platformType = providers.gradleProperty("platformType")
        val platformVersion = providers.gradleProperty("platformVersion")

        create(platformType, platformVersion)
        bundledPlugin("com.intellij.database")
        pluginVerifier()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

intellijPlatform {
    buildSearchableOptions = false
}

tasks {
    prepareSandbox {
        doLast {
            val sandboxDir = defaultDestinationDirectory.get().asFile
            val pluginDir = sandboxDir.resolve("datagrip-libsql")
            val targetDir = pluginDir.resolve("datagrip-driver-libsql")
            targetDir.mkdirs()
            pluginDir.resolve("lib").listFiles()?.filter { it.extension == "jar" }?.forEach { jar ->
                jar.copyTo(targetDir.resolve(jar.name), overwrite = true)
            }
        }
    }

    patchPluginXml {
        sinceBuild.set(providers.gradleProperty("pluginSinceBuild"))
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
