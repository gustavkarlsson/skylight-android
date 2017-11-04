package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.SetupNotifications
import se.gustavkarlsson.skylight.android.actions_impl.SetupNotificationsFromObservable

@Module
abstract class SetupNotificationsModule {

    @Binds
    @Reusable
    abstract fun bindSetupNotifications(impl: SetupNotificationsFromObservable): SetupNotifications
}
