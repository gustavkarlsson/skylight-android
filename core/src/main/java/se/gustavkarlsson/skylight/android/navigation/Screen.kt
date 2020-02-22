package se.gustavkarlsson.skylight.android.navigation

import android.os.Parcelable
import androidx.fragment.app.Fragment
import se.gustavkarlsson.skylight.android.navigation.ScreenName

interface Screen : Parcelable {
    val name: ScreenName
    val tag: String get() = "$name[${hashCode()}]"
    val scopeStart: String? get() = null
    fun createFragment(): Fragment
}
