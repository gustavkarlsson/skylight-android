package se.gustavkarlsson.skylight.android.test

import android.support.test.espresso.Espresso
import android.support.test.espresso.PerformException
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.espresso.util.HumanReadables
import android.support.test.espresso.util.TreeIterables
import android.view.View
import org.hamcrest.Matcher
import java.util.concurrent.TimeoutException

fun waitForView(timeoutMillis: Long = 5000L, vararg viewMatchers: Matcher<View>) {
	Espresso.onView(isRoot()).perform(WaitForView(viewMatchers.asList(), timeoutMillis))
}

private class WaitForView(
	private val viewMatchers: Iterable<Matcher<View>>,
	private val timeoutMillis: Long
) : ViewAction {
	override fun getConstraints(): Matcher<View> {
		return isRoot()
	}

	override fun getDescription(): String {
		return "Wait for a child view to match $viewMatchers for up to $timeoutMillis millis."
	}

	override fun perform(uiController: UiController, root: View) {
		val startTime = System.currentTimeMillis()
		val endTime = startTime + timeoutMillis
		uiController.loopMainThreadUntilIdle()
		do {
			if (hasMatchingChild(root)) {
				return
			}
			uiController.loopMainThreadForAtLeast(50)
		} while (System.currentTimeMillis() < endTime)
		timeout(HumanReadables.describe(root))
	}

	private fun hasMatchingChild(root: View): Boolean {
		return TreeIterables.breadthFirstViewTraversal(root)
			.any { view ->
				viewMatchers.all { matcher ->
					matcher.matches(view)
				}
			}
	}

	private fun timeout(viewDescription: String?) {
		throw PerformException.Builder()
			.withActionDescription(description)
			.withViewDescription(viewDescription)
			.withCause(TimeoutException())
			.build()
	}
}
