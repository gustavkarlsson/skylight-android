package se.gustavkarlsson.skylight.android.services_impl.providers.kpindex

import io.reactivex.Single
import retrofit2.http.GET
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped

interface KpIndexService { // TODO resolve conflict with common library service
	@GET("kp-index")
	fun get(): Single<Timestamped<Float>>
}
