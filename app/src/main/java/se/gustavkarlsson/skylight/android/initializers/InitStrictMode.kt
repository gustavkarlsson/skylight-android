package se.gustavkarlsson.skylight.android.initializers

import android.os.Build
import android.os.StrictMode
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.core.logging.logError

internal fun initStrictMode() {
    if (BuildConfig.DEBUG) {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().apply {
                detectCustomSlowCalls()
                detectDiskReads()
                detectDiskWrites()
                detectNetwork()
                detectResourceMismatches()
                detectUnbufferedIo()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    penaltyListener(Runnable::run) { logError(it) }
                } else {
                    penaltyLog()
                }
            }.build(),
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder().apply {
                detectActivityLeaks()
                detectFileUriExposure()
                detectLeakedClosableObjects()
                detectLeakedRegistrationObjects()
                detectLeakedSqlLiteObjects()
                // Ignored for now since AppCompatDelegateImpl.computeFitSystemWindows calls this
                // detectNonSdkApiUsage()
                detectCleartextNetwork()
                detectContentUriWithoutPermission()
                // Ignored because OkHttp doesn't deal with this
                // https://github.com/square/okhttp/issues/3537#issuecomment-619414434
                // detectUntaggedSockets()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    detectCredentialProtectedWhileLocked()
                    detectImplicitDirectBoot()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    penaltyListener(Runnable::run) { logError(it) }
                } else {
                    penaltyLog()
                }
            }.build(),
        )
    }
}
