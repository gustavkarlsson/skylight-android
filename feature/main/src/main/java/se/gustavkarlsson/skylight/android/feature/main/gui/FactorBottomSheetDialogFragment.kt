package se.gustavkarlsson.skylight.android.feature.main.gui

import android.content.DialogInterface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_factor_bottom_sheet.*
import se.gustavkarlsson.skylight.android.feature.main.R

internal class FactorBottomSheetDialogFragment : BottomSheetDialogFragment() {

    var onCancelListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.layout_factor_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireArguments().apply {
            title.setText(getInt(TITLE_KEY))
            description.setText(getInt(DESCRIPTION_KEY))
            description.movementMethod = LinkMovementMethod.getInstance()
            val errorText = getString(ERROR_TEXT_KEY)
            if (errorText != null) {
                error.text = errorText
                error.visibility = View.VISIBLE
            } else {
                error.visibility = View.GONE
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        onCancelListener?.invoke()
        super.onCancel(dialog)
    }

    companion object {
        private const val TITLE_KEY = "title"
        private const val DESCRIPTION_KEY = "description"
        private const val ERROR_TEXT_KEY = "errorText"
        fun newInstance(@StringRes title: Int, @StringRes description: Int, errorText: String?) =
            FactorBottomSheetDialogFragment().apply {
                arguments = bundleOf(
                    TITLE_KEY to title,
                    DESCRIPTION_KEY to description,
                    ERROR_TEXT_KEY to errorText
                )
            }
    }
}
