plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // Distinto del androidPackage del módulo Flutter
    // (com.demo.financial_simulator), o falla el merge de Dex.
    namespace = "com.demo.host_financiero"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.demo.host_financiero"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Solo para la demo: firmar release con la llave de debug.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    // Java 11 es lo que genera Android Studio. El paso 3 de la integración
    // lo sube a 17, que es lo que pide Flutter.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
}
