## ASAP

## Soon
* Don't send location streams to stream other data
* Readme
* Look into introducing some functional type instead of Report
* Dark mode splash screen for Android 12
* Global notification level?
* Notifications on/off without dialog (or only dialog when enabling). Needs a way to select current setting
* Only subscribe to current location reports when location is turned on?
* Show helper about location turned off?
* Consider using jetpack BackHandler
* Consider using accompanist permissions
* Standard dimensions?
* Standard animations?
* Make location refresh on phone location on/off, or permission granted
* Show actionable banner for missing location, permission, internet, notifications denied, with better action text than "fix"
* Migrate libraries using sqldelight to use store?
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
