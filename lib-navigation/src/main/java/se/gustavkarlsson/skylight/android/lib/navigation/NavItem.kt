package se.gustavkarlsson.skylight.android.lib.navigation

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

data class NavItem(
	val name: String,                  // unique name for the screen
	val scope: String = "",            // name of the scope that this key belongs to
	val arguments: Bundle = Bundle()   // arguments for the screen
) : Parcelable {
	constructor(parcel: Parcel) : this(
		name = parcel.readString()!!,
		scope = parcel.readString()!!,
		arguments = parcel.readBundle(NavItem::class.java.classLoader)!!
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		with(parcel) {
			writeString(name)
			writeString(scope)
			writeBundle(arguments)
		}
	}

	override fun describeContents() = 0

	companion object CREATOR : Parcelable.Creator<NavItem> {
		override fun createFromParcel(parcel: Parcel): NavItem = NavItem(parcel)
		override fun newArray(size: Int): Array<NavItem?> = arrayOfNulls(size)
		val EMPTY: NavItem = NavItem("")
	}
}
