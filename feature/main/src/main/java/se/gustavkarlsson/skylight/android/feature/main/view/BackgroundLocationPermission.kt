package se.gustavkarlsson.skylight.android.feature.main.view

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography

@Composable
internal fun RequiresBackgroundLocationPermission(
    onClickOpenSettings: () -> Unit,
    onClickTurnOffNotifications: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding(),
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Image(
            modifier = Modifier
                .fillMaxSize(0.3f)
                .align(Alignment.CenterHorizontally),
            imageVector = Icons.Default.MyLocation,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Colors.onBackground),
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.background_location_permission_required),
            style = Typography.h5,
        )
        Spacer(modifier = Modifier.height(8.dp))
        val backgroundLocationName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            LocalContext.current.packageManager.backgroundPermissionOptionLabel.toString()
        } else {
            stringResource(R.string.allow_all_the_time)
        }
        Text(stringResource(R.string.background_location_permission_denied_message, backgroundLocationName))
        Spacer(modifier = Modifier.height(16.dp))
        ExtendedFloatingActionButton(
            backgroundColor = Colors.primary,
            text = { Text(stringResource(R.string.open_settings)) },
            onClick = onClickOpenSettings,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onClickTurnOffNotifications) {
            Text(stringResource(R.string.turn_off_notifications))
        }
    }
}
