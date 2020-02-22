package se.gustavkarlsson.skylight.android.feature.about

import dagger.Component
import dagger.Module
import dagger.Provides
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.services.Time
import javax.inject.Named

@Component(
    modules = [AboutModule::class],
    dependencies = [AppComponent::class]
)
internal interface AboutComponent {
    fun viewModel(): AboutViewModel

    companion object {
        fun viewModel(): AboutViewModel =
            DaggerAboutComponent.builder()
                .appComponent(appComponent)
                .build()
                .viewModel()
    }
}

@Module
internal class AboutModule {

    @Provides
    fun aboutViewModel(
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
