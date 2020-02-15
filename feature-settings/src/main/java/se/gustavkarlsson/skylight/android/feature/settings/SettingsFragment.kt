package se.gustavkarlsson.skylight.android.feature.settings

import androidx.appcompat.widget.Toolbar
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment

internal class SettingsFragment : BaseFragment() {

	private val viewModel by viewModel<SettingsViewModel>()

	private val adapter by lazy { SettingsAdapter(viewModel) }

	override val layoutId: Int = R.layout.fragment_settings

	override val toolbar: Toolbar? get() = toolbarView

	override fun initView() {
		itemsRecyclerView.adapter = adapter
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		viewModel.settingsItems
			.autoDisposable(scope)
			.subscribe(adapter::setItems)
	}
}
