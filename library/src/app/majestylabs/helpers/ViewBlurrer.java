/*
 * Copyright (C) 2014 Orhan Sönmez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package app.majestylabs.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nineoldandroids.view.ViewHelper;

@SuppressLint("NewApi")
public class ViewBlurrer {
	
	Context mContext;
	
	FrameLayout mFrameLayout;
	
	View mOriginalView;
	ImageView mBlurImageView;
	View mOverlayView;
	
	Bitmap mBlurredBitmap;
	
	Float mBlurRadius = 12.5f;
	int mScaleSize = 4;
	
	
	/**
	 * Constructs a new ViewBlurrer Object.
	 * 
	 * @param context	Application context of the caller
	 * @param view		View to be blurred
	 */
	public ViewBlurrer(Context context, View view) {

		// Save our context for later reference
		mContext = context;
		
		
		// Create a new FrameLayout that will hold the incoming View and its blurred presentation
		mFrameLayout = new FrameLayout(context);

		
		// Get the parent of our incoming View
		ViewGroup parentView = (ViewGroup) view.getParent();
		

		// Copy all relevant attributes from the incoming View as it will replace it
		mFrameLayout.setLayoutParams(view.getLayoutParams());
		
		
		// Get the index our incoming View is added at in its parent View
		int index = parentView.indexOfChild(view);
		
		
		// Remove the incoming View from its parent and add our FrameLayout
		parentView.removeView(view);
		parentView.addView(mFrameLayout, index);
		
		
		// Define LayoutParams for our FrameLayout that defines the stretches the width and height to fill the layout
		FrameLayout.LayoutParams fillFrameLayoutLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		
		
		// Instantiate the ImageView that holds our blurred image
		mBlurImageView = new ImageView(context);
		
		
		// Setup the ImageView
		mBlurImageView.setLayoutParams(fillFrameLayoutLP);
		mBlurImageView.setScaleType(ScaleType.FIT_XY);
		ViewHelper.setAlpha(mBlurImageView, 0f);
		
		
		// Setup the overlaying View
		mOverlayView = new View(context);
		mOverlayView.setLayoutParams(fillFrameLayoutLP);
		ViewHelper.setAlpha(mOverlayView, 0f);
		
		
		// Add our incoming View and our ImageView to our FrameLayout
		mFrameLayout.addView(view);
		mFrameLayout.addView(mBlurImageView);
		mFrameLayout.addView(mOverlayView);
		
		
		// Save our incoming View for later reference
		mOriginalView = view;
		
	}
	
	
	/**
	 * Sets a background resource to the View overlaying the blurred View
	 * 
	 * @param resourceId		Specifies the resource identifier to apply as background of the View
	 */
	public void setOverlayBackgroundResource(int resourceId) {
		mOverlayView.setBackgroundResource(resourceId);
	}
	
	
	/**
	 * Sets a background Drawable to the View overlaying the blurred View
	 * 
	 * @param drawable		Specifies the Drawable to apply as background of the View
	 */
	@SuppressWarnings("deprecation")
	public void setOverlayBackgroundDrawable(Drawable drawable) {
		
		if (Build.VERSION.SDK_INT >= 16)
			mOverlayView.setBackground(drawable);
		
		else
			mOverlayView.setBackgroundDrawable(drawable);
		
	}
	
	
	/**
	 * Sets a background color to the View overlaying the blurred View
	 * 
	 * @param color	Specifies the color to apply as background of the View
	 */
	public void setOverlayBackgroundColor(int color) {
		mOverlayView.setBackgroundColor(color);
	}
	
	
	/**
	 * Sets the opacity of the overlaying View
	 * 
	 * @param opacity	Specifies the opacity of the overlaying View. Range between 0f and 1f.
	 */
	public void setOverlayOpacity(float opacity) {
		
		// Catch edge cases
		if (opacity < 0f)
			opacity = 0f;
		
		else if (opacity > 1f)
			opacity = 1f;
		
		
		// Set the alpha
		mOverlayView.clearAnimation();
		ViewHelper.setAlpha(mOverlayView, opacity);
		
	}
	
	
	/**
	 * Sets the opacity of the overlaying View with an animation
	 * 
	 * @param opacity	Specifies the opacity the overlaying View should be set to. Range between 0f and 1f.
	 * @param duration	Specifies the duration of the animation (in milliseconds)
	 */
	public void animateOverlayOpacity(float opacity, long duration) {

		// Catch edge cases
		if (opacity < 0f)
			opacity = 0f;
		
		else if (opacity > 1f)
			opacity = 1f;
		
		
		// Set the alpha
		AlphaAnimation alphaAnimation = new AlphaAnimation(ViewHelper.getAlpha(mOverlayView), opacity);
		alphaAnimation.setDuration(duration);
		alphaAnimation.setFillAfter(true);
		
		ViewHelper.setAlpha(mOverlayView, 1f);
		mOverlayView.clearAnimation();
		mOverlayView.startAnimation(alphaAnimation);
		
	}
	
	
	/**
	 * Sets how much the View should be blurred.
	 * 
	 * @param radius		Specifies the blur radius. Range between 0f and 25f
	 */
	public void setBlurRadius(float radius) {
		
		if (radius < 0f)
			radius = 0f;
		
		else if (radius > 25f)
			radius = 25f;
		
		mBlurRadius = radius;
		
	}
	
	
	/**
	 * Sets the sample size of the processed blurred image, higher values improve blurriness and performance significantly!
	 * 
	 * @param sampleSize	Specifies the sample size (must be >= 1)
	 */
	public void setBlurSampleSize(int sampleSize) {
		
		if (sampleSize < 1)
			sampleSize = 1;
		
		mScaleSize = sampleSize;
		
	}
	
	
	/**
	 * Sets the opacity of the blurred View
	 * @param opacity	Opacity the blurred View will be set to. Range between 0f and 1f
	 */
	public void setOpacity(float opacity) {
		
		// Set the alpha values of our Views
		mBlurImageView.clearAnimation();
		mOverlayView.clearAnimation();
		
		ViewHelper.setAlpha(mBlurImageView, opacity);
		ViewHelper.setAlpha(mOverlayView, opacity);
		
	}
	
	
	/**
	 * Sets the opacity of the blurred View
	 * 
	 * @param opacity	Specifies the opacity the blurred View should be set to. Range between 0f and 1f.
	 */
	public void setBlurOpacity(float opacity) {
		
		// Check whether we have blurred the View already, if not, do it now
		if (mBlurredBitmap == null) {
			mBlurredBitmap = applyBlur();
			mBlurImageView.setImageBitmap(mBlurredBitmap);
		}
		
		
		// Catch edge cases
		if (opacity < 0f)
			opacity = 0f;
		
		else if (opacity > 1f)
			opacity = 1f;
		
		
		// Set the alpha
		mBlurImageView.clearAnimation();
		ViewHelper.setAlpha(mBlurImageView, opacity);
		
		
		// Reset the blur if we have zero opacity
		if (opacity == 0f)
			reset();
		
	}
	
	
	/**
	 * Sets the opacity of the blurred View with an animation
	 * 
	 * @param opacity	Specifies the opacity the blurred View should be set to. Range between 0f and 1f.
	 * @param duration	Specifies the duration of the animation (in milliseconds)
	 */
	public void animateBlurOpacity(float opacity, long duration) {
		
		// Check whether we have blurred the View already, if not, do it now
		if (mBlurredBitmap == null) {
			mBlurredBitmap = applyBlur();
			mBlurImageView.setImageBitmap(mBlurredBitmap);
		}
		
		
		// Catch edge cases
		if (opacity < 0f)
			opacity = 0f;
		
		else if (opacity > 1f)
			opacity = 1f;
		
		
		// Set the alpha
		AlphaAnimation alphaAnimation = new AlphaAnimation(ViewHelper.getAlpha(mBlurImageView), opacity);
		alphaAnimation.setDuration(duration);
		alphaAnimation.setFillAfter(true);
		
		ViewHelper.setAlpha(mBlurImageView, 1f);
		mBlurImageView.clearAnimation();
		mBlurImageView.startAnimation(alphaAnimation);
		
		
		// Reset the blur if we have zero opacity
		if (opacity == 0f)
			reset();
		
	}
	
	
	/**
	 * Sets the opacity of the original content View with an animation
	 * 
	 * @param opacity	Specifies the opacity the original View should be set to. Range between 0f and 1f.
	 * @param duration	Specifies the duration of the animation (in milliseconds)
	 */
	public void animateViewOpacity(float opacity, long duration) {

		// Catch edge cases
		if (opacity < 0f)
			opacity = 0f;
		
		else if (opacity > 1f)
			opacity = 1f;
		
		
		// Set the alpha
		AlphaAnimation alphaAnimation = new AlphaAnimation(ViewHelper.getAlpha(mOriginalView), opacity);
		alphaAnimation.setDuration(duration);
		alphaAnimation.setFillAfter(true);
		
		ViewHelper.setAlpha(mOriginalView, 1f);
		mOriginalView.clearAnimation();
		mOriginalView.startAnimation(alphaAnimation);
		
	}
	
	
	/**
	 * Scales the View to achieve a zoom-in / zoom-out effect.
	 * 
	 * @param scale		Specifies the scale of the effect
	 */
	public void setScale(float scale) {
		ViewHelper.setScaleX(mOverlayView, scale);
		ViewHelper.setScaleY(mOverlayView, scale);
		
		ViewHelper.setScaleX(mBlurImageView, scale);
		ViewHelper.setScaleY(mBlurImageView, scale);
		
		ViewHelper.setScaleX(mOriginalView, scale);
		ViewHelper.setScaleY(mOriginalView, scale);
	}
	
	
	/**
	 * Tilt the view (rotate it around its Y axis).
	 * 
	 * @param rotation		The degrees of Y rotation.
	 */
	public void setTilt(float rotation) {
		ViewHelper.setRotationY(mOverlayView, rotation);
		ViewHelper.setRotationY(mBlurImageView, rotation);
		ViewHelper.setRotationY(mOriginalView, rotation);
	}
	
	
	/**
	 * Resets the ViewBlurrer, call this to revert the view to its original state.
	 */
	public void reset() {
		
		if (mBlurredBitmap != null) {
			mBlurredBitmap.recycle();
			mBlurredBitmap = null;
		}
		
		ViewHelper.setAlpha(mBlurImageView, 0f);
		ViewHelper.setAlpha(mOverlayView, 0f);
		ViewHelper.setAlpha(mOriginalView, 1f);
		
		mBlurImageView.clearAnimation();
		mOverlayView.clearAnimation();
		mOriginalView.clearAnimation();
		
		mOriginalView.setVisibility(View.VISIBLE);
		mOriginalView.invalidate();
		
		mBlurImageView.setImageBitmap(null);
		
	}
	
	
	/**
	 * Applies the blur
	 */
	private Bitmap applyBlur() {
		
		Bitmap resultBitmap = null;
		
		try {
			
			// Do some garbage collection first
			System.gc();
			
			
			// Get the drawing cache of our incoming View
			mOriginalView.buildDrawingCache();
			Bitmap originalBitmap = mOriginalView.getDrawingCache().copy(Config.ARGB_8888, true);
			mOriginalView.destroyDrawingCache();
			
			
			// Define a resulting Bitmap
			resultBitmap = Bitmap.createScaledBitmap(originalBitmap, originalBitmap.getWidth() / mScaleSize, originalBitmap.getHeight() / mScaleSize, true);
			
	
			// Recycle the original bitmap
			originalBitmap.recycle();
			originalBitmap = null;
			
			
			// Do this only if we want to apply a RS-blur
			if (mBlurRadius > 0f) {
				
				// Get a new RenderScript object
		    	RenderScript renderScript = RenderScript.create(mContext);
		    	
		        final Allocation input = Allocation.createFromBitmap(renderScript, resultBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SHARED);
		        final Allocation output = Allocation.createFromBitmap(renderScript, resultBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SHARED);
		        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
		        
		        script.setRadius(mBlurRadius);
		        script.setInput(input);
		        script.forEach(output);
		        
		        renderScript.finish();
		        output.copyTo(resultBitmap);
		        
		    	renderScript.destroy();
		    	renderScript = null;
	    	
			}
			
		}
		
		catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// Do a garbage collection 
		System.gc();
		
		
		// Return our result
		return resultBitmap;
		
	}

}
