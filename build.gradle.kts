plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta12"
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

// Separate configuration for JARs that go into the fat driver JAR
val driverRuntime: Configuration by configurations.creating

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    driverRuntime("com.google.code.gson:gson:2.11.0")

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

// Build a fat JAR with Gson shaded in for the JDBC driver classloader
val fatDriverJar by tasks.registering(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
    archiveClassifier.set("driver")
    archiveBaseName.set("libsql-driver")
    from(sourceSets.main.map { it.output })
    configurations = listOf(driverRuntime)
    mergeServiceFiles()
}

tasks {
    prepareSandbox {
        dependsOn(fatDriverJar)
        doLast {
            val sandboxDir = defaultDestinationDirectory.get().asFile
            val pluginDir = sandboxDir.resolve("datagrip-libsql")
            val targetDir = pluginDir.resolve("datagrip-driver-libsql")
            targetDir.mkdirs()
            // Copy the fat driver JAR (includes Gson)
            val fat = fatDriverJar.get().archiveFile.get().asFile
            fat.copyTo(targetDir.resolve("libsql-driver.jar"), overwrite = true)
            // Write driver.xml
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
