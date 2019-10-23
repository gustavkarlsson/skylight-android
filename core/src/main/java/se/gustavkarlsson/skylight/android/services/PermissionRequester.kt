package se.gustavkarlsson.skylight.android.services

import io.reactivex.Completable
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Permission

interface PermissionRequester {
	fun request(): Completable
}
