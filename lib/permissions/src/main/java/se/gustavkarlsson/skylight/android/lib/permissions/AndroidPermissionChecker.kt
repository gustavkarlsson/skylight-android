package se.gustavkarlsson.skylight.android.lib.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import se.gustavkarlsson.skylight.android.core.logging.logDebug

@ExperimentalCoroutinesApi
internal class AndroidPermissionChecker(
    private val permissionKey: String,
    private val context: Context,
    private val channel: ConflatedBroadcastChannel<Access>
) : PermissionChecker {

    @FlowPreview
    override val access: Flow<Access>
        get() {
            refresh()
            return channel
                .asFlow()
                .distinctUntilChanged()
        }

    override fun refresh() {
        val currentValue = channel.valueOrNull ?: Access.Unknown
        val systemPermission = checkSystemPermission()
        if (currentValue == Access.DeniedForever && systemPermission == Access.Denied) {
            logDebug { "Won't change from $currentValue to $systemPermission" }
            return
        }
        channel.offer(systemPermission)
    }

    private fun checkSystemPermission(): Access {
        val result = ContextCompat.checkSelfPermission(context, permissionKey)
        val access = if (result == PackageManager.PERMISSION_GRANTED)
            Access.Granted
        else
            Access.Denied
        logDebug { "$permissionKey = $access" }
        return access
    }
}
