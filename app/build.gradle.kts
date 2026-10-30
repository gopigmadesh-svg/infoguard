plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.myapplication"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")

    // ML Kit Text Recognition
    implementation("com.google.mlkit:text-recognition:16.0.1")

    // Coil Compose for Image URI rendering
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Networking: OkHttp, Gson & Retrofit
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

// Automatically configure ADB reverse port forwarding for XAMPP Apache
@UntrackedTask(because = "Interacts dynamically with attached USB device via ADB")
abstract class EnsureAdbReverseTask : DefaultTask() {
    @TaskAction
    fun run() {
        val userHome = System.getProperty("user.home")
        val adb = File(userHome, "AppData/Local/Android/Sdk/platform-tools/adb.exe")
        if (adb.exists()) {
            try {
                val devicesProcess = ProcessBuilder(adb.absolutePath, "devices").start()
                val devicesOutput = devicesProcess.inputStream.bufferedReader().readText()
                devicesProcess.waitFor()
                val hasDevice = devicesOutput.lines().any { line ->
                    line.trim().isNotEmpty() &&
                    !line.startsWith("List of devices") &&
                    line.contains("\tdevice")
                }
                if (hasDevice) {
                    val reverseProcess = ProcessBuilder(adb.absolutePath, "reverse", "tcp:80", "tcp:80").start()
                    reverseProcess.waitFor()
                    println(">> [TruthLens] ADB reverse port forwarding active: tcp:80 -> tcp:80")
                }
            } catch (e: Exception) {
                println(">> [TruthLens] ADB reverse notice: ${e.message}")
            }
        }
    }
}

tasks.register<EnsureAdbReverseTask>("ensureAdbReverse") {
    group = "development"
    description = "Automatically configures ADB reverse port forwarding for XAMPP Apache (port 80)"
}

tasks.matching { it.name == "preBuild" || it.name.startsWith("install") }.configureEach {
    finalizedBy("ensureAdbReverse")
}

