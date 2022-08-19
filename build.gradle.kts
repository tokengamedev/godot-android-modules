import org.jetbrains.kotlin.ir.backend.js.lower.generateJsTests

// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins{
//    kotlin("kotlin-dsl")
//
//}
tasks.create<Delete>("clean") {
    delete(
        fileTree("bin"),
        fileTree("build")
    )
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath(kotlin("gradle-plugin", "1.6.21"))
        gradleApi()

    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
