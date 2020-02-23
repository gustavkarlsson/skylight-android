package se.gustavkarlsson.skylight.android.services

import androidx.fragment.app.Fragment
import io.reactivex.Completable

interface PermissionRequester {
    fun request(fragment: Fragment): Completable
}
