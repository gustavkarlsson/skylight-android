package se.gustavkarlsson.skylight.android.gui.kakao

import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import com.agoda.kakao.KBaseView
import com.agoda.kakao.KView
import com.agoda.kakao.ViewBuilder
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`

class KToolbar(function: ViewBuilder.() -> Unit) : KBaseView<KView>(function) {
    fun hasTitle(title: String) {
        view.check(matches(withToolbarTitle(`is`(title))))
    }

    private fun withToolbarTitle(textMatcher: Matcher<CharSequence>): Matcher<Any> {
        return object : BoundedMatcher<Any, Toolbar>(Toolbar::class.java) {
            public override fun matchesSafely(toolbar: Toolbar): Boolean {
                return textMatcher.matches(toolbar.title)
            }

            override fun describeTo(description: Description) {
                description.appendText("with toolbar title: ")
                textMatcher.describeTo(description)
            }
        }
    }
}
