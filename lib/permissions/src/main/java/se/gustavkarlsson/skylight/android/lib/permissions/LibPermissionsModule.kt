package se.gustavkarlsson.skylight.android.lib.permissions

import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
abstract class LibPermissionsModule {

    @Binds
    @Reusable
    internal abstract fun bindPermissionChecker(permissionManager: PermissionManager): PermissionChecker

    @Binds
    @Reusable
    internal abstract fun bindPermissionRequester(permissionManager: PermissionManager): PermissionRequester
}
