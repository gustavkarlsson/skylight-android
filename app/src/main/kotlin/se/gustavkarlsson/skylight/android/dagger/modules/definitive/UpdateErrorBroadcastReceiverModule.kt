package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.background.Updater
import se.gustavkarlsson.skylight.android.dagger.Names.UPDATE_ERROR_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope
import javax.inject.Named

@Module
class UpdateErrorBroadcastReceiverModule {

    @Provides
    @Named(UPDATE_ERROR_NAME)
    @ActivityScope
    fun provideUpdateErrorBroadcastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (Updater.RESPONSE_UPDATE_ERROR == action) {
                    val message = intent.getStringExtra(Updater.RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}
