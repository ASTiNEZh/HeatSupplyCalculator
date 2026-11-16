plugins {
    id("java")
    application
}

group = "ru.ASTiNEZh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

tasks.test {
    useJUnitPlatform()
}

val mainClassName = "ru.ASTiNEZh.Main"

application {
    mainClass.set(mainClassName)
}

tasks.register<Exec>("packageExe") {
    group = "distribution"
    description = "Собирает приложение с exe и встроенной JRE (jpackage)"
    dependsOn(tasks.jar)

    val packageType = (findProperty("packageType") as String?) ?: "app-image"
    val console = findProperty("console") == "true"
    val jarName = tasks.jar.get().archiveFileName.get()
    val libsDir = layout.buildDirectory.dir("libs").get().asFile
    val outDir = layout.buildDirectory.dir("jpackage").get().asFile

    // JDK, которым компилируется проект (toolchain), а не тот, на котором запущен Gradle
    val jdkHome = javaToolchains.launcherFor(java.toolchain)
        .get().metadata.installationPath.asFile
    val exeSuffix = if (System.getProperty("os.name").startsWith("Windows")) ".exe" else ""

    doFirst { outDir.deleteRecursively() }

    val cmd = mutableListOf(
        File(jdkHome, "bin/jpackage$exeSuffix").absolutePath,
        "--type", packageType,
        "--name", "HeatSupplyCalculator",
        "--app-version", "1.0",
        "--input", libsDir.absolutePath,
        "--main-jar", jarName,
        "--main-class", mainClassName,
        "--dest", outDir.absolutePath
    )
    if (packageType != "app-image") {
        cmd += listOf("--win-menu", "--win-shortcut", "--win-dir-chooser")
    }
    if (console) {
        cmd += "--win-console"
    }
    commandLine(cmd)
}