package se.gustavkarlsson.skylight.android.entities

data class AnimationConfig(
    val forward: Animations = Animations.NONE,
    val backward: Animations = Animations.NONE,
    val replace: Animations = Animations.NONE
)
