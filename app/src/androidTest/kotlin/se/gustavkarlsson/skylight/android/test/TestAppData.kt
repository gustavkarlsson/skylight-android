package se.gustavkarlsson.skylight.android.test

import android.content.Context
import org.apache.commons.io.FileUtils

fun getSharedPreferencesName(context: Context) = context.packageName + "_preferences_test"

fun clearSharedPreferences(context: Context) {
	context.getSharedPreferences(getSharedPreferencesName(context), Context.MODE_PRIVATE).edit().clear().commit()
}

fun clearCache(context: Context) {
	FileUtils.deleteDirectory(context.cacheDir)
}
