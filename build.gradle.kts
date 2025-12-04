import org.gradle.api.tasks.JavaExec

        tasks.named<JavaExec>("run") {
            standardInput = System.`in`
        }
plugins {
    id("java")
    application
}

group = "com.lior-karayev"
version = "1.0-SNAPSHOT"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21))}
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation ("com.mysql:mysql-connector-j:9.5.0")
}

application {
    mainClass.set("app.Main")
}

tasks.test {
    useJUnitPlatform()
}