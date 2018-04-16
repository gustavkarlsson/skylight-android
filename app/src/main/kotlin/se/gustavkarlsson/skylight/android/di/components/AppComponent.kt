package se.gustavkarlsson.skylight.android.di.components

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import se.gustavkarlsson.skylight.android.background.di.components.BackgroundComponent
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.DarknessViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.GeomagLocationViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.KpIndexViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.VisibilityViewModel
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.Settings

interface AppComponent {
	val settings: Settings
	val analytics: Analytics

	fun auroraChanceViewModel(fragment: Fragment): AuroraChanceViewModel
	fun darknessViewModel(fragment: Fragment): DarknessViewModel
	fun geomagLocationViewModel(fragment: Fragment): GeomagLocationViewModel
	fun kpIndexViewModel(fragment: Fragment): KpIndexViewModel
	fun visibilityViewModel(fragment: Fragment): VisibilityViewModel
	fun mainViewModel(activity: FragmentActivity): MainViewModel

	val backgroundComponent: BackgroundComponent
}
