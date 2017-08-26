package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context

import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class ContextModule(
        private val context: Context
) {

    @Provides
    @Reusable
    fun provideContext(): Context = context
}
