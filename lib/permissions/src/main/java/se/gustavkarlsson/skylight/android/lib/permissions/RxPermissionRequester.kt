package se.gustavkarlsson.skylight.android.lib.permissions

import androidx.fragment.app.Fragment
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import se.gustavkarlsson.skylight.android.core.logging.logInfo

internal class RxPermissionRequester(
    private val permissionKeys: List<String>,
    private val accessChangeConsumer: (Access) -> Unit
) : PermissionRequester {

    override suspend fun request(fragment: Fragment) {
        val access = when (fragment.requestPermissions(permissionKeys)) {
            is PermissionResult.PermissionGranted -> Access.Granted
            is PermissionResult.PermissionDenied -> Access.Denied
            is PermissionResult.ShowRational -> Access.DeniedShowRationale
            is PermissionResult.PermissionDeniedPermanently -> Access.DeniedForever
        }
        logInfo { "Permission is $access" }
        accessChangeConsumer(access)
    }
}

private suspend fun Fragment.requestPermissions(permissionKeys: List<String>): PermissionResult {
    val permissionKeysArray = permissionKeys.toTypedArray()
    return PermissionManager.requestPermissions(this, REQUEST_ID, *permissionKeysArray)
}

private const val REQUEST_ID = 28345
