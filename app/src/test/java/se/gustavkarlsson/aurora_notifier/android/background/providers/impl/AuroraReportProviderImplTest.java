package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import org.junit.Test;

public class AuroraReportProviderImplTest {
	@Test
	public void getDegreesFromClosestPole() throws Exception {
		new GeomagLocationProviderImpl().getGeomagLocation(50.1109, 8.6821);
	}

}
