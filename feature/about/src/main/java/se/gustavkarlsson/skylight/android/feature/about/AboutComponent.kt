package se.gustavkarlsson.skylight.android.feature.about

import dagger.Component
import dagger.Module
import dagger.Provides
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.AppComponent
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import javax.inject.Named

@Component(
    modules = [AboutModule::class],
    dependencies = [
        AppComponent::class,
        TimeComponent::class
    ]
)
internal interface AboutComponent {
    fun viewModel(): AboutViewModel

    companion object {
        fun build(): AboutComponent =
            DaggerAboutComponent.builder()
                .appComponent(AppComponent.instance)
                .timeComponent(TimeComponent.instance)
                .build()
    }
}

@Module
internal object AboutModule {

    @Provides
    fun viewModel(
        time: Time,
        @Named("versionCode") versionCode: Int,
        @Named("versionName") versionName: String
    ): AboutViewModel = AboutViewModel(
        time = time,
        showDevelopData = BuildConfig.DEVELOP,
        versionCode = versionCode,
        versionName = versionName,
        gitBranch = BuildConfig.GIT_BRANCH,
        gitSha1 = BuildConfig.GIT_SHA1,
        buildTime = Instant.ofEpochMilli(BuildConfig.BUILD_TIME_MILLIS)
    )
}
