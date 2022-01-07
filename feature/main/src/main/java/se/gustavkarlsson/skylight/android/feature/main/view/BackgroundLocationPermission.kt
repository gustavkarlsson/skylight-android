package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.LargeDialog
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef

@Composable
internal fun RequiresBackgroundLocationPermission(
    description: TextRef,
    onClickOpenSettings: () -> Unit,
    onClickTurnOffNotifications: () -> Unit,
) {
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
        description = textRef(description),
        primaryActionText = stringResource(R.string.open_settings),
        onClickPrimaryAction = onClickOpenSettings,
        secondaryActionText = stringResource(R.string.turn_off_notifications_instead),
        onClickSecondaryAction = onClickTurnOffNotifications,
    )
}
