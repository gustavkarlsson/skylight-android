package se.gustavkarlsson.skylight.android.entities

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes

data class Animations(
    @AnimatorRes @AnimRes val enter: Int,
    @AnimatorRes @AnimRes val exit: Int,
    @AnimatorRes @AnimRes val popEnter: Int,
    @AnimatorRes @AnimRes val popExit: Int
) {
    constructor(
        @AnimatorRes @AnimRes enter: Int,
        @AnimatorRes @AnimRes exit: Int
    ) : this(enter, exit, 0, 0)

    companion object {
        val NONE =
            Animations(0, 0, 0, 0)
    }
}
