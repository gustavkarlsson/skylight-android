package se.gustavkarlsson.skylight.android.gui.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import se.gustavkarlsson.skylight.android.entities.AuroraReport

class AuroraReportViewModel(
	val auroraReports: LiveData<AuroraReport>
) : ViewModel()
