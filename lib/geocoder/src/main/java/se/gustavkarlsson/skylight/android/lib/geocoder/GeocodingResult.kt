package se.gustavkarlsson.skylight.android.lib.geocoder

import arrow.core.Either

typealias GeocodingResult = Either<GeocodingError, List<PlaceSuggestion>>
