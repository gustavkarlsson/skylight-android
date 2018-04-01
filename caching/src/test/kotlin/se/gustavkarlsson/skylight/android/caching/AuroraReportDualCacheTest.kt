package se.gustavkarlsson.skylight.android.caching

import android.content.Context
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.AuroraReport


@RunWith(RobolectricTestRunner::class)
class AuroraReportDualCacheTest {

	lateinit var context: Context

	lateinit var impl: AuroraReportDualCache

    @Before
    fun setUp() {
        context = RuntimeEnvironment.application
		impl = AuroraReportDualCache(context, "id", DEFAULT)
    }

    @Test
    fun containsInitialDefaultValue() {
        assertThat(impl.value).isEqualTo(DEFAULT)
    }

    @Test
    fun containsNewValueAfterSettingIt() {
        impl.value = NEW

        assertThat(impl.value).isEqualTo(NEW)
    }

    @Test
    fun newInstanceWithSameIdIsDefaultedToOldInstanceValue() {
        val first = AuroraReportDualCache(context, "shared", DEFAULT)
        first.value = NEW

        val second = AuroraReportDualCache(
			context,
			"shared",
			DEFAULT)

		assertThat(second.value).isEqualTo(NEW)
    }

    @Test
    fun newInstanceWithDifferentIdIsDefaultedToDefault() {
		AuroraReportDualCache(context, "first", DEFAULT).value = NEW

        val second = AuroraReportDualCache(context, "second", DEFAULT)

        assertThat(second.value).isEqualTo(DEFAULT)
    }


	companion object {
        private val DEFAULT = AuroraReport.empty
		private val NEW = DEFAULT.copy(Instant.ofEpochMilli(684284948))
	}
}
