package se.gustavkarlsson.skylight.android.lib.ui

import android.os.Binder
import android.os.Bundle
import android.os.Parcelable
import androidx.core.app.BundleCompat
import androidx.fragment.app.Fragment
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class ArgumentDelegate<T> : ReadWriteProperty<Fragment, T> {
	@Suppress("UNCHECKED_CAST")
	override fun getValue(thisRef: Fragment, property: KProperty<*>): T =
		thisRef.arguments?.get(property.name) as T

	override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
		if (thisRef.arguments == null)
			thisRef.arguments = Bundle()

		val args = thisRef.arguments!!
		val key = property.name

		when (value) {
			null -> args.putString(key, null)
			is String -> args.putString(key, value)
			is Int -> args.putInt(key, value)
			is Short -> args.putShort(key, value)
			is Long -> args.putLong(key, value)
			is Byte -> args.putByte(key, value)
			is ByteArray -> args.putByteArray(key, value)
			is Char -> args.putChar(key, value)
			is CharArray -> args.putCharArray(key, value)
			is CharSequence -> args.putCharSequence(key, value)
			is Float -> args.putFloat(key, value)
			is Bundle -> args.putBundle(key, value)
			is Binder -> BundleCompat.putBinder(args, key, value)
			is Parcelable -> args.putParcelable(key, value)
			is Serializable -> args.putSerializable(key, value)
			else -> throw IllegalStateException(
				"Type ${(value as Any).javaClass.canonicalName}" +
					" of property ${property.name} is not supported"
			)
		}
	}
}

fun <T> argument(): ReadWriteProperty<Fragment, T> = ArgumentDelegate()
