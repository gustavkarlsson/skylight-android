package se.gustavkarlsson.skylight.android.dagger.modules

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
    fun provideNotificationManager(context: Context): NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Reusable
    fun provideConnectivityManager(context: Context): ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

}
