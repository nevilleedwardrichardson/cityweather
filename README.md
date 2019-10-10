# cityweather

Code and image use within the app

1) Images courtesy of google image search with free to use royalty selected and also weather symbols taken from recent iOS course I have completed: https://www.udemy.com/course/ios-13-app-development-bootcamp/
2) WeatherAPI.java taken and modified from stack overflow question https://stackoverflow.com/questions/49456378/how-to-show-weather-based-on-location-with-openweathermap-on-android

Notes: 
1) You may need to use your own OPEN_WEATHER_MAP_API from https://openweathermap.org
2) The app does not look for location updates as that would have been a lot more work so takes the current location from fusedLocationClient.getLastLocation(). Ideally it would scan for the location and also check the GoogleAPIClient is connected for play services etc.
3) There is none of the optional unit testing included.
