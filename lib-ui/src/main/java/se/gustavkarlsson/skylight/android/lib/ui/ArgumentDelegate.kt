package se.gustavkarlsson.skylight.android.lib.ui

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class ArgumentDelegate<T> : ReadWriteProperty<Fragment, T> {
	@Suppress("UNCHECKED_CAST")
	override fun getValue(thisRef: Fragment, property: KProperty<*>): T =
		thisRef.arguments?.get(property.name) as T

	override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
		val arguments = thisRef.arguments ?: Bundle().also { thisRef.arguments = it }
		arguments.putAll(bundleOf(property.name to value))
	}
}

fun <T> argument(): ReadWriteProperty<Fragment, T> = ArgumentDelegate()
