## ASAP
* Fix location permission:
  * On Android 11+: Denying a permission more than once automatically sets "don't ask again" (https://developer.android.com/about/versions/11/privacy/permissions#dialog-visibility)
  * On Android 11+ Cannot request foreground AND background permission at the same time (https://developer.android.com/about/versions/11/privacy/location#request-background-location-separately)
  * On Android 11+: Cannot request background permission via dialog at all? Must use app settings? (https://developer.android.com/about/versions/11/privacy/location#background-location-permission-dialog-changes)
  * "Ask every time" shows up as "denied" for some reason (due to above reasons?)
  * Test on different SDK:s Especially 10 and 11 which have slightly different solutions for requesting background permission
* Ensure fetching aurora report in background always happens (se.gustavkarlsson.skylight.android.feature.background.scheduling.BackgroundWorkImpl.getNotificationData)

## Soon
* Consider using accompanist permissions
* Standard dimensions?
* Standard animations?
* Bug: Single-use permission doesn't really work: https://developer.android.com/preview/privacy/permissions#one-time
* Make location refresh on phone location on/off, or permission granted
* Show actionable banner for missing location, permission, internet, notifications denied, with better action text than "fix"
* Migrate libraries using sqldelight to use store?
* Readme
* Simpler configuration of required keys (move to root? fail if not present?)
* Retire ThreeTenABP
* Add Hyperion
* Add prettier detail screens
* Improve feature graphics
* Main screen: Tell a story, one or a few sentences?
* Look into adding screen with open source licenses: https://developers.google.com/android/guides/opensource

## Some day
* Oversee text style usage (also use TextAppearance instead of style in XML)
* Make testing use other settings
* Add more tests
* Add develop settings
* Add instrumentation testing to CI
* Consider refresh after changing debug options
* Separate debug menu instead of settings page
* Firebase performance monitoring
* Add manual clearing of "notification sent" in develop mode
* Add more testing options (individual overrides for example)
* Look into country changes while app running (Locale etc)
* Additional analytics(Failed remote calls, Notifications on/off, Manually started app, Started app from notification, denied location permission, no Google Play Services installed, first start)
* Add performance traces
* Integrate code coverage (Coveralls or Codecov)
* Look into using Spek (problems with junit5 and android)
* Firebase remote config, or move more stuff to backend?

## Never?
* Rename chance to score?
* Slices
* Widget
* Instant app
* Wear OS App, Tablet, Chrome OS, Leanback apps
* Allow reporting of false negatives/positives

Factor card ideas:
Change question mark to warning icon. Add CTA for fixing location problem, with same action name as in banner
Put warning icon
Make them bigger
Add graphics

Graphics ideas for factors:
Kp: Show a scale with the values pointed out
Location: globe with latitude lines/scale
Weather: Clouds/Sun
Darkness: Sunset/Sunrise, maybe with a globe, and a line showing the movement of sun

Idea: Map in top, with bottom sheet that contains all factors
