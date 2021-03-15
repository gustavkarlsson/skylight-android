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
fun SearchField(
    modifier: Modifier = Modifier,
    unfocusedText: String = "Current location",
    placeholderText: String = "Enter your search term",
    onTextChanged: (String?) -> Unit = {},
) {
    var state by remember { mutableStateOf<SearchFieldState>(SearchFieldState.Unfocused) }
    LaunchedEffect(key1 = state) {
        onTextChanged(state.text)
    }
    TextField(
        modifier = modifier.onFocusChanged { focus ->
            state = if (focus.isFocused) {
                SearchFieldState.Focused("")
            } else {
                SearchFieldState.Unfocused
            }
        },
        value = (state as? SearchFieldState.Focused)?.text ?: unfocusedText,
        leadingIcon = {
            val focusManager = LocalFocusManager.current
            IconButton(
                enabled = state.isFocused,
                onClick = { focusManager.clearFocus() }
            ) {
                Crossfade(targetState = state.isFocused) { isFocused ->
                    val imageVector = if (isFocused) {
                        Icons.ArrowBack
                    } else Icons.Search
                    Icon(
                        imageVector = imageVector,
                        contentDescription = null,
                    )
                }
            }
        },
        trailingIcon = if ((state as? SearchFieldState.Focused)?.text?.isEmpty() == false) {
            {
                IconButton(onClick = { state = SearchFieldState.Focused("") }) {
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
            state = SearchFieldState.Focused(newText)
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
    abstract val isFocused: Boolean
    abstract val text: String?

    object Unfocused : SearchFieldState() {
        override val isFocused: Boolean = false
        override val text: String? = null
    }

    data class Focused(override val text: String) : SearchFieldState() {
        override val isFocused: Boolean = true
    }
}
