package se.gustavkarlsson.skylight.android.lib.kpindex

import retrofit2.Response
import retrofit2.http.GET

internal interface KpIndexApi {
    @GET("kp-index")
    suspend fun getKpIndex(): Response<KpIndexBody>

    @GET("kp-index-forecast")
    suspend fun getKpIndexForecast(): Response<KpIndexForecastBody>
}
