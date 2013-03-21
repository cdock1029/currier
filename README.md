currier
=======
To get the MapFragments working properly in Eclipse, you must include the google-play-services_lib as a library for the Currier project
(1) Make sure google-play-services has been downloaded via the SDK manager
(2) Import into Eclipse the google-play-services library located at:
~/sdk/extras/google/google_play_services/libproject/google-play-services_lib
(3) Right click on the Currier project, open Properties
(4) Select "Android" in the list
(5) On the bottom of the resulting page with Library, click on Add, select the google-play-services_lib, and select OK.

The google-play-services_lib should now be a referenced library for the project, and all "com.google.android.gms.maps" references
should now be resolved.