package se.gustavkarlsson.skylight.android.gui.screens.main.drawer

import com.ioki.textref.TextRef

data class PlaceItem(val isActive: Boolean, val name: TextRef, val onClick: () -> Unit)
