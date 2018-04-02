package se.gustavkarlsson.skylight.android.test

import org.amshove.kluent.should

infix fun <T : Comparable<T>> T.shouldBeInRange(range: ClosedRange<T>) =
	this.should("Expected $this to be between (and including) ${range.start} and ${range.endInclusive}") {
		this in range
	}
