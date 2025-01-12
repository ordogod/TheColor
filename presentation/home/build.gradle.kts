plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "io.github.mmolosay.thecolor.presentation.home"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
    }

    java {
        toolchain {
            val version = libs.versions.java.get().toInt()
            languageVersion.set(JavaLanguageVersion.of(version))
        }
    }

    kapt {
        correctErrorTypes = true
    }
}

@Suppress("SpellCheckingInspection")
dependencies {
    // Modules
    implementation(project(":domain"))
    implementation(project(":utils"))
    implementation(project(":presentation:common:api"))
    implementation(project(":presentation:common:impl"))
    implementation(project(":presentation:design-system"))
    implementation(project(":presentation:color-input:api"))
    implementation(project(":presentation:color-input:impl"))
    implementation(project(":presentation:color-preview"))
    implementation(project(":presentation:color-center"))
    implementation(project(":presentation:color-details"))
    implementation(project(":presentation:color-scheme"))
    implementation(project(":presentation:settings"))

    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${libs.versions.coroutines.get()}")

    // Jetpack
    implementation("androidx.core:core-ktx:${libs.versions.androidx.core.coreKtx.get()}")
    implementation("androidx.appcompat:appcompat:${libs.versions.androidx.appcompat.get()}")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:${libs.versions.compose.bom.get()}")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended-android") // seems to use BOM version
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:${libs.versions.androidx.lifecycle.get()}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${libs.versions.androidx.lifecycle.get()}")
    implementation("androidx.hilt:hilt-navigation-compose:${libs.versions.hiltNavigationCompose.get()}")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${libs.versions.androidx.lifecycle.get()}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${libs.versions.androidx.lifecycle.get()}")

    // Hilt
    implementation("com.google.dagger:hilt-android:${libs.versions.hilt.get()}")
    kapt("com.google.dagger:hilt-compiler:${libs.versions.hilt.get()}")

    // Misc (preserve alphabetical order)
    implementation("com.jakewharton.timber:timber:${libs.versions.jakewhartonTimber.get()}")
    implementation("io.github.mmolosay:debounce:${libs.versions.mmolosayDebounce.get()}")

    // Testing
    testImplementation(project(":utils:testing"))
    testImplementation("junit:junit:${libs.versions.junit.get()}")
    testImplementation("io.mockk:mockk:${libs.versions.mockk.get()}")
    testImplementation("io.kotest:kotest-assertions-core:${libs.versions.kotestAssertions.get()}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${libs.versions.coroutines.get()}")
}