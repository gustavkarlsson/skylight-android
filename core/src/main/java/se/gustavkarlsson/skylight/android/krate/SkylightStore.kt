package se.gustavkarlsson.skylight.android.krate

import se.gustavkarlsson.krate.core.Store

// FIXME move this to main module, and make sure background can still fetch reports somehow
typealias SkylightStore = Store<State, Command, Result>
