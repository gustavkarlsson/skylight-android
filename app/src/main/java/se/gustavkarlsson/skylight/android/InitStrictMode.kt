package se.gustavkarlsson.skylight.android

import android.os.Build
import android.os.StrictMode
import timber.log.Timber

internal fun initStrictMode() {
    if (BuildConfig.DEBUG) {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().apply {
                detectCustomSlowCalls()
                detectDiskReads()
                detectDiskWrites()
                detectNetwork()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    detectResourceMismatches()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    detectUnbufferedIo()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    penaltyListener(Runnable::run, Timber::e)
                } else {
                    penaltyLog()
                }
            }.build()
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    detectCleartextNetwork()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    detectContentUriWithoutPermission()
                    // Ignored because OkHttp doesn't deal with this
                    // https://github.com/square/okhttp/issues/3537#issuecomment-619414434
                    // detectUntaggedSockets()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    detectCredentialProtectedWhileLocked()
                    detectImplicitDirectBoot()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    penaltyListener(Runnable::run, Timber::e)
                } else {
                    penaltyLog()
                }
            }.build()
        )
    }
}
