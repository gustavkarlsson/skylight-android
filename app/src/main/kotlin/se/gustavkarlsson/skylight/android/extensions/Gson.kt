package se.gustavkarlsson.skylight.android.extensions

import com.google.gson.Gson

// Removes need to supply class parsing json
inline fun <reified T: Any> Gson.fromJson(json: String): T = this.fromJson(json, T::class.java)
