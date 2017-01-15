package se.gustavkarlsson.aurora_notifier.android.util;


import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;
import static se.gustavkarlsson.aurora_notifier.android.util.AuroraChance.*;


public class AuroraEvaluator {
	private final RealmWeather weather;
	private final RealmSunPosition sunPosition;
	private final RealmKpIndex kpIndex;
	private final RealmGeomagneticCoordinates geomagneticCoordinates;

	public AuroraEvaluator(RealmWeather weather, RealmSunPosition sunPosition, RealmKpIndex kpIndex, RealmGeomagneticCoordinates geomagneticCoordinates) {
		this.weather = checkNotNull(weather);
		this.sunPosition = checkNotNull(sunPosition);
		this.kpIndex = checkNotNull(kpIndex);
		this.geomagneticCoordinates = checkNotNull(geomagneticCoordinates);
	}

	public AuroraEvaluation evaluate() {
		AuroraEvaluation evaluation = new AuroraEvaluation();
		evaluateWeather(weather, evaluation);
		evaluateSunPosition(sunPosition, evaluation);
		evaluateKpIndexAndGeomagneticCoordinates(kpIndex, geomagneticCoordinates, evaluation);
		return evaluation;
	}

	private static void evaluateWeather(RealmWeather weather, AuroraEvaluation evaluation) {
		Integer cloudPercentage = weather.getCloudPercentage();
		if (cloudPercentage == null) {
			evaluation.addComplication("Could not get the current weather", UNKNOWN);
			return;
		}
		if (cloudPercentage > 50) {
			evaluation.addComplication("It's too cloudy", NONE);
		} else if (cloudPercentage > 25) {
			evaluation.addComplication("Clouds might obstruct the view", LOW);
		}
	}

	private static void evaluateSunPosition(RealmSunPosition sunPosition, AuroraEvaluation evaluation) {
		Float zenithAngle = sunPosition.getZenithAngle();
		if (zenithAngle == null) {
			evaluation.addComplication("Could not determine your position", UNKNOWN);
			return;
		}
		if (zenithAngle < 90) {
			evaluation.addComplication("The sun is still up", NONE);
		} else if (zenithAngle < 95) {
			evaluation.addComplication("It might be too bright outside", LOW);
		}
	}

	private static void evaluateKpIndexAndGeomagneticCoordinates(RealmKpIndex realmKpIndex, RealmGeomagneticCoordinates geomagneticCoordinates, AuroraEvaluation evaluation) {
		Float kpIndex = realmKpIndex.getKpIndex();
		Float degreesFromClosestPole = geomagneticCoordinates.getDegreesFromClosestPole();
		if (kpIndex == null) {
			evaluation.addComplication("Could not determine current solar activity", UNKNOWN);
		}
		if (degreesFromClosestPole == null) {
			evaluation.addComplication("Could not determine your position", UNKNOWN);
		}
		if (kpIndex != null && kpIndex < 1) {
			evaluation.addComplication("There is not enough solar activity", NONE);
		}
		if (degreesFromClosestPole != null && degreesFromClosestPole > 35) {
			evaluation.addComplication("You are too far away from any magnetic pole", NONE);
		}
		if (kpIndex == null || degreesFromClosestPole == null) {
			return;
		}
		// TODO fill out the rest
	}
}
