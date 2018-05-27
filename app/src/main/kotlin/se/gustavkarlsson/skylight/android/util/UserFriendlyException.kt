package se.gustavkarlsson.skylight.android.util

import android.support.annotation.IdRes

class UserFriendlyException : RuntimeException {
    val stringResourceId: Int

    constructor(@IdRes stringResourceId: Int, cause: Throwable) : super(cause) {
        this.stringResourceId = stringResourceId
    }

    constructor(@IdRes stringResourceId: Int, message: String, cause: Throwable) : super(message, cause) {
        this.stringResourceId = stringResourceId
    }

    constructor(@IdRes stringResourceId: Int, message: String) : super(message) {
        this.stringResourceId = stringResourceId
    }

    constructor(@IdRes stringResourceId: Int) {
        this.stringResourceId = stringResourceId
    }
}
