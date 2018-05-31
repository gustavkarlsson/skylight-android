package se.gustavkarlsson.skylight.android.test

import com.agoda.kakao.KBaseView
import com.agoda.kakao.KView

class KBottomSheetDialogFragment : KBaseView<KView> {
	constructor() : super({ withId(android.support.design.R.id.design_bottom_sheet) })
}
