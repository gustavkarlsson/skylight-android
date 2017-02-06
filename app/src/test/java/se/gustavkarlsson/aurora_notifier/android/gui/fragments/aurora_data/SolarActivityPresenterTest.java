package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;
import se.gustavkarlsson.aurora_notifier.android.models.data.SolarActivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SolarActivityPresenterTest {
	private SolarActivityPresenter solarActivityPresenter;
	private AuroraDataView mockAuroraDataView;

	@Before
	public void setUp() throws Exception {
		Context mockContext = mock(Context.class);
		mockAuroraDataView = mock(AuroraDataView.class);
		when(mockAuroraDataView.getContext()).thenReturn(mockContext);

		solarActivityPresenter = new SolarActivityPresenter(mockAuroraDataView);
	}

	@Test
	public void construct_nullView_throwsNpe() throws Exception {
		assertThatThrownBy(() -> new SolarActivityPresenter(null)).isInstanceOf(NullPointerException.class);
	}

	@Test
	public void getTitleResourceId_positive() throws Exception {
		assertThat(solarActivityPresenter.getTitleResourceId()).isGreaterThan(0);
	}

	@Test
	public void getDescriptionResourceId_positive() throws Exception {
		assertThat(solarActivityPresenter.getDescriptionResourceId()).isGreaterThan(0);
	}

	@Test
	public void onUpdate_solarActivityCloseToWhole_setsDataValueToWhole() throws Exception {
		solarActivityPresenter.onUpdate(new SolarActivity(3.1F));

		verify(mockAuroraDataView).setValue(anyString());
	}
}
