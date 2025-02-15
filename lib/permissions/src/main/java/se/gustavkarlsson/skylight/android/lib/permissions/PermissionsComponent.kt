package se.gustavkarlsson.skylight.android.lib.permissions

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import se.gustavkarlsson.skylight.android.core.CoreComponent
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER

@Component
@PermissionsScope
abstract class PermissionsComponent internal constructor(
    @Component internal val coreComponent: CoreComponent,
) {

    abstract val permissionChecker: PermissionChecker

    abstract val permissionRequester: PermissionRequester

    @Provides
    internal fun permissionChecker(impl: PermissionManager): PermissionChecker = impl

    @Provides
    internal fun permissionRequester(impl: PermissionManager): PermissionRequester = impl

    companion object {
        val instance: PermissionsComponent = PermissionsComponent::class.create(
            coreComponent = CoreComponent.instance,
        )
    }
}

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class PermissionsScope
