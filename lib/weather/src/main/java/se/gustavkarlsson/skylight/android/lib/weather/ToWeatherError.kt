package se.gustavkarlsson.skylight.android.lib.weather

import java.io.IOException

// TODO Fix duplication with kp index module
internal fun Throwable.toWeatherError(): WeatherError {
    return when (this) {
        is IOException -> WeatherError.Connectivity
        is ServerResponseException -> WeatherError.ServerResponse
        else -> WeatherError.Unknown
    }
}
