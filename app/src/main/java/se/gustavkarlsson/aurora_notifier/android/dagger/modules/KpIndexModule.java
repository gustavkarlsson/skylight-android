package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedGeomagActivityProvider;
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService;

@Module
public abstract class KpIndexModule {
	// TODO Update to more permanent hostname
	private static final String API_URL = "http://9698.s.t4vps.eu/rest/";

	@Provides
	@Reusable
	static KpIndexService provideKpIndexService() {
		return new Retrofit.Builder()
				.baseUrl(API_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build().create(KpIndexService.class);
	}

	@Binds
	@Reusable
	abstract GeomagActivityProvider bindGeomagActivityProvider(RetrofittedGeomagActivityProvider provider);
}
