package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography

@Preview
@Composable
private fun PreviewRequiresLocationPermission() {
    RequiresLocationPermission(
        title = "Title",
        description = "This is the description",
        primaryActionText = "Primary action",
        onClickPrimaryAction = {},
        secondaryActionText = "Secondary action",
        onClickSecondaryAction = {},
    )
}

@Composable
internal fun RequiresLocationPermission(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    primaryActionText: String,
    onClickPrimaryAction: () -> Unit,
    secondaryActionText: String,
    onClickSecondaryAction: () -> Unit,
) {
    // FIXME Improve layout
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = title,
            style = Typography.h5,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(description)
        Spacer(modifier = Modifier.weight(1f))
        ExtendedFloatingActionButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            backgroundColor = Colors.primary,
            text = { Text(primaryActionText) },
            onClick = onClickPrimaryAction,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = onClickSecondaryAction,
            content = { Text(secondaryActionText) },
        )
    }
}
