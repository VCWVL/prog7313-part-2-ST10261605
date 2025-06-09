plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.prog7313_part2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.prog7313_part2"
        minSdk = 26
        targetSdk = 35
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
}

dependencies {

    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.recyclerview:recyclerview:1.3.1")
    //firebase
    //code attribute:
    //https://firebase.google.com/docs/android/setup (accessed: 8 June 2025)
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation ("com.google.firebase:firebase-analytics-ktx")

    // For pie chart
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

//    //roomDb components
//    implementation ("androidx.room:room-runtime:2.5.2")
//    //adding kapt for roomDb
//    kapt ("androidx.room:room-compiler:2.5.2")
//    implementation ("androidx.room:room-ktx:2.5.2")

    implementation(libs.androidx.lifecycle.viewmodel.android)
//    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}

//References or Code Attributions: https://chat.deepseek.com/a/chat/s/16bea1c9-0e2a-4b8e-9a9f-9b7f940eb8c4 ; DeepSeek ; May 2025