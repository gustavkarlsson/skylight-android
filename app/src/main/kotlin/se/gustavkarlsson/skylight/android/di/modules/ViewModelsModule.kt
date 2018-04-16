package se.gustavkarlsson.skylight.android.di.modules

import android.support.v4.app.FragmentActivity
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.DarknessViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.GeomagLocationViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.KpIndexViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.VisibilityViewModel

interface ViewModelsModule {
	fun auroraChanceViewModel(activity: FragmentActivity): AuroraChanceViewModel
	fun darknessViewModel(activity: FragmentActivity): DarknessViewModel
	fun geomagLocationViewModel(activity: FragmentActivity): GeomagLocationViewModel
	fun kpIndexViewModel(activity: FragmentActivity): KpIndexViewModel
	fun visibilityViewModel(activity: FragmentActivity): VisibilityViewModel
	fun mainViewModel(activity: FragmentActivity): MainViewModel
}
