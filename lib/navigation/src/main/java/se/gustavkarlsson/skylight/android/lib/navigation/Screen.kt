package se.gustavkarlsson.skylight.android.lib.navigation

import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

interface Screen : Parcelable {
    val name: ScreenName
    val tag: String get() = "$name[${hashCode()}]"
    val scopeStart: String? get() = null

    fun AppCompatActivity.onCreateDestroyScope(scope: CoroutineScope) = Unit
    fun AppCompatActivity.onStartStopScope(scope: CoroutineScope) = Unit
    fun AppCompatActivity.onResumePauseScope(scope: CoroutineScope) = Unit

    fun AppCompatActivity.onBackPress(): BackPress = BackPress.NOT_HANDLED

    @Composable
    fun AppCompatActivity.Content(scope: CoroutineScope)
}

enum class BackPress {
    HANDLED, NOT_HANDLED
}
