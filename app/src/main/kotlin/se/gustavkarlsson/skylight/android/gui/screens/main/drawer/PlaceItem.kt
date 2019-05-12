package se.gustavkarlsson.skylight.android.gui.screens.main.drawer

import androidx.annotation.DrawableRes
import com.ioki.textref.TextRef

data class PlaceItem(
	val isActive: Boolean,
	@DrawableRes val icon: Int,
	val name: TextRef,
	val onClick: () -> Unit
)
