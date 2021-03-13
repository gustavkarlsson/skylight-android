## ASAP Compose
* Create base theme
* Include icon set in base theme
* Include extra colors
* Include extra text styles
* Include standard dimensions

## Soon
* Single-use permission doesn't really work
* Make location take phone location on/off into account
* Make location auto-refresh on permission granted
* Migrate libraries using sqldelight to use store
* Bug: If location is granted (while using app), then go to add place, and go back. Banner shows up again.
* Rework location inputs to "ApproximateLocation" so store can cache results from nearby locations
* Readme
* Simpler configuration of required keys (move to root? fail if not present?)
* Retire ThreeTenABP
* Add Hyperion
* Android 11: https://developer.android.com/preview/privacy/permissions#one-time
* Android 11: https://developer.android.com/preview/privacy/location#background-location
* Use cases instead of repositories?
* Show banner when no connectivity (check for flight mode?)
* Look into adding screen with open source licenses: https://developers.google.com/android/guides/opensource
* Add prettier detail screens
* Improve feature graphics

## Some day
* Put more info in bottom sheet. Images, text, links, CTA for missing location
* Use favorites instead of saving? If so: Change map icon (in menu) to favorite icon?
* Change "FIX" to "ALLOW/GRANT" in banner?
* Main screen: Tell a story, one or a few sentences.
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
