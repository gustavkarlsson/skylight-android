package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
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
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    state: SearchFieldState,
    inactiveText: String,
    placeholderText: String,
    textStyle: TextStyle = LocalTextStyle.current,
    onStateChanged: (SearchFieldState) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = FocusRequester()
    var canClearFocus by remember { mutableStateOf(false) } // Prevents clearing focus when started as Inactive
    val active = state is SearchFieldState.Active
    val activeText = (state as? SearchFieldState.Active)?.text
    SideEffect {
        when {
            active -> {
                focusRequester.requestFocus()
                canClearFocus = true
            }
            canClearFocus -> focusManager.clearFocus(force = true)
        }
    }
    TextField(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focus ->
                val newState = if (focus.isFocused) {
                    SearchFieldState.Active(activeText ?: "")
                } else {
                    SearchFieldState.Inactive
                }
                onStateChanged(newState)
            },
        textStyle = textStyle,
        value = activeText ?: inactiveText,
        singleLine = true,
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
        } else {
            null
        },
        placeholder = {
            Text(placeholderText)
        },
        onValueChange = { newText ->
            val newState = if (active) {
                SearchFieldState.Active(newText)
            } else {
                SearchFieldState.Inactive
            }
            onStateChanged(newState)
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Colors.secondary,
        ),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        keyboardActions = KeyboardActions { keyboardController?.hide() },
    )
}

sealed interface SearchFieldState {
    object Inactive : SearchFieldState
    data class Active(val text: String) : SearchFieldState
}
