package se.gustavkarlsson.skylight.android.lib.kpindex

import io.reactivex.Single
import retrofit2.http.GET

internal interface KpIndexApi {
    @GET("kp-index")
    fun get(): Single<KpIndexBody>
}
