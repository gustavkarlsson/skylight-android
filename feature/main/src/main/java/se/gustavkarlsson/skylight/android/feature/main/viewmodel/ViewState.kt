package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.ui.compose.SkylightColors
import se.gustavkarlsson.skylight.android.lib.ui.compose.ToggleButtonState


internal data class ViewState(
    val toolbarTitleName: TextRef,
    val chanceLevelText: TextRef,
    val chanceSubtitleText: TextRef,
    val errorBannerData: BannerData?,
    val notificationsButtonState: ToggleButtonState,
    val favoriteButtonState: ToggleButtonState,
    val factorItems: List<FactorItem>,
    val search: SearchViewState,
    val onFavoritesClickedEvent: Event,
    val notificationLevelItems: List<NotificationLevelItem>,
)

internal data class BannerData(
    val message: TextRef,
    val buttonText: TextRef,
    val icon: ImageVector,
    val buttonEvent: Event
) {
    enum class Event {
        RequestLocationPermission, OpenAppDetails
    }
}

internal data class FactorItem(
    val title: TextRef,
    val valueText: TextRef,
    val descriptionText: TextRef,
    val valueTextColor: SkylightColors.() -> Color,
    val progress: Double?,
    val errorText: TextRef?
) {
    companion object {
        fun loading(
            texts: ItemTexts,
        ): FactorItem {
            return FactorItem(
                title = TextRef.stringRes(texts.shortTitle),
                valueText = TextRef.string("…"),
                descriptionText = TextRef.stringRes(texts.description),
                valueTextColor = { onSurface.copy(alpha = 0.7F) },
                progress = null,
                errorText = null,
            )
        }
    }
}

internal sealed class SearchViewState {
    object Closed : SearchViewState()
    sealed class Open : SearchViewState() {
        abstract val query: String
        abstract val inProgress: Boolean

        data class Ok(
            override val query: String,
            override val inProgress: Boolean,
            val searchResults: List<SearchResult>,
        ) : Open()

        data class Error(
            override val query: String,
            override val inProgress: Boolean,
            val text: TextRef,
        ) : Open()
    }
}

internal sealed class SearchResult {
    abstract val title: TextRef
    abstract val subtitle: TextRef?
    abstract val icon: ImageVector
    abstract val selected: Boolean
    abstract val selectEvent: Event

    data class Known(val place: Place, override val selected: Boolean) : SearchResult() {
        override val title: TextRef get() = place.name
        override val subtitle: Nothing? = null
        override val icon: ImageVector
            get() = when (place) {
                Place.Current -> Icons.MyLocation
                is Place.Favorite -> Icons.Favorite
                is Place.Recent -> Icons.History
            }
        override val selectEvent: Event get() = Event.SelectSearchResult(this)
    }

    data class New(
        val name: String,
        val details: String,
        val location: Location,
    ) : SearchResult() {
        override val title: TextRef get() = TextRef.string(name)
        override val subtitle: TextRef get() = TextRef.string(details)
        override val icon: ImageVector = Icons.Map
        override val selected: Boolean = false
        override val selectEvent: Event get() = Event.SelectSearchResult(this)
    }
}

internal data class NotificationLevelItem(
    val text: TextRef,
    val selected: Boolean,
    val selectEvent: Event,
)

internal sealed class Event {
    data class AddFavorite(val place: Place.Recent) : Event()
    data class RemoveFavorite(val place: Place.Favorite) : Event()
    data class SetNotificationLevel(val place: Place, val level: TriggerLevel) : Event()
    data class SearchChanged(val state: SearchFieldState) : Event()
    data class SelectSearchResult(val result: SearchResult) : Event()
    object RefreshLocationPermission : Event()
    object Noop : Event()
}
