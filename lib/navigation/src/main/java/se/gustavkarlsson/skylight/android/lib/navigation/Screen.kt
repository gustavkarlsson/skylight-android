package se.gustavkarlsson.skylight.android.lib.navigation

import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

interface Screen : Parcelable {
    // TODO Rename to ScreenType?
    val name: ScreenName

    // TODO Combine id and scopeStart in one? And make sure each is unique
    val id: String get() = "$name[${hashCode()}]"
    val scopeStart: String? get() = null

    // TODO Don't extend activity
    fun AppCompatActivity.onCreateDestroyScope(scope: CoroutineScope) = Unit
    fun AppCompatActivity.onStartStopScope(scope: CoroutineScope) = Unit
    fun AppCompatActivity.onResumePauseScope(scope: CoroutineScope) = Unit

    fun AppCompatActivity.onBackPress(): BackPress = BackPress.NOT_HANDLED

    @Composable
    fun AppCompatActivity.Content(tag: String, scope: CoroutineScope)
}

enum class BackPress {
    HANDLED, NOT_HANDLED
}
