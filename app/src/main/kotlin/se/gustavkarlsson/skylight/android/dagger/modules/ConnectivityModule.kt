package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import dagger.Module
import dagger.Provides
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.qualifiers.ConnectedToInternet
import se.gustavkarlsson.skylight.android.dagger.qualifiers.NotConnectedToInternet
import javax.inject.Singleton

@Module
class ConnectivityModule {

	@Provides
	@Singleton
	@ConnectedToInternet
	fun provideConnectivityFlowable(): Flowable<Boolean> {
		return ReactiveNetwork.observeInternetConnectivity()
			.toFlowable(BackpressureStrategy.LATEST)
	}

	@Provides
	@Singleton
	@NotConnectedToInternet
	fun provideNotConnectedToInternetMessage(context: Context): CharSequence {
		return context.getString(R.string.error_no_internet)
	}
}
