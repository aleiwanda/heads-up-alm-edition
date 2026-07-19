plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
//    id("com.google.dagger.hilt.android")
}

android {
    namespace = "at.alm.headsup"
    compileSdk {
        version = release(37) {
            minorApiLevel = 0
        }
    }

    defaultConfig {
        applicationId = "at.alm.headsup"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Android
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Other
    implementation(libs.spotify.auth)
    implementation(libs.jetty.client)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Debug
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Hilt
//    implementation(libs.hilt.android)
//    ksp(libs.hilt.android.compiler)

    // Room
//    ksp(libs.androidx.room.compiler)
//    implementation(libs.androidx.room.runtime)
//    annotationProcessor(libs.androidx.room.compiler)
//    implementation("androidx.room:room-ktx:$room_version")
//    implementation("androidx.room:room-rxjava2:$room_version")
//    implementation("androidx.room:room-rxjava3:$room_version")
//    implementation("androidx.room:room-guava:$room_version")
//    testImplementation("androidx.room:room-testing:$room_version")
//    implementation("androidx.room:room-paging:$room_version")
}