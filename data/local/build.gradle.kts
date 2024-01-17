plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "io.github.mmolosay.thecolor.data.local"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    java {
        toolchain {
            val version = libs.versions.java.get().toInt()
            languageVersion.set(JavaLanguageVersion.of(version))
        }
    }
}

dependencies {
    implementation(project(":utils"))
    implementation(project(":domain"))

    // Room
    val roomVersion = libs.versions.room.get()
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}