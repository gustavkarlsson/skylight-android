package se.gustavkarlsson.skylight.android.services

import se.gustavkarlsson.skylight.android.services.evaluation.ChanceLevel

interface Settings {
    val isEnableNotifications: Boolean
    val triggerLevel: ChanceLevel
}
