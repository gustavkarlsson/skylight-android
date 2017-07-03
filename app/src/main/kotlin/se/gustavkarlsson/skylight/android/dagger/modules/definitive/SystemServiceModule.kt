package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager

import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class SystemServiceModule {

    @Provides
    @Reusable
    fun provideNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Reusable
    fun provideConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

}
