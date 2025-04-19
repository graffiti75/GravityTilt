plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.kotlin.serialization)
	id("com.google.dagger.hilt.android")
	id("com.google.devtools.ksp")
}

android {
	namespace = "com.cericatto.gravitytilt"
	compileSdk = 35

	defaultConfig {
		applicationId = "com.cericatto.gravitytilt"
		minSdk = 24
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
	buildFeatures {
		compose = true
	}
}

dependencies {

	// Compose
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)

	// Hilt
	implementation(libs.hilt.android)
	ksp(libs.hilt.android.compiler)
	implementation(libs.hilt.navigation.compose)

	// Retrofit
	implementation(libs.squareup.retrofit2.retrofit)
	implementation(libs.squareup.retrofit2.converter.gson)
	implementation(libs.squareup.retrofit2.converter.moshi)
	implementation(libs.squareup.okhttp3.logging.interceptor)
	implementation(libs.moshi.kotlin)
	ksp(libs.moshi.kotlin.codegen)

	// Navigation
	implementation(libs.navigation.compose)
	implementation(libs.kotlinx.serialization.json)

	// Tests
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}