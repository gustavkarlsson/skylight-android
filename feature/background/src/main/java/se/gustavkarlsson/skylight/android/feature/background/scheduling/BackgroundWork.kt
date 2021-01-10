package se.gustavkarlsson.skylight.android.feature.background.scheduling

interface BackgroundWork {
    suspend operator fun invoke()
}
