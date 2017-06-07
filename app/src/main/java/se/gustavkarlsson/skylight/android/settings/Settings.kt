package se.gustavkarlsson.skylight.android.settings

import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel

internal interface Settings {
    val isEnableNotifications: Boolean
    val triggerLevel: ChanceLevel
}
