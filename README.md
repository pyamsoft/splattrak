# splattrak

An unofficial map rotation tracker for Splatoon 2.

This Application is not affiliated with Nintendo. All product names, logos, and brands are property
of their respective owners.

[![Get it on Google Play](https://raw.githubusercontent.com/pyamsoft/splattrak/main/art/google-play-badge.png)][1]

# What

See the current and upcoming map rotation for game modes in Splatoon 2.

## Privacy

SplatTrak requires the following Android permissions to run:

**android.permission.ACCESS_NETWORK_STATE** - To check if there is available internet connection.  
**android.permission.INTERNET** - To check for updates and show other related applications by pyamsoft.  
**com.android.vending.BILLING** - For In-App Billing related operation.  
**android.permission.FOREGROUND_SERVICE** - To be completely honest, I have no idea why the
FOREGROUND_SERVICE permission is being pulled in. It is not listed in the release manifest before
the build step, but appears in the merged manifest after building is completed.

## Development

SplatTrak is developed in the Open on GitHub at:
```
https://github.com/pyamsoft/splattrak
```

**PLEASE NOTE:** SplatTrak is **not a fully FOSS application.**
SplatTrak is open source, and always will be. It is free as in beer, but not free as in speech.
All features of SplatTrak (and any pyamsoft Android applications) will be zero-cost. You will
never be asked to purchase an in-app product to unlock a feature in the app. You have the option of
using an in-app purchase to send a "support token" to the developer, but this is neither an
expectation nor an obligation for the user.

This is due to the fact that it relies on the Google Play In-App Billing library for in-app
purchases. The Google Play library is proprietary, and requires a device using the proprietary
Google Play Services to use it. Aside from this single Google Play In-App Billing library, the
entire application and all of it's libraries are (should be to the best of my knowledge) fully
open source. SplatTrak will never try to track, analyze, or invade your privacy intentionally.
Any such discoveries of unintentional tracking from SplatTrak should be brought to the attention
of the developer via a GitHub Issue to be fixed as quickly as possible.

# Issues or Questions

Please post any issues with the code in the Issues section on GitHub. Pull Requests
will be accepted on GitHub only after extensive reading and as long as the request
goes in line with the design of the application.

[1]: https://play.google.com/store/apps/details?id=com.pyamsoft.splattrak

## License

Apache 2

```
Copyright 2021 Peter Kenji Yamanaka

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

