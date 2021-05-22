package se.gustavkarlsson.skylight.android.feature.background.notifications

import se.gustavkarlsson.skylight.android.feature.background.persistence.LastNotificationRepository
import se.gustavkarlsson.skylight.android.feature.background.persistence.NotificationRecord

internal class NotificationEvaluatorImpl(
    private val lastNotificationRepository: LastNotificationRepository,
    private val outdatedEvaluator: OutdatedEvaluator
) : NotificationEvaluator {

    override fun shouldNotify(notification: Notification): Boolean {
        if (notification.data.isEmpty()) return false
        val lastData = lastNotificationRepository.get() ?: return true
        if (lastData.isOutdated) return true
        return notification hasHigherChanceThan lastData
    }

    private val NotificationRecord.isOutdated: Boolean
        get() = outdatedEvaluator.isOutdated(timestamp)

    override fun onNotified(notification: Notification) {
        lastNotificationRepository.insert(notification)
    }
}

private infix fun Notification.hasHigherChanceThan(old: NotificationRecord): Boolean {
    val oldAndNewChances = data.map { new ->
        val correspondingOldChance = old.data
            .firstOrNull { old -> new.place.id == old.id }
            ?.chanceLevel
        correspondingOldChance to new.chanceLevel
    }
    return oldAndNewChances.any { (old, new) ->
        old == null || new > old
    }
}
