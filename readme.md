Project for showing android bug 128894722
=========================================

When a view is made invisible (`setVisibility(View.GONE)` or `setVisibility(View.INVISIBLE)`),
Accessibility services do not get notified with an `AccessibilityEvent` unless the change
causes secondary view tree updates and accessibility events.

This project is a minimal working version to show the bug.

Follow these steps to show the bug behavior:
1. Build and install the app.
2. (optional) Give write permission to the app. The events are dumped to file for convenience. The events are also always dumped to logcat under the "EventLog" tag.
3. Enable the accessibility service provided by this app.
4. Open the activity provided by this app.
5. Wait some time, the button in the app should disappear and re-appear every few seconds.
6. Close the app and the accessibility service.
7. Compare the event logs of the activity with the received events of the service.

For details, see [https://issuetracker.google.com/issues/128894722](https://issuetracker.google.com/issues/128894722).