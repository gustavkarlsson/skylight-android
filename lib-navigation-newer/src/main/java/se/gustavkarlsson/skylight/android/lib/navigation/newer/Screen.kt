package se.gustavkarlsson.skylight.android.lib.navigation.newer

import android.os.Parcelable
import androidx.fragment.app.Fragment

interface Screen : Parcelable {
    val name: ScreenName
    val tag: String get() = "$name[${hashCode()}]"
    val scopeStart: String? get() = null
    fun createFragment(): Fragment
}
