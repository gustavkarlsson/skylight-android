package se.gustavkarlsson.skylight.android

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.formatters.ChanceLevelFormatter
import se.gustavkarlsson.skylight.android.services.Formatter

@Module
class FormattersModule {

    @Provides
    @Reusable
    internal fun chanceLevelFormatter(): Formatter<ChanceLevel> = ChanceLevelFormatter
}
