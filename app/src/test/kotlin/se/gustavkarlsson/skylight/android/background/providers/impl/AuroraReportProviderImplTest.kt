package se.gustavkarlsson.skylight.android.background.providers.impl

import android.location.Address
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.background.providers.AsyncAddressProvider
import se.gustavkarlsson.skylight.android.background.providers.AuroraFactorsProvider
import se.gustavkarlsson.skylight.android.background.providers.LocationProvider
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.models.*
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import java.util.concurrent.Future

private val tenSeconds = Duration.ofSeconds(1)

@RunWith(RobolectricTestRunner::class)
class AuroraReportProviderImplTest {

	@Mock
	lateinit var networkInfo: NetworkInfo

	@Mock
	lateinit var connectivityManager: ConnectivityManager

	@Mock
	lateinit var locationProvider: LocationProvider

	@Mock
	lateinit var auroraFactorsProvider: AuroraFactorsProvider

	@Mock
	lateinit var addressFuture: Future<Address?>

	@Mock
	lateinit var asyncAddressProvider: AsyncAddressProvider

	@Mock
	lateinit var clock: Clock

	lateinit var auroraReportProvider: AuroraReportProviderImpl

	@Before
	fun setUp() {
		MockitoAnnotations.initMocks(this)
		`when`(connectivityManager.activeNetworkInfo).thenReturn(networkInfo)
		`when`(clock.instant()).thenReturn(Instant.EPOCH)
		`when`(networkInfo.isConnected).thenReturn(true)
		`when`(locationProvider.getLocation(any())).thenReturn(Location(""))
		`when`(addressFuture.get(anyLong(), any())).thenReturn(null)
		`when`(asyncAddressProvider.execute(anyDouble(), anyDouble())).thenReturn(addressFuture)
		`when`(auroraFactorsProvider.getAuroraFactors(any(), any())).thenReturn(AuroraFactors(GeomagActivity(), GeomagLocation(), Darkness(), Visibility()))
		auroraReportProvider = AuroraReportProviderImpl(connectivityManager, locationProvider, auroraFactorsProvider, asyncAddressProvider, clock)
	}

	@Test(timeout = 200)
	fun completesWithinTimeout() {
		val report = auroraReportProvider.getReport(Duration.ofMillis(100))
		assertThat(report).isNotNull()
	}

	@Test(expected = UserFriendlyException::class)
	fun noConnectivityThrowsException() {
		`when`(connectivityManager.activeNetworkInfo).thenReturn(null)
		auroraReportProvider.getReport(tenSeconds)
	}

	@Test(expected = UserFriendlyException::class)
	fun disconnectedThrowsException() {
		`when`(networkInfo.isConnected).thenReturn(false)
		auroraReportProvider.getReport(tenSeconds)
	}
}
