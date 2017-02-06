package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;
import se.gustavkarlsson.aurora_notifier.android.models.data.GeomagneticLocation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GeomagneticLocationTest {
	private GeomagneticLocationPresenter geomagneticLocationPresenter;
	private AuroraDataView mockAuroraDataView;

	@Before
	public void setUp() throws Exception {
		Context mockContext = mock(Context.class);
		mockAuroraDataView = mock(AuroraDataView.class);
		when(mockAuroraDataView.getContext()).thenReturn(mockContext);

		geomagneticLocationPresenter = new GeomagneticLocationPresenter(mockAuroraDataView);
	}

	@Test
	public void construct_nullView_throwsNpe() throws Exception {
		assertThatThrownBy(() -> new GeomagneticLocationPresenter(null)).isInstanceOf(NullPointerException.class);
	}

	@Test
	public void getTitleResourceId_positive() throws Exception {
		assertThat(geomagneticLocationPresenter.getTitleResourceId()).isGreaterThan(0);
	}

	@Test
	public void getDescriptionResourceId_positive() throws Exception {
		assertThat(geomagneticLocationPresenter.getDescriptionResourceId()).isGreaterThan(0);
	}

	@Test
	public void onUpdate_geomagneticLocation_setsDataValue() throws Exception {
		geomagneticLocationPresenter.onUpdate(new GeomagneticLocation(15.5F));

		verify(mockAuroraDataView).setValue(anyString());
	}
}
