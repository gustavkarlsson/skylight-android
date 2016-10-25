package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class GeomagneticCoordinatesProviderImplTest {
	@Test
	public void getDegreesFromClosestPole() throws Exception {

		new GeomagneticCoordinatesProviderImpl().getDegreesFromClosestPole(50.1109, 8.6821);
	}

}
