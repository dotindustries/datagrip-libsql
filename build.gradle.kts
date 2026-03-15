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
                val stableName = when {
                    jar.name.startsWith("datagrip-libsql") -> "libsql-driver.jar"
                    jar.name.startsWith("gson") -> "gson.jar"
                    else -> jar.name
                }
                jar.copyTo(targetDir.resolve(stableName), overwrite = true)
            }
            targetDir.resolve("driver.xml").writeText("""<?xml version="1.0" encoding="UTF-8"?>
<drivers>
  <driver id="libsql" name="libSQL / Turso" driver-class="com.dotinc.libsql.LibSqlDriver" dialect="SQLITE">
    <url-template name="default" template="jdbc:libsql:{host::localhost}[:{port::8080}]"/>
    <url-template name="Turso Cloud" template="jdbc:libsql:https://{host}"/>
    <option name="auto-sync" value="true"/>
  </driver>
</drivers>
""")
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
