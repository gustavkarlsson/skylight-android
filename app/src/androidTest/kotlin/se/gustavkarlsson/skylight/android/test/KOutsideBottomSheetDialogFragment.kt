package se.gustavkarlsson.skylight.android.test

import com.agoda.kakao.KBaseView
import com.agoda.kakao.KView

class KOutsideBottomSheetDialogFragment : KBaseView<KView> {
	constructor() : super({ withId(android.support.design.R.id.touch_outside) })
}
