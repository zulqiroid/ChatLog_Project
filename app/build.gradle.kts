plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.chatlog_project"
    compileSdk = 34
    buildFeatures {
        viewBinding = true
    }



    defaultConfig {
        applicationId = "com.example.chatlog_project"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.media3.common)
    implementation(libs.support.annotations)
    implementation(libs.firebase.config)
    implementation(libs.core.animation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.mukeshsolanki.android-otpview-pinview:otpview-compose:3.1.0")
    implementation("com.github.mukeshsolanki.android-otpview-pinview:otpview:3.1.0")
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("jp.wasabeef:picasso-transformations:2.4.0")
    implementation("com.github.pgreze:android-reactions:1.6")
    implementation("com.github.sharish:ShimmerRecyclerView:v1.3")
    implementation("com.github.3llomi:CircularStatusView:V1.0.3")
    implementation("com.github.pgreze:android-reactions:1.6") {
        exclude("com.android.support", "support-compat")
    }
    implementation("androidx.core:core:1.9.0") {
        exclude("com.android.support", "support-compat")
    }
    implementation ("com.github.OMARIHAMZA:StoryView:1.0.2-alpha")

    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-config")

    implementation("com.android.volley:volley:1.2.1")

    implementation("androidx.work:work-runtime-ktx:2.8.1") // Use the latest version

    implementation("com.jsibbold:zoomage:1.3.1")

    implementation ("com.google.android.material:material:1.9.0") // Use the latest version

    implementation ("com.github.3llomi:RecordView:3.1.3")
    implementation ("com.airbnb.android:lottie:4.1.0")
    testImplementation ("junit:junit:4.+")
    implementation ("org.jitsi.react:jitsi-meet-sdk:+")

}