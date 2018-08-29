package se.gustavkarlsson.skylight.android.gui.screens.googleplayservices

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class GooglePlayServicesViewModelFactory(
	private val store: SkylightStore
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		require(modelClass == CLASS) { "Unsupported ViewModel class: $modelClass, expected: $CLASS" }
		return GooglePlayServicesViewModel(store) as T
	}

	companion object {
		private val CLASS = GooglePlayServicesViewModel::class.java
	}
}

