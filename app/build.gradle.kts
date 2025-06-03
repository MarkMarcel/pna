plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.marcel.pna"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.marcel.pna"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_20
        targetCompatibility = JavaVersion.VERSION_20
    }
    kotlinOptions {
        jvmTarget = "20"
    }
    kotlin {
        jvmToolchain(20) // MockK requires this
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    flavorDimensions += listOf("appType")
    productFlavors {
        create("app") {
            dimension = "appType"
        }
        create("componentsdemo") {
            dimension = "appType"
            applicationIdSuffix = ".components"
            versionNameSuffix = "- components"
        }
    }
}

dependencies {
    // APP
    // AndroidX
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.preview.debug)
    // Data
    implementation(libs.datastore.preferences)
    implementation(libs.room)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    // Dependency Injection
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.navigation)
    // Network
    implementation(libs.okhttp.logger)
    implementation(libs.moshi)
    ksp(libs.moshi.codegen)
    implementation(libs.moshi.converter)
    implementation(libs.retrofit)
    // PNA.M Components
    implementation(project(":components"))

    // TESTING
    // Coroutines
    testImplementation(libs.corountines.test)
    // JUnit
    testImplementation(libs.junit)
    // Koin
    testImplementation(libs.koin.test)
    // MockK
    testImplementation(libs.mockK)
}