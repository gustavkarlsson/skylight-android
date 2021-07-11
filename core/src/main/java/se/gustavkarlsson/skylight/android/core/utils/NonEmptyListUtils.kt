package se.gustavkarlsson.skylight.android.core.utils

import arrow.core.NonEmptyList
import arrow.core.Option

fun <T> List<T>.nonEmpty(): Option<NonEmptyList<T>> = NonEmptyList.fromList(this)

fun <T> List<T>.nonEmptyUnsafe(): NonEmptyList<T> = NonEmptyList.fromListUnsafe(this)
