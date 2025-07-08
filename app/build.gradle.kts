plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    // TAMBAHKAN DUA PLUGIN INI
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.firebyte.elearning"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.firebyte.elearning"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "2.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // Opsi Kotlin (bisa ditambahkan untuk konsistensi)
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // AndroidX & Material Design
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase BOM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")

    // Firebase UI untuk RecyclerView
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Utility Libraries
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // --- DEPENDENSI ROOM DATABASE ---
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion") // Menggunakan kapt
    implementation("androidx.room:room-ktx:$roomVersion")
    // ----------------------------
}