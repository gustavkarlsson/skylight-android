package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.animation.Crossfade
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
        state = SearchFieldState.Unfocused,
        unfocusedText = "Current location",
        placeholderText = "Enter your search term",
        onStateChanged = {},
    )
}

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    state: SearchFieldState,
    unfocusedText: String,
    placeholderText: String,
    onStateChanged: (SearchFieldState) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = FocusRequester()
    val wasFocused = remember { mutableStateOf(false) }
    val focused = state is SearchFieldState.Focused
    val focusChanged = wasFocused.value != focused
    if (focusChanged) {
        SideEffect {
            if (focused) {
                focusRequester.requestFocus()
            } else {
                focusManager.clearFocus()
            }
        }
        wasFocused.value = focused
    }
    val focusedText = (state as? SearchFieldState.Focused)?.text
    TextField(
        modifier = modifier
            .onFocusChanged { focus ->
                val newState = if (focus.isFocused) {
                    SearchFieldState.Focused(focusedText ?: "")
                } else SearchFieldState.Unfocused
                onStateChanged(newState)
            }
            .focusRequester(focusRequester),
        value = focusedText ?: unfocusedText,
        leadingIcon = {
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
        trailingIcon = if (!focusedText.isNullOrEmpty()) {
            {
                IconButton(
                    onClick = {
                        val newState = SearchFieldState.Focused("")
                        onStateChanged(newState)
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
            val newState = if (focused) {
                SearchFieldState.Focused(newText)
            } else SearchFieldState.Unfocused
            onStateChanged(newState)
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
