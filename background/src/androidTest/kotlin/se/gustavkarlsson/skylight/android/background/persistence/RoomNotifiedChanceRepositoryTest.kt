
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.background.persistence.BackgroundDatabase
import se.gustavkarlsson.skylight.android.background.persistence.RoomNotifiedChanceRepository
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.NotifiedChance

@RunWith(AndroidJUnit4::class)
class RoomNotifiedChanceRepositoryTest {
	private lateinit var db: BackgroundDatabase
	private lateinit var impl: RoomNotifiedChanceRepository

	private val value1 = NotifiedChance(Chance(3.0), Instant.ofEpochSecond(78253))
	private val value2 = NotifiedChance(Chance(1.0), Instant.ofEpochSecond(5000))

	@Before
	fun setUp() {
		val context = InstrumentationRegistry.getTargetContext()
		db = Room.inMemoryDatabaseBuilder(context, BackgroundDatabase::class.java).build()
		impl = RoomNotifiedChanceRepository(db)
	}

	@After
	fun tearDown() {
		db.close()
	}

	@Test
	fun getNonExistingIsNull() {
		val result: Any? = impl.get()

		assert(result).isNull()
	}

	@Test
	fun getExisting() {
		impl.insert(value1)

		val result = impl.get()

		assert(result).isEqualTo(value1)
	}

	@Test
	fun insertNewOverwritesOld() {
		impl.insert(value1)
		impl.insert(value2)

		val result = impl.get()

		assert(result).isEqualTo(value2)
	}
}
