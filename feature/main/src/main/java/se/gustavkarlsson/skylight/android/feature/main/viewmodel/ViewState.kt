package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.core.R
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.ui.compose.SkylightColors
import se.gustavkarlsson.skylight.android.lib.ui.compose.ToggleButtonState

internal sealed interface ViewState {
    object Loading : ViewState

    data class Ready(
        val appBar: AppBarState,
        val content: ContentState,
    ) : ViewState

    object RequiresBackgroundLocationPermission : ViewState
}

internal sealed interface AppBarState {
    data class PlaceSelected(val title: TextRef) : AppBarState
    data class Searching(val query: String) : AppBarState
}

internal sealed interface ContentState {
    data class PlaceSelected(
        val chanceLevelText: TextRef,
        val errorBannerData: BannerData?,
        val notificationsButtonState: ToggleButtonState,
        val bookmarkButtonState: ToggleButtonState,
        val factorItems: List<FactorItem>,
        val onBookmarkClickedEvent: Event,
        val onNotificationClickedEvent: Event,
    ) : ContentState

    sealed interface Searching : ContentState {
        data class Ok(val searchResults: List<SearchResult>) : Searching
        data class Error(val text: TextRef) : Searching
    }

    sealed interface RequiresLocationPermission : ContentState {
        object UseDialog : RequiresLocationPermission
        object UseAppSettings : RequiresLocationPermission
    }
}

internal data class BannerData(
    val message: TextRef,
    val buttonText: TextRef,
    val icon: ImageVector,
    val buttonEvent: Event,
) {
    enum class Event {
        RequestBackgroundLocationPermission, OpenAppDetails
    }
}

internal data class FactorItem(
    val title: TextRef,
    val valueText: TextRef,
    val descriptionText: TextRef,
    val valueTextColor: SkylightColors.() -> Color,
    val progress: Double?,
    val errorText: TextRef?,
)

internal sealed interface SearchResult {
    val title: TextRef
    val subtitle: TextRef?
    val icon: ImageVector
    val trailingIcon: ImageVector?
    val selected: Boolean
    val selectEvent: Event

    sealed interface Known : SearchResult {
        data class Current(
            val name: String?,
            override val selected: Boolean,
            private val notifications: Boolean,
        ) : Known {
            override val title: TextRef
                get() {
                    return if (name != null) {
                        TextRef.string(name)
                    } else TextRef.stringRes(R.string.your_location)
                }
            override val subtitle: TextRef?
                get() {
                    return if (name != null) {
                        TextRef.stringRes(R.string.your_location)
                    } else null
                }
            override val icon = Icons.MyLocation
            override val trailingIcon: ImageVector?
                get() = if (notifications) {
                    Icons.Notifications
                } else null
            override val selectEvent: Event get() = Event.SelectSearchResult(this)
        }

        data class Saved(
            val place: Place.Saved,
            override val selected: Boolean,
            private val notifications: Boolean,
        ) : Known {
            override val title: TextRef get() = TextRef.string(place.name)
            override val subtitle: Nothing? = null
            override val icon: ImageVector
                get() = if (place.bookmarked) {
                    Icons.Bookmark
                } else Icons.History
            override val trailingIcon: ImageVector?
                get() = if (notifications) {
                    Icons.Notifications
                } else null
            override val selectEvent: Event get() = Event.SelectSearchResult(this)
        }
    }

    data class New(
        val name: String,
        val details: String,
        val location: Location,
    ) : SearchResult {
        override val title: TextRef get() = TextRef.string(name)
        override val subtitle: TextRef get() = TextRef.string(details)
        override val icon: ImageVector = Icons.Map
        override val trailingIcon: Nothing? = null
        override val selected: Boolean = false
        override val selectEvent: Event get() = Event.SelectSearchResult(this)
    }
}

internal sealed interface Event {
    data class AddBookmark(val place: Place.Saved) : Event
    data class RemoveBookmark(val place: Place.Saved) : Event
    data class EnableNotifications(val place: Place) : Event
    data class DisableNotifications(val place: Place) : Event
    data class SearchChanged(val state: SearchFieldState) : Event
    data class SelectSearchResult(val result: SearchResult) : Event
    object RefreshLocationPermission : Event
    object TurnOffCurrentLocationNotifications : Event
    object Noop : Event
}
