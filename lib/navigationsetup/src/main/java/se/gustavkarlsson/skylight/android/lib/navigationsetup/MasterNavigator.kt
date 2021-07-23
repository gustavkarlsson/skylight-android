package se.gustavkarlsson.skylight.android.lib.navigationsetup

import se.gustavkarlsson.skylight.android.lib.navigation.Navigator

interface MasterNavigator : Navigator {
    fun onBackPress()
}
