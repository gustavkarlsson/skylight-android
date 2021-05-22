package se.gustavkarlsson.skylight.android.lib.ui.compose

sealed class ToggleButtonState {
    abstract val visible: Boolean
    abstract val checked: Boolean

    object Gone : ToggleButtonState() {
        override val visible: Boolean = false
        override val checked: Boolean = false
    }

    data class Enabled(override val checked: Boolean) : ToggleButtonState() {
        override val visible: Boolean = true
    }
}
