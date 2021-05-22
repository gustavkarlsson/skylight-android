package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun ClickableText(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        modifier = modifier.clickable(
            role = Role.Button,
            onClick = onClick,
        ),
        color = Colors.primary,
        textDecoration = TextDecoration.Underline,
    )
}
