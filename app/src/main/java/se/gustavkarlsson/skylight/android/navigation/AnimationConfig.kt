package se.gustavkarlsson.skylight.android.navigation

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.lib.navigationsetup.AnimationConfig
import se.gustavkarlsson.skylight.android.lib.navigationsetup.Animations

internal val animationConfig =
    AnimationConfig(
        forward = Animations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        ),
        backward = Animations(
            R.anim.slide_in_left,
            R.anim.slide_out_right,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        ),
        replace = Animations(
            R.anim.fade_in,
            R.anim.fade_out
        )
    )
