package se.gustavkarlsson.skylight.android.lib.aurora

import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessForecast
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexForecastResult
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexResult
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.weather.WeatherForecastResult
import se.gustavkarlsson.skylight.android.lib.weather.WeatherResult

data class AuroraReport(
    val location: Location,
    val kpIndex: KpIndexResult,
    val geomagLocation: GeomagLocation,
    val darkness: Darkness,
    val weather: WeatherResult,
)

data class AuroraForecastReport(
    val location: Location,
    val kpIndex: KpIndexForecastResult,
    val geomagLocation: GeomagLocation,
    val darkness: DarknessForecast,
    val weather: WeatherForecastResult,
)
