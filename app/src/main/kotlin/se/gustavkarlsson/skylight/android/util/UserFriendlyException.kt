package se.gustavkarlsson.skylight.android.util

import androidx.annotation.StringRes

class UserFriendlyException : RuntimeException {
    val stringResourceId: Int

    constructor(@StringRes stringResourceId: Int, cause: Throwable) : super(cause) {
        this.stringResourceId = stringResourceId
    }

    constructor(@StringRes stringResourceId: Int, message: String, cause: Throwable) : super(message, cause) {
        this.stringResourceId = stringResourceId
    }

    constructor(@StringRes stringResourceId: Int, message: String) : super(message) {
        this.stringResourceId = stringResourceId
    }

    constructor(@StringRes stringResourceId: Int) {
        this.stringResourceId = stringResourceId
    }
}
