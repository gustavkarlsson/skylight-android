package se.gustavkarlsson.skylight.android.gui.screens.main

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.BottomSheetDialogFragment
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_factor_bottom_sheet.*
import se.gustavkarlsson.skylight.android.R

class FactorBottomSheetDialogFragment : BottomSheetDialogFragment() {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.layout_factor_bottom_sheet, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		arguments!!.apply {
			title.setText(getInt(TITLE_KEY))
			description.setText(getInt(DESCRIPTION_KEY))
			description.movementMethod = LinkMovementMethod.getInstance()
		}
	}

	companion object {
		private const val TITLE_KEY = "title"
		private const val DESCRIPTION_KEY = "description"
		fun newInstance(@StringRes title: Int, @StringRes description: Int) =
			FactorBottomSheetDialogFragment().apply {
				arguments = Bundle().apply {
					putInt(TITLE_KEY, title)
					putInt(DESCRIPTION_KEY, description)
				}
			}
	}

}
