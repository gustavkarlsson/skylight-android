package se.gustavkarlsson.skylight.android.lib.geocoder

import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.PlaceSuggestion

internal class MapboxGeocoder(private val accessToken: String) :
	Geocoder {

	override fun geocode(locationName: String): Single<List<PlaceSuggestion>> {
		if (locationName.length <= 1)
			return Single.just(emptyList())

		return createSingle(createGeocoding(accessToken, locationName))
			.subscribeOn(Schedulers.io())
			.map(GeocodingResponse::toPlaceSuggestions)
	}
}

private fun GeocodingResponse.toPlaceSuggestions(): List<PlaceSuggestion> =
	features().mapNotNull { feature ->
		val center = feature.center()
		val placeName = feature.placeName() ?: feature.text()
		val text = feature.text() ?: feature.placeName()
		if (center == null || placeName == null || text == null)
			null
		else
			PlaceSuggestion(Location(center.latitude(), center.longitude()), placeName, text)
	}

private fun createGeocoding(accessToken: String, locationName: String): MapboxGeocoding =
	MapboxGeocoding.builder()
		.accessToken(accessToken)
		.autocomplete(true)
		.query(locationName)
		.build()

private fun createSingle(geocoding: MapboxGeocoding): Single<GeocodingResponse> =
	Single.create { emitter ->

		emitter.setCancellable(geocoding::cancelCall)

		geocoding.enqueueCall(object : Callback<GeocodingResponse> {
			override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) =
				emitter.onError(t)

			override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
				if (response.isSuccessful) {
					emitter.onSuccess(response.body()!!)
				} else {
					val code = response.code()
					val error = response.errorBody()?.string() ?: "<empty>"
					emitter.onError(Exception("HTTP $code: $error"))
				}
			}
		})
	}
