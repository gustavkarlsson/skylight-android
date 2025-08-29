package se.gustavkarlsson.skylight.android.feature.background.scheduling

fun interface BackgroundWork {
    suspend operator fun invoke()
}
