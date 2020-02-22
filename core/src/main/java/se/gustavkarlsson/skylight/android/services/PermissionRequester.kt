package se.gustavkarlsson.skylight.android.services

import androidx.fragment.app.Fragment
import io.reactivex.Completable
import se.gustavkarlsson.skylight.android.entities.Permission

interface PermissionRequester<T : Permission> {
    fun request(fragment: Fragment): Completable
}
