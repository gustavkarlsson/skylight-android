package se.gustavkarlsson.skylight.android

import android.support.test.InstrumentationRegistry
import org.junit.Before
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.dagger.components.ApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.components.DaggerApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.modules.ContextModule

abstract class DaggerTest {
    @Before
    fun setUp() {
        Skylight.applicationComponent = createComponent()
    }

    private fun createComponent(): ApplicationComponent {
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Skylight
        return DaggerApplicationComponent.builder()
                .contextModule(ContextModule(app))
                .build()
    }
}
