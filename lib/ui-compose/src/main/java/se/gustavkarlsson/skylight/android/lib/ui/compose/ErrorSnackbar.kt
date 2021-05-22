package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ErrorSnackbar(
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
    actionOnNewLine: Boolean = false,
    shape: Shape = Shapes.small,
    elevation: Dp = 6.dp,
    content: @Composable () -> Unit,
) {
    Snackbar(
        modifier = modifier,
        action = action,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        backgroundColor = Colors.error,
        contentColor = Colors.onError,
        elevation = elevation,
        content = content,
    )
}

@Composable
fun ErrorSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = Shapes.small,
    elevation: Dp = 6.dp,
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        backgroundColor = Colors.error,
        contentColor = Colors.onError,
        actionColor = Colors.onError,
        elevation = elevation,
    )
}
