package se.gustavkarlsson.skylight.android.extensions

import retrofit2.Retrofit

// Removes need to supply class when creating service
inline fun <reified T: Any> Retrofit.create(): T = this.create(T::class.java)
