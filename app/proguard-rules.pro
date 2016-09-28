# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/kuahusg/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

##class extends Application
-keep class com.kuahusg.weather.App

##class extends Activity, Receiver, Service, Content Provider, CustomerView??
#-keep class * extends android.app.Activity
##class entity
#-keep class com.kuahusg.weather.model.Data.**
-keep class com.kuahusg.weather.model.**



##libraries??

