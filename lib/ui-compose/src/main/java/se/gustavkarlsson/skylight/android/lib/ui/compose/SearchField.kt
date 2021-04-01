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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
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
        state = SearchFieldState.Inactive,
        inactiveText = "Current location",
        placeholderText = "Enter your search term",
        onStateChanged = {},
    )
}

// TODO Fix flickering when focus changes
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    state: SearchFieldState,
    inactiveText: String,
    placeholderText: String,
    onStateChanged: (SearchFieldState) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = FocusRequester()
    var focused by remember { mutableStateOf(false) }
    val active = state is SearchFieldState.Active
    val activeText = (state as? SearchFieldState.Active)?.text
    SideEffect {
        if (active) {
            focusRequester.requestFocus()
        } else if (focused) {
            focusManager.clearFocus()
        }
    }
    TextField(
        modifier = modifier
            .onFocusChanged { focus ->
                focused = focus.isFocused
                val newState = if (focus.isFocused) {
                    SearchFieldState.Active(activeText ?: "")
                } else SearchFieldState.Inactive
                onStateChanged(newState)
            }
            .focusRequester(focusRequester),
        value = activeText ?: inactiveText,
        leadingIcon = {
            IconButton(
                enabled = active,
                onClick = { focusManager.clearFocus() }
            ) {
                Crossfade(targetState = active) { active ->
                    val imageVector = if (active) {
                        Icons.ArrowBack
                    } else Icons.Search
                    Icon(
                        imageVector = imageVector,
                        contentDescription = null,
                    )
                }
            }
        },
        trailingIcon = if (!activeText.isNullOrEmpty()) {
            {
                IconButton(
                    onClick = {
                        val newState = SearchFieldState.Active("")
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
            val newState = if (active) {
                SearchFieldState.Active(newText)
            } else SearchFieldState.Inactive
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
    object Inactive : SearchFieldState()
    data class Active(val text: String) : SearchFieldState()
}
