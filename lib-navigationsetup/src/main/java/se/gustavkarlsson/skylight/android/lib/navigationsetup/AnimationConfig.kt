package se.gustavkarlsson.skylight.android.lib.navigationsetup

import se.gustavkarlsson.skylight.android.entities.Animations

data class AnimationConfig(
    val forward: Animations = Animations.NONE,
    val backward: Animations = Animations.NONE,
    val replace: Animations = Animations.NONE
)
