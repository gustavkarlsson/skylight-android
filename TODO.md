# TODO

## Release
* Create new screenshots on Google Play

## Soon
* Consider refresh after changing debug options
* Look into adding screen with open source licenses: https://developers.google.com/android/guides/opensource
* Generate png:s from master vector files (https://github.com/avianey/androidsvgdrawable-plugin)
* Consolidate color use in logo and app (not all logo variants are the same color)
* Rework API responses.
* KtLint
* Improve color system
* Make testing use other settings
* Add test to verify Koin setup
* Add instrumentation testing to CI
* Add more tests
* TextRef

## Some day
* Migrate CardView to MaterialCardView when they fixed the ripple effect bug (ripple not working)
* Add the thing that clears data between launches (check with Nick)
* Add prettier detail screens
* Separate debug menu instead of settings page
* Add more KTX
* Firebase performance monitoring
* Add manual clearing of "notification sent" in develop mode
* Add more testing options (individual overrides for example)
* Look into country changes while app running (Locale etc)
* Automate release in CI (Gradle release plugin? Fastlane? Automatic version management?)
* Additional analytics(Failed remote calls, Notifications on/off, Manually started app, Started app from notification, denied location permission, no Google Play Services installed, first start)
* Add performance traces
* Integrate code coverage (Coveralls or Codecov)
* Look into using Spek (problems with junit5 and android)
* Look into replacing evernote-job with WorkManager (difficult since it's hard to post results)
* Firebase remote config

## Never?
* Rename chance to score?
* Slices
* Widget
* Instant app
* Wear OS App, Tablet, Chrome OS, Leanback apps
* Allow reporting of false negatives/positives
