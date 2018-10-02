package se.gustavkarlsson.skylight.android.kpindex

import io.reactivex.Single
import retrofit2.http.GET

internal interface KpIndexApi {
	@GET("kp-index")
	fun get(): Single<DoubleHolder>
}
