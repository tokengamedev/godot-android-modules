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
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath(kotlin("gradle-plugin", "1.8.0"))
        gradleApi()

    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
