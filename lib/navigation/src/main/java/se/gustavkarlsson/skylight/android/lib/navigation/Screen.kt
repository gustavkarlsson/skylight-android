package se.gustavkarlsson.skylight.android.lib.navigation

import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

interface Screen : Parcelable {
    val name: ScreenName
    val tag: String get() = "$name[${hashCode()}]"
    val scopeStart: String? get() = null

    fun AppCompatActivity.onNewCreateDestroyScope(scope: CoroutineScope) = Unit
    fun AppCompatActivity.onNewStartStopScope(scope: CoroutineScope) = Unit
    fun AppCompatActivity.onNewResumePauseScope(scope: CoroutineScope) = Unit

    fun AppCompatActivity.onBackPress(): BackPress = BackPress.NOT_HANDLED

    @Composable
    fun AppCompatActivity.Content()
}

enum class BackPress {
    HANDLED, NOT_HANDLED
}
