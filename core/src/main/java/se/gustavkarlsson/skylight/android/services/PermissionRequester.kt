package se.gustavkarlsson.skylight.android.services

import io.reactivex.Completable

interface PermissionRequester {
    fun request(): Completable
}
