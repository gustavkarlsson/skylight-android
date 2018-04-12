package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator

interface EvaluationModule {
    val kpIndexEvaluator: ChanceEvaluator<KpIndex>
    val geomagLocationEvaluator: ChanceEvaluator<GeomagLocation>
    val visibilityEvaluator: ChanceEvaluator<Visibility>
    val darknessEvaluator: ChanceEvaluator<Darkness>
    val auroraReportEvaluator: ChanceEvaluator<AuroraReport>
}
