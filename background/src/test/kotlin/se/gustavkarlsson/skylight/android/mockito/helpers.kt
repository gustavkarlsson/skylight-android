package se.gustavkarlsson.skylight.android.mockito

import org.mockito.Mockito

// Replacement for mockito's any() not working with kotlin
fun <T> any(): T {
	Mockito.any<T>()
	return null as T
}
