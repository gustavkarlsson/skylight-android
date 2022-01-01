package se.gustavkarlsson.skylight.android.lib.navigation

import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag

interface Screen : Parcelable {
    val type: Type
    val scopeStart: String? get() = null

    fun onBackPress(): BackPress = BackPress.NOT_HANDLED

    @Composable
    fun Content(activity: AppCompatActivity, tag: ServiceTag)

    enum class Type {
        Intro,
        GooglePlayServices,
        Main,
        About,
        Settings,
        PrivacyPolicy,
    }
}

enum class BackPress {
    HANDLED, NOT_HANDLED
}
