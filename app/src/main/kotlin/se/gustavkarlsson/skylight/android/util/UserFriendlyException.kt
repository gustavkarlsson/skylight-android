package se.gustavkarlsson.skylight.android.util

class UserFriendlyException : RuntimeException {
    val stringResourceId: Int

    constructor(stringResourceId: Int, cause: Throwable) : super(cause) {
        this.stringResourceId = stringResourceId
    }

    constructor(stringResourceId: Int, message: String, cause: Throwable) : super(message, cause) {
        this.stringResourceId = stringResourceId
    }

    constructor(stringResourceId: Int, message: String) : super(message) {
        this.stringResourceId = stringResourceId
    }

    constructor(stringResourceId: Int) {
        this.stringResourceId = stringResourceId
    }
}
