package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.appcompat.app.AppCompatActivity
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator

interface MasterNavigator : Navigator {
    fun onBackPress(activity: AppCompatActivity)
}
