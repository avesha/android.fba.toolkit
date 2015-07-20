# ru_profi1c_maspsforge
# custom rules only (need base rules from proguard-android.txt or proguard-android-optimize.txt)
-libraryjars /libs/mapsforge-core-0.5.1.jar
-libraryjars /libs/mapsforge-map-0.5.1.jar
-libraryjars /libs/mapsforge-map-android-0.5.1.jar
-libraryjars /libs/mapsforge-map-reader-0.5.1.jar

-keep class org.mapsforge.** { *; }
-keep class osmarender.** { *; }