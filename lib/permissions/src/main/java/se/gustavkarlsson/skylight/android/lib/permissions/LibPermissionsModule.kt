package se.gustavkarlsson.skylight.android.lib.permissions

import dagger.Module
import dagger.Provides

@Module
object LibPermissionsModule {

    @Provides
    internal fun bindPermissionChecker(impl: PermissionManager): PermissionChecker = impl

    @Provides
    internal fun bindPermissionRequester(impl: PermissionManager): PermissionRequester = impl
}
