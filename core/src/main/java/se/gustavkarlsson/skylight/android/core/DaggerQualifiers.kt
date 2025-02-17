package se.gustavkarlsson.skylight.android.core

import me.tatarka.inject.annotations.Qualifier
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@Qualifier
@Target(PROPERTY_GETTER, FUNCTION, VALUE_PARAMETER, TYPE)
annotation class Main

@Qualifier
@Target(PROPERTY_GETTER, FUNCTION, VALUE_PARAMETER, TYPE)
annotation class Io

@Qualifier
@Target(PROPERTY_GETTER, FUNCTION, VALUE_PARAMETER, TYPE)
annotation class Computation

@Qualifier
@Target(PROPERTY_GETTER, FUNCTION, VALUE_PARAMETER, TYPE)
annotation class Global

@Qualifier
@Target(PROPERTY_GETTER, FUNCTION, VALUE_PARAMETER, TYPE)
annotation class VersionCode

@Qualifier
@Target(PROPERTY_GETTER, FUNCTION, VALUE_PARAMETER, TYPE)
annotation class VersionName
