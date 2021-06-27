package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import se.gustavkarlsson.skylight.android.feature.main.R

@Composable
internal fun RequiresBackgroundLocationPermission(onClickOpenSettings: () -> Unit) {
    Column {
        // FIXME build layout and fix string resources
        Text(stringResource(R.string.background_location_permission_denied_message))
        Text("To do this, open the settings screen and allow the background permission")
        Button(onClick = onClickOpenSettings) {
            Text(stringResource(R.string.open_settings))
        }
        Text("You can also disable notifications for the current place")
        Button(onClick = { /* FIXME disable notifications for current place */ }) {
            Text("Disable")
        }
    }
}
