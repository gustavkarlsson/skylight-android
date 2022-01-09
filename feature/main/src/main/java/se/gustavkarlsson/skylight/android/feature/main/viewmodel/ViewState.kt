package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
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

    data class RequiresBackgroundLocationPermission(
        val description: TextRef,
    ) : ViewState
}

internal sealed interface AppBarState {
    data class PlaceSelected(val title: TextRef) : AppBarState
    data class Searching(val query: String) : AppBarState
}

internal sealed interface ContentState {
    val dialog: DialogData?

    data class PlaceSelected(
        val chanceLevelText: TextRef,
        val errorBannerData: BannerData?,
        val notificationsButtonState: ToggleButtonState,
        val factorItems: List<FactorItem>,
        val onNotificationClickedEvent: Event,
    ) : ContentState {
        override val dialog: Nothing? = null
    }

    sealed interface Searching : ContentState {
        data class Ok(
            val searchResults: List<SearchResult>,
            override val dialog: DialogData?,
        ) : Searching

        data class Error(val text: TextRef) : Searching {
            override val dialog: Nothing? = null
        }
    }

    sealed interface RequiresLocationPermission : ContentState {
        override val dialog: Nothing? get() = null

        object UseDialog : RequiresLocationPermission
        object UseAppSettings : RequiresLocationPermission
    }

    object RequiresLocationService : ContentState {
        override val dialog: Nothing? = null
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

internal data class DialogData(
    val text: TextRef,
    val dismissEvent: Event,
    val confirmData: ButtonData,
    val cancelData: ButtonData,
)

internal data class ButtonData(
    val text: TextRef,
    val event: Event,
)

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
    val clickEvent: Event
    val longClickEvent: Event?

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
            override val clickEvent: Event get() = Event.ClickSearchResult(this)
            override val longClickEvent: Nothing? = null
        }

        data class Saved(
            val place: Place.Saved,
            override val selected: Boolean,
            private val notifications: Boolean,
        ) : Known {
            override val title: TextRef get() = TextRef.string(place.name)
            override val subtitle: Nothing? = null
            override val icon: ImageVector = Icons.History
            override val trailingIcon: ImageVector?
                get() = if (notifications) {
                    Icons.Notifications
                } else null
            override val clickEvent: Event get() = Event.ClickSearchResult(this)
            override val longClickEvent: Event get() = Event.LongClickSearchResult(this)
        }
    }

    data class New(
        val name: String,
        val details: String,
        val location: Location,
    ) : SearchResult {
        override val title: TextRef get() = TextRef.string(name)
        override val subtitle: TextRef get() = TextRef.string(details)
        override val icon: ImageVector = Icons.Search
        override val trailingIcon: Nothing? = null
        override val selected: Boolean = false
        override val clickEvent: Event get() = Event.ClickSearchResult(this)
        override val longClickEvent: Nothing? = null
    }
}

internal sealed interface Event {
    data class SetNotifications(val place: Place, val enabled: Boolean) : Event
    data class SearchChanged(val state: SearchFieldState) : Event
    data class ClickSearchResult(val result: SearchResult) : Event
    data class LongClickSearchResult(val result: SearchResult) : Event
    data class DeletePlace(val place: Place.Saved) : Event
    object CancelPlaceDeletion : Event
    object RefreshLocationPermission : Event
    object TurnOffCurrentLocationNotifications : Event
    object Noop : Event
}
