package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import android.content.Context

import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class ContextModule(private val context: Context) {

    @Provides
    @Reusable
    fun provideContext(): Context {
        return context
    }
}
