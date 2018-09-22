package se.gustavkarlsson.skylight.android.services_impl.providers.kpindex

import io.reactivex.Single
import retrofit2.http.GET

interface KpIndexApi {
	@GET("kp-index")
	fun get(): Single<DoubleHolder>
}
