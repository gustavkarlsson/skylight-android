package se.gustavkarlsson.skylight.android.test

import com.agoda.kakao.KBaseView
import com.agoda.kakao.KView
import se.gustavkarlsson.skylight.android.R

class KBottomSheetDialogFragment : KBaseView<KView> {
	constructor() : super({ withId(R.id.design_bottom_sheet) })
}
