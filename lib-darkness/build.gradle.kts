plugins {
	id("com.android.library")
	id("kotlin-android")
}

dependencies {
	implementation(project(":core"))

	implementation("net.e175.klaus:solarpositioning:${Versions.solarPositioning}")
}
