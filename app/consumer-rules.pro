# ProGuard rules for consumers of this library
# Keep all public and protected classes and their public/protected members
-keep public class * {
    public protected *;
}

# Keep all classes in the library's package
-keepclassmembers class com.github.gustavlindberg99.androidsuspendutils.** {
    *;
}
