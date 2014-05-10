ViewBlurrer (for Android)
=========================

Little nifty helper to apply a blur-effect to any View. Its convenience lies in it acting as a drop-in replacement for the View that should be blurred, which means that it does not require any changes to your XML layouts. 


How it works
============

When creating a `ViewBlurrer` instance, you pass it the `View` that you want it to blur.
ViewBlurrer creates a new `FrameLayout` and adds the passed `View` and an `ImageView` to it, which overlays your `View` element perfectly and be used to hold the blurred version of it.


Setup
=====

Note: this is the setup routine for Eclipse/ANT.

1. Import `ViewBlurrer` as a new Android Project or download the available .jar file and add it to your build path.
2. Add the following lines to your project.properties file (this references the [RenderScript support library](http://android-developers.blogspot.de/2013/09/renderscript-in-android-support-library.html)):
```
renderscript.target=19
enderscript.support.mode=true
sdk.buildtools=19.0.1* 

* Replace this with the most recent version of the build tools
```

Usage
=====

1. Crate a new instance of `ViewBlurrer` and pass it the `View` element you want to have blurred

2. Optional: Setup the `ViewBlurrer` by setting its options:
   * Set the sample size (similar to [BitmapFactory.inSampleSize](http://developer.android.com/reference/android/graphics/BitmapFactory.Options.html#inSampleSize)) by calling `setSampleSize(x)`, where x is an `int` value >= 1. Higher values improve the blurriness and performance significantly, but the result may become too "washed out".
   * Set the blur radius by calling `setBlurRadius(x)`, where x is a `float` value between `1f` and `25f`.
   * Set additional options like an overlay color, drawable or resource, e.g. to also darken or light up the blurred view (for example by applying an alpha-added color like `#80000000`).

3. Show the blurred View by calling `setOpacity(1f)`. You can enter any `float`value between 0f (hiding the view altogether) and 1f (for full opaqueness).

---

Code example:

```
ViewBlurrer mViewBlurrer = new ViewBlurrer(getApplicationContext(), mRootLayout);
mViewBlurrer.setBlurScale(8);
mViewBlurrer.setBlurRadius(5f);
mViewBlurrer.setOverlayBackgroundColor(Color.parseColor("#99000000"));

mViewBlurrer.setOpacity(1f);
```

Known issues / limitations
==========================

- Creating a FrameLayout and an ImageView for every instance of ViewBlurrer can become somewhat costly, so try to keep the amount of blurred Views low (usually you would apply it to the root view of your activity; see examples).
- Caution: Calling getParent() on the initial View will return the FrameLayout now, so call getParent() again to get the initial parent!
