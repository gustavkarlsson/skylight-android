package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun PreviewLargeDialog() {
    LargeDialog(
        image = {
            Icon(
                modifier = Modifier.fillMaxSize(0.3f),
                imageVector = Icons.MyLocation,
                contentDescription = null,
                tint = Colors.onBackground,
            )
        },
        title = "Title",
        description = "This is the description",
        primaryActionText = "Primary action",
        onClickPrimaryAction = {},
        secondaryActionText = "Secondary action",
        onClickSecondaryAction = {},
    )
}

@Composable
fun LargeDialog(
    modifier: Modifier = Modifier,
    image: @Composable ColumnScope.() -> Unit,
    title: String,
    description: String,
    primaryActionText: String,
    onClickPrimaryAction: () -> Unit,
    secondaryActionText: String,
    onClickSecondaryAction: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        image()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = Typography.h5,
        )
        Spacer(modifier = Modifier.height(16.dp))
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
