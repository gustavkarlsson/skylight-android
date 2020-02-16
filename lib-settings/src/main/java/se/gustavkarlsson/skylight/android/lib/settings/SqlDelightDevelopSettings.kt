package se.gustavkarlsson.skylight.android.lib.settings

import io.reactivex.Observable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.services.DevelopSettings

internal class SqlDelightDevelopSettings : DevelopSettings {
	override val overrideValues: Boolean
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
	override val overrideValuesChanges: Observable<Boolean>
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
	override val kpIndex: Double
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
	override val geomagLatitude: Double
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
	override val sunZenithAngle: Double
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
	override val cloudPercentage: Int
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
	override val refreshDuration: Duration
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

}
