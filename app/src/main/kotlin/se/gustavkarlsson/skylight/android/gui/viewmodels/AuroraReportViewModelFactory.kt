package se.gustavkarlsson.skylight.android.gui.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import dagger.Reusable
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.toLiveData
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceLevel
import java.util.*
import javax.inject.Inject

@Reusable
class AuroraReportViewModelFactory
@Inject
constructor(
	private val auroraReports: Observable<AuroraReport>,
	private val chanceEvaluator: ChanceEvaluator<AuroraReport>,
	private val kpIndexChaneEvaluator: ChanceEvaluator<KpIndex>,
	private val geomagLocationChanceEvaluator: ChanceEvaluator<GeomagLocation>,
	private val visibilityChanceEvaluator: ChanceEvaluator<Visibility>,
	private val darknessChanceEvaluator: ChanceEvaluator<Darkness>,
	private val context: Context
) : ViewModelProvider.Factory { // FIXME clean up

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		require(modelClass == CLASS) { "Unsupported ViewModel class: $modelClass, expected: $CLASS" }
		return auroraReports.let {
			val chanceLevel = createChanceLevel(it)
			val locationName = createLocationName(it)
			val timestamp = createTimestamp(it)
			val kpIndex = createKpIndex(it)
			val geomagLocation = createGeomagLocation(it)
			val visibility = createVisibility(it)
			val darkness = createDarkness(it)
			AuroraReportViewModel(
				chanceLevel,
				locationName,
				timestamp,
				kpIndex,
				geomagLocation,
				visibility,
				darkness
			) as T
		}
	}

	private fun createChanceLevel(auroraReport: Observable<AuroraReport>): LiveData<String> {
		return auroraReport
			.map(chanceEvaluator::evaluate)
			.map(ChanceLevel.Companion::fromChance)
			.map { context.getString(it.resourceId) }
			.toLiveData(BackpressureStrategy.LATEST)
	}

	private fun createLocationName(auroraReport: Observable<AuroraReport>): LiveData<String> {
		return auroraReport
			.observeOn(Schedulers.computation())
			.map { it.locationName ?: context.getString(R.string.your_location) }
			.toLiveData(BackpressureStrategy.LATEST)
	}

	private fun createTimestamp(auroraReport: Observable<AuroraReport>): LiveData<Instant> {
		return auroraReport
			.map(AuroraReport::timestamp)
			.toLiveData(BackpressureStrategy.LATEST)
	}

	private fun createKpIndex(auroraReport: Observable<AuroraReport>): LiveData<AuroraReportViewModel.Factor> {
		return auroraReport
			.map { it.factors.kpIndex }
			.map {
				val value = evaluateKpIndexText(it)
				val chance = kpIndexChaneEvaluator.evaluate(it)
				AuroraReportViewModel.Factor(value, chance)
			}
			.toLiveData(BackpressureStrategy.LATEST)
	}

	private fun createGeomagLocation(auroraReport: Observable<AuroraReport>): LiveData<AuroraReportViewModel.Factor> {
		return auroraReport
			.map { it.factors.geomagLocation }
			.map {
				val value = evaluateGeomagLocationText(it)
				val chance = geomagLocationChanceEvaluator.evaluate(it)
				AuroraReportViewModel.Factor(value, chance)
			}
			.toLiveData(BackpressureStrategy.LATEST)
	}

	private fun createVisibility(auroraReport: Observable<AuroraReport>): LiveData<AuroraReportViewModel.Factor> {
		return auroraReport
			.map { it.factors.visibility }
			.map {
				val value = evaluateVisibilityText(it)
				val chance = visibilityChanceEvaluator.evaluate(it)
				AuroraReportViewModel.Factor(value, chance)
			}
			.toLiveData(BackpressureStrategy.LATEST)
	}

	private fun createDarkness(auroraReport: Observable<AuroraReport>): LiveData<AuroraReportViewModel.Factor> {
		return auroraReport
			.map { it.factors.darkness }
			.map {
				val value = evaluateDarknessText(it)
				val chance = darknessChanceEvaluator.evaluate(it)
				AuroraReportViewModel.Factor(value, chance)
			}
			.toLiveData(BackpressureStrategy.LATEST)
	}

	private fun evaluateKpIndexText(factor: KpIndex): String {
		fun parsePart(part: Double): String {
			if (part < 0.15) {
				return ""
			}
			if (part < 0.5) {
				return "+"
			}
			return "-"
		}

		fun parseWhole(whole: Int, partString: String): String {
			val wholeAdjusted = if (partString == "-") whole + 1 else whole
			return Integer.toString(wholeAdjusted)
		}

		val kpIndex = factor.value ?: return "?"
		val whole = kpIndex.toInt()
		val part = kpIndex - whole
		val partString = parsePart(part)
		val wholeString = parseWhole(whole, partString)
		return wholeString + partString
	}

	 private fun evaluateGeomagLocationText(factor: GeomagLocation): String {
		val latitude = factor.latitude ?: return "?"
		return String.format(Locale.ENGLISH, "%.0f°", latitude)
	}

	private fun evaluateVisibilityText(factor: Visibility): String {
		val clouds = factor.cloudPercentage ?: return "?"
		val visibilityPercentage = 100 - clouds
		return Integer.toString(visibilityPercentage) + '%'
	}

	/*
	 * 0% at 90°. 100% at 108°
	 */
	private fun evaluateDarknessText(factor: Darkness): String {
		val zenithAngle = factor.sunZenithAngle ?: return "?"
		val darknessFactor = 1.0 / 18.0 * zenithAngle - 5.0
		var darknessPercentage = Math.round(darknessFactor * 100.0)
		darknessPercentage = Math.max(0, darknessPercentage)
		darknessPercentage = Math.min(100, darknessPercentage)
		return "$darknessPercentage%"
	}

	companion object {
		private val CLASS = AuroraReportViewModel::class.java
	}
}

