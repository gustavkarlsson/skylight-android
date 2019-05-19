package se.gustavkarlsson.skylight.android.gui.utils

import android.content.Intent
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction

fun verifyPrivacyPolicyOpened() = intended(hasAction(Intent.ACTION_VIEW))
