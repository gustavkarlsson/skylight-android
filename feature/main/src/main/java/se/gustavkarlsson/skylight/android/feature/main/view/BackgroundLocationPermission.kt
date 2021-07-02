package se.gustavkarlsson.skylight.android.feature.main.view

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.LargeDialog

@Composable
internal fun RequiresBackgroundLocationPermission(
    onClickOpenSettings: () -> Unit,
    onClickTurnOffNotifications: () -> Unit,
) {
    val backgroundLocationName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        LocalContext.current.packageManager.backgroundPermissionOptionLabel.toString()
    } else {
        stringResource(R.string.allow_all_the_time)
    }
    LargeDialog(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 32.dp)
            .systemBarsPadding(),
        image = {
            Icon(
                modifier = Modifier.fillMaxSize(0.3f),
                imageVector = Icons.MyLocation,
                contentDescription = null,
                tint = Colors.onBackground,
            )
        },
        title = stringResource(R.string.background_location_permission_required),
        description = stringResource(R.string.background_location_permission_denied_message, backgroundLocationName),
        primaryActionText = stringResource(R.string.open_settings),
        onClickPrimaryAction = onClickOpenSettings,
        secondaryActionText = stringResource(R.string.turn_off_notifications_instead),
        onClickSecondaryAction = onClickTurnOffNotifications,
    )
}
