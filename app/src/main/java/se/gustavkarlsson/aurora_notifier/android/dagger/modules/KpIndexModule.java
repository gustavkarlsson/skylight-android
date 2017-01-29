package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedSolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService;

@Module
public abstract class KpIndexModule {

	@Provides
	@Reusable
	static KpIndexService provideKpIndexService() {
		return new Retrofit.Builder()
				// TODO Update to more permanent hostname
				.baseUrl("http://9698.s.t4vps.eu/rest/")
				.addConverterFactory(GsonConverterFactory.create())
				.build().create(KpIndexService.class);
	}

	@Binds
	abstract SolarActivityProvider provideSolarActivityProvider(RetrofittedSolarActivityProvider provider);
}
