import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // Plugin required for the Project
    alias(libs.plugins.googleServices) // Add the Google services Gradle plugin
    alias(libs.plugins.crashlytics) // Add the Crashlytics Gradle plugin
}

android {
    namespace = "mx.edu.utez.jyps"
    compileSdk = 36

    defaultConfig {
        applicationId = "mx.edu.utez.jyps"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "1.0.6"

        // Wallet Secure Environment Injection
        val keystoreProperties = Properties()
        val keystorePropertiesFile = rootProject.file("local.properties")
        if (keystorePropertiesFile.exists()) {
            keystoreProperties.load(keystorePropertiesFile.inputStream())
        }
        val rawWalletKey = keystoreProperties.getProperty("GOOGLE_WALLET_PRIVATE_KEY", "")
        val escapedWalletKey = rawWalletKey.replace("\n", "\\n").replace("\"", "\\\"")
        buildConfigField("String", "WALLET_KEY", "\"${escapedWalletKey}\"")
        buildConfigField("String", "WALLET_EMAIL", "\"${keystoreProperties.getProperty("GOOGLE_WALLET_CLIENT_EMAIL", "")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // TO RENAME THE APK
    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val appName = "JyPS" //
            val newName = "${appName}_${variant.versionName}.apk"
            output.outputFileName = newName
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    /* Dependencies required for the Project */
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.gson)
    // Coil to load images from URL
    implementation(libs.coil.compose)
//    implementation(libs.room.runtime)
//    implementation(libs.room.ktx)
//    ksp(libs.room.compiler)
    implementation(libs.androidx.sqlite)
    implementation(libs.androidx.sqlite.framework)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.compose.material.icons.extended)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    // Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.tink.android)
    implementation(libs.zxing.core)
    implementation(libs.play.services.pay)
    implementation(libs.java.jwt)

    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    /**/

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose) // Required for the ViewModel
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}