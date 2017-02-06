package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;
import se.gustavkarlsson.aurora_notifier.android.models.data.Weather;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WeatherPresenterTest {
	private WeatherPresenter weatherPresenter;
	private AuroraDataView mockAuroraDataView;

	@Before
	public void setUp() throws Exception {
		Context mockContext = mock(Context.class);
		mockAuroraDataView = mock(AuroraDataView.class);
		when(mockAuroraDataView.getContext()).thenReturn(mockContext);

		weatherPresenter = new WeatherPresenter(mockAuroraDataView);
	}

	@Test
	public void construct_nullView_throwsNpe() throws Exception {
		assertThatThrownBy(() -> new WeatherPresenter(null)).isInstanceOf(NullPointerException.class);
	}

	@Test
	public void getTitleResourceId_positive() throws Exception {
		assertThat(weatherPresenter.getTitleResourceId()).isGreaterThan(0);
	}

	@Test
	public void getDescriptionResourceId_positive() throws Exception {
		assertThat(weatherPresenter.getDescriptionResourceId()).isGreaterThan(0);
	}

	@Test
	public void onUpdate_weather_setsDataValue() throws Exception {
		weatherPresenter.onUpdate(new Weather(20));

		verify(mockAuroraDataView).setValue(anyString());
	}
}
