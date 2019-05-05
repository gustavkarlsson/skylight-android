package se.gustavkarlsson.skylight.android.gui.utils

import androidx.fragment.app.FragmentActivity
import assertk.assert
import assertk.assertions.isEqualTo

fun FragmentActivity.assertBackStackCount(expected: Int) {
	assert(supportFragmentManager.backStackEntryCount).isEqualTo(expected)
}
