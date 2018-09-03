package se.gustavkarlsson.skylight.android.gui.screens.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class PermissionViewModelFactory(
	private val store: SkylightStore
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		require(modelClass == CLASS) { "Unsupported ViewModel class: $modelClass, expected: $CLASS" }
		return PermissionViewModel(store) as T
	}

	companion object {
		private val CLASS = PermissionViewModel::class.java
	}
}

