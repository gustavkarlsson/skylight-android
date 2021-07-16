package se.gustavkarlsson.skylight.android.lib.weather

import arrow.core.Either

typealias WeatherResult = Either<WeatherError, Weather>
