plugins {
    id("java")
}

group = "com.victorchimenton"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation("net.dv8tion:JDA:5.2.1")
    implementation("com.mercadopago:sdk-java:2.1.29")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")
}