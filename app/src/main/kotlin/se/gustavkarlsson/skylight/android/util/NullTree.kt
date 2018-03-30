package se.gustavkarlsson.skylight.android.util

import timber.log.Timber

class NullTree : Timber.Tree() {
	override fun log(priority: Int, tag: String?, message: String, t: Throwable?) = Unit
}
