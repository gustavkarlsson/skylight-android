package se.gustavkarlsson.skylight.android.feature.background.notifications

import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.skylight.android.feature.background.persistence.LastNotificationRepository
import se.gustavkarlsson.skylight.android.feature.background.persistence.NotificationRecord

@Inject
internal class NotificationEvaluatorImpl(
    private val lastNotificationRepository: LastNotificationRepository,
    private val outdatedEvaluator: OutdatedEvaluator,
) : NotificationEvaluator {

    override suspend fun shouldNotify(notification: Notification): Boolean {
        val lastData = lastNotificationRepository.get() ?: return true
        if (lastData.isOutdated) return true
        return notification hasHigherChanceThan lastData
    }

    private val NotificationRecord.isOutdated: Boolean
        get() = outdatedEvaluator.isOutdated(timestamp)

    override suspend fun onNotified(notification: Notification) {
        lastNotificationRepository.insert(notification)
    }
}

private infix fun Notification.hasHigherChanceThan(old: NotificationRecord): Boolean {
    val oldAndNewChances = placesWithChance.map { new ->
        val correspondingOldChance = old.data
            .firstOrNull { old -> new.place.id == old.id }
            ?.chanceLevel
        correspondingOldChance to new.chanceLevel
    }
    return oldAndNewChances.any { (old, new) ->
        old == null || new > old
    }
}
