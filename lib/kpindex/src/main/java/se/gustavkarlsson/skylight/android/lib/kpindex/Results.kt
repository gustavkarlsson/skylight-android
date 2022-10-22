package se.gustavkarlsson.skylight.android.lib.kpindex

import arrow.core.Either

typealias KpIndexResult = Either<KpIndexError, KpIndex>

typealias KpIndexForecastResult = Either<KpIndexError, KpIndexForecast>
