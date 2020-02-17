package se.gustavkarlsson.skylight.android.lib.navigation

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.bundleOf

data class NavItem(
    val name: String, // unique name for the screen
    val scope: String = "", // name of the scope that this key belongs to
    val arguments: Bundle = Bundle() // arguments for the screen
) : Parcelable {
    constructor(
        name: String,
        scope: String = "",
        arguments: ArgumentsBuilder.() -> Unit
    ) : this(name, scope, bundleOf(*ArgumentsBuilder().apply(arguments).build()))

    internal constructor(parcel: Parcel) : this(
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
        internal val EMPTY: NavItem = NavItem("")
    }
}

class ArgumentsBuilder internal constructor() {
    private val arguments = mutableListOf<Pair<String, Any?>>()

    infix fun String.to(value: Any?) {
        arguments += Pair(this, value)
    }

    fun build(): Array<Pair<String, Any?>> = arguments.toTypedArray()
}
