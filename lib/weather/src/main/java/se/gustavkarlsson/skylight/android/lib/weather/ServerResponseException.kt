package se.gustavkarlsson.skylight.android.lib.weather

// TODO Fix duplication with RetrofittedKpIndexProvider
internal class ServerResponseException(code: Int, body: String) : Exception("Server error $code. Body: $body")
