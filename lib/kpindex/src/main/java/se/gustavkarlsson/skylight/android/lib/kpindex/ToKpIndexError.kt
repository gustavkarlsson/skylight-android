package se.gustavkarlsson.skylight.android.lib.kpindex

import java.io.IOException

// TODO Fix duplication with weather module
internal fun Throwable.toKpIndexError(): KpIndexError =
    when (this) {
        is IOException -> KpIndexError.Connectivity
        is ServerResponseException -> KpIndexError.ServerResponse
        else -> KpIndexError.Unknown
    }
