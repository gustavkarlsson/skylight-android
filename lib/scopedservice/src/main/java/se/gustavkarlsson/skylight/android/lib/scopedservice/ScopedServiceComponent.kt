package se.gustavkarlsson.skylight.android.lib.scopedservice

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER

@Component
@ScopedServiceScope
abstract class ScopedServiceComponent {

    abstract val serviceCatalog: ServiceCatalog

    abstract val serviceClearer: ServiceClearer

    @Provides
    internal fun serviceCatalog(impl: ServiceRegistry): ServiceCatalog = impl

    @Provides
    internal fun serviceClearer(impl: ServiceRegistry): ServiceClearer = impl

    // TODO Should this be a singleton? Should there be a way to save/restore state when activity is killed?
    companion object {
        val instance: ScopedServiceComponent = ScopedServiceComponent::class.create()
    }
}

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class ScopedServiceScope
