plugins {
    id("java")
}

group = "org.example"
version = "0.0.1"

repositories {
    mavenCentral()
}

val vertxVersion = "4.4.2"

dependencies {
    implementation("io.vertx:vertx-core:$vertxVersion")
    implementation("io.vertx:vertx-web:$vertxVersion")
}
