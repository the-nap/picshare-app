plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.picshare.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.picshare.app"
        manifestPlaceholders["appAuthRedirectScheme"] = "com.picshare.app"

        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_URL", "\"https://amoeba-immense-macaw.ngrok-free.app/\"")
            buildConfigField("String", "REALM", "\"picshare-realm\"")
            buildConfigField("String", "CLIENT_ID", "\"picshare-app\"")
            buildConfigField("String", "REDIRECT_URI", "\"com.picshare.app:/oauth2redirec\"")
        }
        release {
            buildConfigField("String", "API_URL", "\"https://amoeba-immense-macaw.ngrok-free.app/\"")
            buildConfigField("String", "REALM", "\"picshare-realm\"")
            buildConfigField("String", "CLIENT_ID", "\"picshare-app\"")
            buildConfigField("String", "REDIRECT_URI", "\"com.picshare.app:/oauth2redirec\"")
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.hilt)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    implementation(libs.retrofit)
    implementation(libs.gson.converter)
    implementation(libs.appauth)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.security)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.coil)
    implementation(libs.navigation)
    implementation(libs.composeIcons.cssGg)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}