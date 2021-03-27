package se.gustavkarlsson.skylight.android.feature.main

import androidx.compose.material.Colors
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons


internal data class ViewState(
    val toolbarTitleName: TextRef,
    val chanceLevelText: TextRef,
    val chanceSubtitleText: TextRef,
    val errorBannerData: BannerData?,
    val factorItems: List<FactorItem>,
    val search: SearchViewState,
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
    val valueTextColor: Colors.() -> Color,
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
    data class Open(val query: String, val searchResults: List<SearchResult>) : SearchViewState()
}

internal sealed class SearchResult {
    abstract val title: TextRef
    abstract val subtitle: TextRef?
    abstract val icon: ImageVector
    abstract val selected: Boolean

    data class Known(val place: Place, override val selected: Boolean) : SearchResult() {
        override val title: TextRef get() = place.name
        override val subtitle: Nothing? = null
        override val icon: ImageVector
            get() = when (place) {
                Place.Current -> Icons.MyLocation
                is Place.Favorite -> Icons.Star
                is Place.Recent -> Icons.History
            }
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
    }
}
