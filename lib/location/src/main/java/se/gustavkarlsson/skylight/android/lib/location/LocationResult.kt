package se.gustavkarlsson.skylight.android.lib.location

import arrow.core.Either

typealias LocationResult = Either<LocationError, Location>

typealias LocationResultSuccess = Either.Right<Location>
