plugins {
	id("com.android.library")
	id("kotlin-android")
	id("com.squareup.sqldelight")
}

dependencies {
	implementation(project(":core"))

	implementation("com.squareup.sqldelight:android-driver:${Versions.sqldelight}")
	implementation("com.squareup.sqldelight:rxjava2-extensions:${Versions.sqldelight}")
	implementation("de.halfbit:knot:${Versions.knot}")
}
