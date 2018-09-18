package se.gustavkarlsson.skylight.android.gui.screens.about

import androidx.lifecycle.ViewModel
import se.gustavkarlsson.skylight.android.BuildConfig

class AboutViewModel : ViewModel() {

	val branch: String = BuildConfig.GIT_BRANCH

	val versionName: String = BuildConfig.VERSION_NAME

	val versionCode: Int = BuildConfig.VERSION_CODE
}
