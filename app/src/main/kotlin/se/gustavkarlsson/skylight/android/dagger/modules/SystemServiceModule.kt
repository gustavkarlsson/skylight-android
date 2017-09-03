package se.gustavkarlsson.skylight.android.dagger.modules

import android.app.KeyguardManager
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
    fun provideNotificationManager(context: Context) = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Reusable
    fun provideConnectivityManager(context: Context) = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Reusable
    fun provideKeyguardManager(context: Context) = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

}
