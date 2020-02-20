## Soon
* Use rhythm for layout, and other dimensions for elevation
* Show banner when no network (or just when on flight mode?)
* App bundles
* Consider refresh after changing debug options
* Look into adding screen with open source licenses: https://developers.google.com/android/guides/opensource
* Rework API responses.
* KtLint
* Make testing use other settings
* Switch to dagger
* Add instrumentation testing to CI
* Add more tests

## Android 11 migration checklist
* https://developer.android.com/preview/privacy/permissions#one-time
* https://developer.android.com/preview/privacy/location#background-location

## Some day
* Add the thing that clears data between launches (check with Nick)
* Add prettier detail screens
* Separate debug menu instead of settings page
* Add more KTX
* Firebase performance monitoring
* Add manual clearing of "notification sent" in develop mode
* Add more testing options (individual overrides for example)
* Look into country changes while app running (Locale etc)
* Additional analytics(Failed remote calls, Notifications on/off, Manually started app, Started app from notification, denied location permission, no Google Play Services installed, first start)
* Add performance traces
* Integrate code coverage (Coveralls or Codecov)
* Look into using Spek (problems with junit5 and android)
* Look into replacing evernote-job with WorkManager (difficult since it's hard to post results)
* Firebase remote config, or move more stuff to backend?
* Use cases instead of repositories?

## Never?
* Rename chance to score?
* Slices
* Widget
* Instant app
* Wear OS App, Tablet, Chrome OS, Leanback apps
* Allow reporting of false negatives/positives
