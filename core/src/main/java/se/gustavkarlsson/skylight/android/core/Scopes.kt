package se.gustavkarlsson.skylight.android.core

import me.tatarka.inject.annotations.Scope
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class ViewModelScope

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class CoreScope
