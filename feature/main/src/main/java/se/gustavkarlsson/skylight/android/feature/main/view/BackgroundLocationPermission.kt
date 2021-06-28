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
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Image(
            modifier = Modifier.fillMaxSize(0.3f),
            imageVector = Icons.Default.MyLocation,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Colors.onBackground),
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Background location required", // FIXME String resource
            style = Typography.h5,
        )
        Spacer(modifier = Modifier.height(32.dp))
        val backgroundLocationName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            LocalContext.current.packageManager.backgroundPermissionOptionLabel.toString()
        } else {
            "Allow all the time" // FIXME String resource. SV: Tillåt alltid
        }
        Text(
            text = stringResource(R.string.background_location_permission_denied_message, backgroundLocationName),
            style = Typography.body1,
        )
        ExtendedFloatingActionButton(
            backgroundColor = Colors.primary,
            text = { Text(stringResource(R.string.open_settings)) }, // FIXME String resource
            onClick = onClickOpenSettings,
        )
        Text(
            text = "Don't want to allow background permission? You can turn off notifications for your current location.", // FIXME String resource
            style = Typography.body1,
        )
        TextButton(onClick = onClickTurnOffNotifications) {
            Text("Turn off notifications") // FIXME String resource
        }
    }
}
