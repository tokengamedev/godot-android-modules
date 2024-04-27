plugins {
    kotlin("jvm") version "1.9.10"
}

repositories {
    google()
    mavenCentral()
}
kotlin{
    jvmToolchain(17)
}

dependencies {
    implementation("com.android.tools.build:gradle-api:8.2.2")
    implementation(kotlin("stdlib"))
    gradleApi()
}
