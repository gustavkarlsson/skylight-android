package se.gustavkarlsson.skylight.android.lib.navigation

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

internal data class NavKey(
	val name: String,
	val arguments: Map<String, String> = emptyMap(),
	val destination: String? = null
) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString()!!,
		parcel.readBundle(NavKey::class.java.classLoader)!!.toMap(),
		parcel.readString()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		with(parcel) {
			writeString(name)
			writeBundle(arguments.toBundle())
			writeString(destination)
		}
	}

	override fun describeContents() = 0

	companion object CREATOR : Parcelable.Creator<NavKey> {
		override fun createFromParcel(parcel: Parcel): NavKey = NavKey(parcel)
		override fun newArray(size: Int): Array<NavKey?> = arrayOfNulls(size)
	}
}

private fun Map<String, String>.toBundle(): Bundle =
	Bundle().apply { onEach { (k, v) -> putString(k, v) } }


private fun Bundle.toMap(): Map<String, String> =
	keySet().associateWith { getString(it) }
