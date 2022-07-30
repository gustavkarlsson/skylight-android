package se.gustavkarlsson.skylight.android.lib.permissions

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@Module
@ContributesTo(AppScopeMarker::class)
object LibPermissionsModule {

    @Provides
    internal fun bindPermissionChecker(impl: PermissionManager): PermissionChecker = impl

    @Provides
    internal fun bindPermissionRequester(impl: PermissionManager): PermissionRequester = impl
}
