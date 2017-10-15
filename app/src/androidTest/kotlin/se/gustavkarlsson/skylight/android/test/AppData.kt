package se.gustavkarlsson.skylight.android.test

import android.content.Context
import android.support.test.InstrumentationRegistry
import org.apache.commons.io.FileUtils

fun getSharedPreferencesName() = getContext().packageName + "_preferences_test"

fun clearSharedPreferences() {
	val context = getContext()
	context.getSharedPreferences(getSharedPreferencesName(), Context.MODE_PRIVATE).edit().clear().commit()
}

fun clearCache() {
	FileUtils.deleteDirectory(getContext().cacheDir)
}

private fun getContext(): Context = InstrumentationRegistry.getTargetContext()
