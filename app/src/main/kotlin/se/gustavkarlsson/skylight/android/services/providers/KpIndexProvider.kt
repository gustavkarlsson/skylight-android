package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.KpIndex

interface KpIndexProvider {
	fun get(): Single<KpIndex>
}
