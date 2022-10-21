package se.gustavkarlsson.skylight.android.core.utils

import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.toNonEmptyListOrNull
import arrow.core.toOption

fun <T> List<T>.nonEmpty(): Option<NonEmptyList<T>> = this.toNonEmptyListOrNull().toOption()

fun <T> List<T>.nonEmptyUnsafe(): NonEmptyList<T> =
    this.toNonEmptyListOrNull() ?: throw IndexOutOfBoundsException("Empty list")
