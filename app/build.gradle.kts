plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.appbanhang"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.manager.appbanhang"
        minSdk = 23
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
}


dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-messaging:24.0.1")
    implementation(fileTree(mapOf(
        "dir" to "C:\\Users\\ZaloPay",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    //glider
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    //RxJava
    implementation("io.reactivex.rxjava3:rxjava:3.1.3")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")  // RxJava3 Adapter for Retrofit
    // bradge
    implementation ("com.nex3z:notification-badge:1.0.4")
    // even bus
    implementation ("org.greenrobot:eventbus:3.2.0")
    //Paper
    implementation ("io.github.pilgr:paperdb:2.7.2")
    // Gson
    implementation ("com.google.code.gson:gson:2.8.9")
    //lottie
    implementation ("com.airbnb.android:lottie:6.5.2")
    //neumorphism
    implementation ("com.github.fornewid:neumorphism:0.3.2")
    //image pick
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    // chart
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // Zalopay
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("commons-codec:commons-codec:1.14")
}


