package se.gustavkarlsson.skylight.android.gui.screens.main

import android.content.DialogInterface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_factor_bottom_sheet.*
import se.gustavkarlsson.skylight.android.R

class FactorBottomSheetDialogFragment : BottomSheetDialogFragment() {

	var onCancelListener: (() -> Unit)? = null

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

	override fun onCancel(dialog: DialogInterface) {
		onCancelListener?.invoke()
		super.onCancel(dialog)
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
