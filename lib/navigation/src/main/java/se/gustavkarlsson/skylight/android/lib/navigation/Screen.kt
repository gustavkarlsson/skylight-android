package se.gustavkarlsson.skylight.android.lib.navigation

import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag

interface Screen : Parcelable {
    val type: Type

    // TODO Combine id and scopeStart in one? And make sure each is unique
    val id: String get() = "$type[${hashCode()}]"
    val scopeStart: String? get() = null

    fun onCreateDestroyScope(activity: AppCompatActivity, scope: CoroutineScope) = Unit
    fun onStartStopScope(activity: AppCompatActivity, scope: CoroutineScope) = Unit
    fun onResumePauseScope(activity: AppCompatActivity, scope: CoroutineScope) = Unit

    fun onBackPress(): BackPress = BackPress.NOT_HANDLED

    @Composable
    fun Content(activity: AppCompatActivity, tag: ServiceTag, scope: CoroutineScope)

    enum class Type {
        Intro,
        GooglePlayServices,
        Main,
        About,
        PrivacyPolicy,
    }
}

enum class BackPress {
    HANDLED, NOT_HANDLED
}
