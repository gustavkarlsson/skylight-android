package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Banner(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    icon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope) -> Unit,
    text: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        contentColor = contentColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 24.dp,
                    start = 16.dp,
                    end = 8.dp,
                    bottom = 8.dp,
                ),
        ) {
            Row {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(16.dp))
                }
                text()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                actions(this)
            }
        }
    }
}
