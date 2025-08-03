plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ls.diplomski.euterpe"
    compileSdk = 36

    defaultConfig {
        applicationId = "ls.diplomski.euterpe"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.navigation)
    implementation(libs.koin.workmanager)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.androidx.navigation.compose)
    val camerax_version = "1.3.0"
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")
    dependencies {
        // CameraX
        val camerax_version = "1.3.0"
        implementation("androidx.camera:camera-core:$camerax_version")
        implementation("androidx.camera:camera-camera2:$camerax_version")
        implementation("androidx.camera:camera-lifecycle:$camerax_version")
        implementation("androidx.camera:camera-view:$camerax_version")

        // Permissions handling
        implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")

        // Ktor Client
        implementation("io.ktor:ktor-client-android:2.3.7")
        implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
        implementation("io.ktor:ktor-serialization-gson:2.3.7")
        implementation("io.ktor:ktor-client-logging:2.3.7")

        // Activity Compose
        implementation("androidx.activity:activity-compose:1.8.2")
    }

}