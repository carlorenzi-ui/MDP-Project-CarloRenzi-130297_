plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.0.13"
    application
}

application {
    mainClass.set("it.unicam.cs.mpgc.afc250713.view.Launcher")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "21.0.3"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")

    // Dipendenze esplicite per il Language Server dell'IDE
    implementation("org.openjfx:javafx-controls:21.0.3")
    implementation("org.openjfx:javafx-graphics:21.0.3")
    implementation("org.openjfx:javafx-base:21.0.3")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
