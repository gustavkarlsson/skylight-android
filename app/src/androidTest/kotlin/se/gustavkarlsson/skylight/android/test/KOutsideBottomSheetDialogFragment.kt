package se.gustavkarlsson.skylight.android.test

import com.agoda.kakao.KBaseView
import com.agoda.kakao.KView
import se.gustavkarlsson.skylight.android.R

class KOutsideBottomSheetDialogFragment : KBaseView<KView> {
	constructor() : super({ withId(R.id.touch_outside) })
}
