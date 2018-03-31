package se.gustavkarlsson.skylight.android.services_impl.cache

import android.content.Context
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment


@RunWith(RobolectricTestRunner::class)
class DualSingletonCacheTest {

	lateinit var context: Context

	lateinit var impl: DualSingletonCache<String>

    @Before
    fun setUp() {
        context = RuntimeEnvironment.application
		impl = DualSingletonCache("id", DEFAULT, StringCacheSerializer(), context)
    }

    @Test
    fun containsInitialDefaultValue() {
        assertThat(impl.value).isEqualTo(DEFAULT)
    }

    @Test
    fun containsNewValueAfterSettingIt() {
        val newValue = "new"

        impl.value = newValue

        assertThat(impl.value).isEqualTo(newValue)
    }

    @Test
    fun newInstanceWithSameIdIsDefaultedToOldInstanceValue() {
        val newValue = "new"
        val first = DualSingletonCache("shared", DEFAULT, StringCacheSerializer(), context)
        first.value = newValue

        val second = DualSingletonCache("shared", DEFAULT, StringCacheSerializer(), context)

		assertThat(second.value).isEqualTo(newValue)
    }

    @Test
    fun newInstanceWithDifferentIdIsDefaultedToDefault() {
        DualSingletonCache("first", DEFAULT, StringCacheSerializer(), context).value = "one"

        val second = DualSingletonCache("second", DEFAULT, StringCacheSerializer(), context)

        assertThat(second.value).isEqualTo(DEFAULT)
    }


	companion object {
        private const val DEFAULT = "default"
	}
}

private class StringCacheSerializer : CacheSerializer<String> {

    override fun toString(data: String) = data

    override fun fromString(data: String) = data
}
