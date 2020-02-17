plugins {
	id("com.android.library")
	id("kotlin-android")
	id("kotlin-android-extensions")
}

dependencies {
	implementation(project(":core"))
	implementation(project(":lib-ui"))

	implementation("com.google.android.gms:play-services-gcm:${Versions.playServicesGcm}")
	implementation("io.ashdavies.rx.rxtasks:rx-tasks:${Versions.rxtasks}")
}
