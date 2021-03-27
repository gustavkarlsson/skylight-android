package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.animation.Crossfade
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
private fun PreviewSearchField() {
    SearchField(
        modifier = Modifier,
        text = "",
        unfocusedText = "Current location",
        placeholderText = "Enter your search term",
        onStateChanged = {},
    )
}

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    text: String,
    unfocusedText: String,
    placeholderText: String,
    onStateChanged: (SearchFieldState) -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = focused, key2 = text) {
        val state = if (focused) {
            SearchFieldState.Focused(text)
        } else SearchFieldState.Unfocused
        onStateChanged(state)
    }
    TextField(
        modifier = modifier.onFocusChanged { focus ->
            focused = focus.isFocused
        },
        value = if (focused) text else unfocusedText,
        leadingIcon = {
            val focusManager = LocalFocusManager.current
            IconButton(
                enabled = focused,
                onClick = { focusManager.clearFocus() }
            ) {
                Crossfade(targetState = focused) { focused ->
                    val imageVector = if (focused) {
                        Icons.ArrowBack
                    } else Icons.Search
                    Icon(
                        imageVector = imageVector,
                        contentDescription = null,
                    )
                }
            }
        },
        trailingIcon = if (focused && text.isNotEmpty()) {
            {
                IconButton(
                    onClick = {
                        val state = SearchFieldState.Focused("")
                        onStateChanged(state)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Close,
                        contentDescription = null,
                    )
                }
            }
        } else null,
        placeholder = {
            Text(placeholderText)
        },
        onValueChange = { newText ->
            val state = if (focused) {
                SearchFieldState.Focused(newText)
            } else SearchFieldState.Unfocused
            onStateChanged(state)
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Colors.secondary,
        ),
    )
}

sealed class SearchFieldState {
    object Unfocused : SearchFieldState()
    data class Focused(val text: String) : SearchFieldState()
}
