package se.gustavkarlsson.skylight.android.services_impl.providers

import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.services.providers.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import se.gustavkarlsson.skylight.android.services.providers.VisibilityProvider
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

@RunWith(MockitoJUnitRunner::class)
class CombiningAuroraFactorsProviderTest {

	@Mock
	lateinit var mockKpIndexProvider: KpIndexProvider

	@Mock
	lateinit var mockVisibilityProvider: VisibilityProvider

	@Mock
	lateinit var mockDarknessProvider: DarknessProvider

	@Mock
	lateinit var mockGeomagLocationProvider: GeomagLocationProvider

	lateinit var time: Single<Instant>

	lateinit var location: Single<Location>

	lateinit var kpIndex: Single<KpIndex>

	lateinit var visibility: Single<Visibility>

	lateinit var darkness: Single<Darkness>

	lateinit var geomagLocation: Single<GeomagLocation>

	lateinit var impl: CombiningAuroraFactorsProvider

	@Before
	fun setUp() {
		time = Single.just(Instant.ofEpochSecond(950_000))
		location = Single.just(Location(5.5, 10.5))
		kpIndex = Single.just(KpIndex(4.5))
		visibility = Single.just(Visibility(50))
		darkness = Single.just(Darkness(30.0))
		geomagLocation = Single.just(GeomagLocation(55.3))
		whenever(mockKpIndexProvider.get()).thenReturn(kpIndex)
		whenever(mockVisibilityProvider.get(any())).thenReturn(visibility)
		whenever(mockDarknessProvider.get(any(), any())).thenReturn(darkness)
		whenever(mockGeomagLocationProvider.get(any())).thenReturn(geomagLocation)
		impl = CombiningAuroraFactorsProvider(
			mockKpIndexProvider,
			mockVisibilityProvider,
			mockDarknessProvider,
			mockGeomagLocationProvider)
	}

	@Test
	fun evaluatesFactorsConcurrently() {
		whenever(mockKpIndexProvider.get()).thenReturn(kpIndex.delayedBy100Millis())
		whenever(mockVisibilityProvider.get(any())).thenReturn(visibility.delayedBy100Millis())
		whenever(mockDarknessProvider.get(any(), any())).thenReturn(darkness.delayedBy100Millis())
		whenever(mockGeomagLocationProvider.get(any())).thenReturn(geomagLocation.delayedBy100Millis())

		val timeTakenMillis = measureTimeMillis {
			impl.get(time, location).blockingGet()
		}

		assertThat(timeTakenMillis).isBetween(100, 199)
	}

	// Test time is provided lazy

	private fun <T> Single<T>.delayedBy100Millis(): Single<T> = delay(100, TimeUnit.MILLISECONDS)
}
