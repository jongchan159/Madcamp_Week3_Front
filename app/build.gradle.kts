import java.util.regex.Pattern.compile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.project3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.project3"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(files("libs/oauth-5.9.1.aar"))
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

    // viewpager
    implementation (libs.androidx.viewpager2)

    // Network
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson.v290)
    implementation(libs.play.services.auth)
    implementation(libs.gson)

    // gif
    implementation (libs.glide)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    annotationProcessor (libs.compiler)

/*    // naver login
    implementation("com.navercorp.nid:oauth:5.9.1") // jdk 11*/

    implementation (libs.kotlin.stdlib)
    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.androidx.legacy.support.core.utils)
    implementation (libs.androidx.browser)
    implementation (libs.androidx.legacy.support.v4)
    implementation (libs.androidx.constraintlayout.v113)
    implementation (libs.androidx.security.crypto)
    implementation (libs.moshi.kotlin)
    implementation (libs.logging.interceptor.v421)
    implementation (libs.lottie)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}