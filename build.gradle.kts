plugins {
    application
    java
}

group = "connect5plus"
version = "2.2.2"

val generateVersionClass = tasks.register("generateVersionClass") {
    val versionValue = project.version.toString()
    val outputDirectory = layout.buildDirectory.dir("generated/sources/version/java/connect5plus")
    val outputFile = outputDirectory.map { it.file("Version.java") }

    inputs.property("version", versionValue)
    outputs.file(outputFile)

    doLast {
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            package connect5plus;

            public final class Version {
                public static final String VERSION = "$versionValue";

                private Version() {
                }
            }
            """.trimIndent() + System.lineSeparator(),
        )
    }
}

sourceSets.main {
    java.srcDir(layout.buildDirectory.dir("generated/sources/version/java"))
}

tasks.compileJava {
    dependsOn(generateVersionClass)
    options.encoding = "UTF-8"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(26))
    }
}

application {
    mainClass.set("connect5plus.Main")
    applicationDefaultJvmArgs = listOf(
        "-Dfile.encoding=UTF-8",
        "-Dstdout.encoding=UTF-8",
        "-Dstderr.encoding=UTF-8",
    )
}

tasks.named<JavaExec>("run") {
    jvmArgs("-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get(),
            "Implementation-Version" to project.version,
        )
    }
}
