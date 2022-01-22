package se.gustavkarlsson.skylight.android.lib.permissions

import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
object LibPermissionsModule {

    @Provides
    @Reusable
    internal fun bindPermissionChecker(impl: PermissionManager): PermissionChecker = impl

    @Provides
    @Reusable
    internal fun bindPermissionRequester(impl: PermissionManager): PermissionRequester = impl
}
