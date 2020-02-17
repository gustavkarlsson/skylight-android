plugins {
	id("com.android.library")
	id("kotlin-android")
	id("kotlin-android-extensions")
}

dependencies {
	implementation(project(":core"))

	implementation("androidx.fragment:fragment:${Versions.androidFragment}")
	implementation("com.github.Zhuinden:simple-stack:${Versions.simpleStack}")
}
