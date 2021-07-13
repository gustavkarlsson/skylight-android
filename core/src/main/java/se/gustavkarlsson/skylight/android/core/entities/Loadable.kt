package se.gustavkarlsson.skylight.android.core.entities

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

typealias Loadable<T> = Option<T>
typealias Loading = None
typealias Loaded<T> = Some<T>
