# ru_profi1c_engine
# custom rules only (need base rules from proguard-android.txt or proguard-android-optimize.txt)

-keepattributes *Annotation*,Exceptions,Signature

-libraryjars /libs/commons-io-2.1.jar
-libraryjars /libs/gson-2.2.2.jar
-libraryjars /libs/ksoap2-android-2.6.0.jar
-libraryjars /libs/ormlite-android-4.49.jar
-libraryjars /libs/ormlite-core-4.49.jar

#==========================================================================
-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#==========================================================================
# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
-dontwarn **CompatHoneycomb
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.app.Fragment

#==========================================================================
# apache IO
-keep class org.apache.**
-keep class org.apache.commons.logging.**               { *; }

-keep class org.apache.commons.logging.LogFactory {
    <methods>;
}
-keep class org.apache.commons.logging.Log {
    <methods>;
}

-keep class org.apache.commons.codec {
    <methods>;
}

-keep class org.apache.commons.codec.binary {
    <methods>;
}
-keep class org.apache.commons.codec.net {
    <methods>;
}
-keep class org.apache.commons.codec.binary.Base64 {
    <methods>;
}
-keep class org.apache.commons.codec.net.URLCodec {
    <methods>;
}

#==========================================================================
#ormlite
-keep class com.j256.** {
	*;
}
-keepclassmembers class com.j256.** {
    *;
}
-keep public class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper

#==========================================================================
# ru_profi1c_engine specific

#database
-keep class ru.profi1c.engine.meta.DBOpenHelper{
	public <init>(android.content.Context);
}
-keep class * extends ru.profi1c.engine.meta.Row {
	<fields>;
}

#ui
-keep public class ru.profi1c.engine.widget.** { public *; }

#ofter
-keep public class * extends ru.profi1c.engine.FbaRuntimeException { public *; }