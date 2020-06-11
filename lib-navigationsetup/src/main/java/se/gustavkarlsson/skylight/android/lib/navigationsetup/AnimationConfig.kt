package se.gustavkarlsson.skylight.android.lib.navigationsetup

data class AnimationConfig(
    val forward: Animations = Animations.NONE,
    val backward: Animations = Animations.NONE,
    val replace: Animations = Animations.NONE
)
