package com.usda.fmsc.android;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.lang.reflect.Method;

public class AndroidUtils {
    public static class App {
        private boolean isServiceRunning(Context ctx, String serviceName) {
            ActivityManager manager = (ActivityManager) ctx.getApplicationContext().getSystemService(ctx.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceName.equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }

        public static void openFileIntent(Activity context, String minmeType, int resultCode) {
            openFileIntent(context, minmeType, null, resultCode);
        }

        public static void openFileIntent(Activity context, String mimeType, String[] extraMimes, int resultCode) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(mimeType);

            if (extraMimes != null && extraMimes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimes);
            }

            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // special intent for Samsung file manager
            Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            // if you want any file type, you can skip next line
            //sIntent.putExtra("CONTENT_TYPE", minmeType);
            sIntent.putExtra("CONTENT_TYPE", "*/*");
            sIntent.addCategory(Intent.CATEGORY_DEFAULT);

            Intent chooserIntent;
            if (context.getPackageManager().resolveActivity(sIntent, 0) != null) {
                // it is device with samsung file manager
                chooserIntent = Intent.createChooser(sIntent, "Open file");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
            } else {
                chooserIntent = Intent.createChooser(intent, "Open file");
            }

            try {
                context.startActivityForResult(chooserIntent, resultCode);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context.getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
            }
        }

        public static Integer checkPlayServices(Activity activity, int resultCode) {
            GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
            int result = googleApi.isGooglePlayServicesAvailable(activity);

            if (result != ConnectionResult.SUCCESS &&
                    (!googleApi.isUserResolvableError(result) ||
                            !googleApi.showErrorDialogFragment(activity, result, resultCode))) {
                return result;
            }

            return null;
        }


        public static boolean checkCoarseLocationPermission(Context context) {
            return checkPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        public static boolean checkFineLocationPermission(Context context) {
            return checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        }

        public static boolean checkLocationPermission(Context context) {
            return checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    checkPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        public static boolean checkPermission(Context context, String permission) {
            return Build.VERSION.SDK_INT < 23 || (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        }
    }

    public static class Device {
        public static void vibrate(Context context, long millisecond) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(millisecond);
        }

        public static void vibrate(Context context, long[] pattern) {
            vibrate(context, pattern, -1);
        }

        public static void vibrate(Context context, long[] pattern, int repeat) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(pattern, repeat);
        }


        public static void playSound(Context context, int sound) {
            final MediaPlayer player = MediaPlayer.create(context, sound);

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    player.reset();
                    player.release();
                }
            });

            player.start();
        }

        public static void playSound(Context context, Uri sound) {
            final MediaPlayer player = MediaPlayer.create(context, sound);

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    player.reset();
                    player.release();
                }
            });

            player.start();
        }


        public static boolean isPackageInstalled(Context context, String packagename) {
            PackageManager pm = context.getPackageManager();
            try {
                pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }

        public static void navigateAppStore(Context context, String packageName) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
        }


        public static boolean hasJellyBean() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
        }

        public static boolean hasLollipop() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        }
    }

    public static class UI {

        public static void removeSelectionOnUnfocus(final EditText editText) {
            removeSelectionOnUnfocus(editText, false);
        }

        public static void removeSelectionOnUnfocus(final EditText editText, final boolean setEndSelected) {
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        editText.setSelection(setEndSelected ? editText.getText().length() : 0);
                        editText.clearFocus();
                    }
                }
            });
        }

        public static void hideKeyboardOnSelect(final View view, final EditText editText) {
            hideKeyboardOnSelect(view, editText, false);
        }

        public static void hideKeyboardOnSelect(final View view, final EditText editText, final boolean setEndSelected) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) view.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    editText.setSelection(setEndSelected ? editText.getText().length() : 0);
                    editText.clearFocus();
                }
            });
        }


        public static void hideKeyboardOnTouch(final View view, final EditText editText) {
            hideKeyboardOnTouch(view, editText, false);
        }


        public static void hideKeyboardOnTouch(final View view, final EditText editText, final boolean setEndSelected) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (editText.hasFocus()) {
                        InputMethodManager imm = (InputMethodManager) view.getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                        editText.setSelection(setEndSelected ? editText.getText().length() : 0);
                        editText.clearFocus();
                    }
                    return false;
                }
            });
        }

        public static void hideKeyboardOnSelect(final View view, final EditText[] editTexts) {
            hideKeyboardOnSelect(view, editTexts, false);
        }

        public static void hideKeyboardOnSelect(final View view, final EditText[] editTexts, final boolean setEndSelected) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) view.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    for (final EditText editText : editTexts) {
                        editText.setSelection(setEndSelected ? editText.getText().length() : 0);
                        editText.clearFocus();
                    }
                }
            });
        }

        public static void hideKeyboardOnTouch(final View view, final EditText[] editTexts) {
            hideKeyboardOnTouch(view, editTexts, false);
        }

        public static void hideKeyboardOnTouch(final View view, final EditText[] editTexts, final boolean setEndSelected) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) view.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    for (final EditText editText : editTexts) {
                        editText.setSelection(setEndSelected ? editText.getText().length() : 0);
                        editText.clearFocus();
                    }
                    return false;
                }
            });
        }

        public static void hideKeyboard(Activity activity) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        public static int getWidestView(Context context, Adapter adapter) {
            int maxWidth = 0;
            View view = null;
            FrameLayout fakeParent = new FrameLayout(context);
            for (int i = 0, count = adapter.getCount(); i < count; i++) {
                view = adapter.getView(i, view, fakeParent);
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int width = view.getMeasuredWidth();
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            return maxWidth;
        }


        public static void setContentDescToast(View view) {
            setContentDescToast(view, view.getContentDescription(), true);
        }

        public static void setContentDescToast(View view, boolean vibrate) {
            setContentDescToast(view, view.getContentDescription(), vibrate);

        }

        public static void setContentDescToast(View view, CharSequence desc) {
            setContentDescToast(view, desc, true);
        }

        public static void setContentDescToast(View view, final CharSequence desc, final boolean vibrate) {
            if (desc.length() > 0) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Toast.makeText(view.getContext(), desc, Toast.LENGTH_SHORT).show();
                        if (vibrate) {
                            Device.vibrate(view.getContext(), 100);
                        }
                        return true;
                    }
                });
            }
        }


        public static BitmapDrawable resizeDrawable(Drawable drawable, int x, int y, Resources resources) {
            return new BitmapDrawable(resources, Bitmap.createScaledBitmap(
                    ((BitmapDrawable) drawable).getBitmap(), x, y, true));
        }

        public static Bitmap resizeBitmap(Bitmap bm, int newWidth, int newHeight) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
            bm.recycle();
            return resizedBitmap;
        }


        /**
         * Get center child in X Axes
         */
        public static View getCenterXChild(RecyclerView recyclerView) {
            int childCount = recyclerView.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View child = recyclerView.getChildAt(i);
                    if (isChildInCenterX(recyclerView, child)) {
                        return child;
                    }
                }
            }
            return null;
        }

        /**
         * Get position of center child in X Axes
         */
        public static int getCenterXChildPosition(RecyclerView recyclerView) {
            int childCount = recyclerView.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View child = recyclerView.getChildAt(i);
                    if (isChildInCenterX(recyclerView, child)) {
                        return recyclerView.getChildAdapterPosition(child);
                    }
                }
            }
            return childCount;
        }

        /**
         * Get center child in Y Axes
         */
        public static View getCenterYChild(RecyclerView recyclerView) {
            int childCount = recyclerView.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View child = recyclerView.getChildAt(i);
                    if (isChildInCenterY(recyclerView, child)) {
                        return child;
                    }
                }
            }
            return null;
        }

        /**
         * Get position of center child in Y Axes
         */
        public static int getCenterYChildPosition(RecyclerView recyclerView) {
            int childCount = recyclerView.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View child = recyclerView.getChildAt(i);
                    if (isChildInCenterY(recyclerView, child)) {
                        return recyclerView.getChildAdapterPosition(child);
                    }
                }
            }
            return childCount;
        }

        public static boolean isChildInCenterX(RecyclerView recyclerView, View view) {
            int childCount = recyclerView.getChildCount();
            int[] lvLocationOnScreen = new int[2];
            int[] vLocationOnScreen = new int[2];
            recyclerView.getLocationOnScreen(lvLocationOnScreen);
            int middleX = lvLocationOnScreen[0] + recyclerView.getWidth() / 2;
            if (childCount > 0) {
                view.getLocationOnScreen(vLocationOnScreen);
                if (vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + view.getWidth() >= middleX) {
                    return true;
                }
            }
            return false;
        }

        public static boolean isChildInCenterY(RecyclerView recyclerView, View view) {
            int childCount = recyclerView.getChildCount();
            int[] lvLocationOnScreen = new int[2];
            int[] vLocationOnScreen = new int[2];
            recyclerView.getLocationOnScreen(lvLocationOnScreen);
            int middleY = lvLocationOnScreen[1] + recyclerView.getHeight() / 2;
            if (childCount > 0) {
                view.getLocationOnScreen(vLocationOnScreen);
                if (vLocationOnScreen[1] <= middleY && vLocationOnScreen[1] + view.getHeight() >= middleY) {
                    return true;
                }
            }
            return false;
        }


        public static void addIconsToMenu(Menu menu) {
            if (menu != null) {
                if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                    try {
                        Method m = menu.getClass().getDeclaredMethod(
                                "setOptionalIconsVisible", Boolean.TYPE);
                        m.setAccessible(true);
                        m.invoke(menu, true);
                    } catch (Exception e) {
                        //Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                    }
                }
            }
        }

        public static Drawable getDrawable(Context context, int id) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return context.getResources().getDrawable(id, context.getTheme());
            } else {
                return context.getResources().getDrawable(id);
            }
        }

        public static int getColor(Context context, int id) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return context.getColor(id);
            } else {
                return context.getResources().getColor(id);
            }
        }


        public static void setOverscrollColor(Resources resources, Context context, int color) {

            int glowDrawableId = resources.getIdentifier("overscroll_glow", "drawable", "android");
            getDrawable(context, glowDrawableId).setColorFilter(color, PorterDuff.Mode.MULTIPLY);

            final int edgeDrawableId = resources.getIdentifier("overscroll_edge", "drawable", "android");
            getDrawable(context, edgeDrawableId).setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        }


        public static void setEnableViewGroup(ViewGroup layout, boolean enabled) {
            setEnableViewGroup(layout, enabled, 1);
        }

        public static void setEnableViewGroup(ViewGroup layout, boolean enabled, int recursive) {
            layout.setEnabled(enabled);
            int count = layout.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = layout.getChildAt(i);

                if (recursive > 1 && child instanceof ViewGroup) {
                    setEnableViewGroup((ViewGroup) child, enabled, --recursive);
                } else {
                    child.setEnabled(enabled);
                }

            }
        }


        /*
        public static void applyShadows(ViewGroup layout) {
            if (Build.VERSION.SDK_INT  < Build.VERSION_CODES.LOLLIPOP) {
                View view;

                if (layout.getChildCount() > 0) {
                    for (int i = 0; i < layout.getChildCount(); i++) {
                        view = layout.getChildAt(i);

                        if (view instanceof ViewGroup) {
                            applyShadows((ViewGroup)view);
                        } else {
                            applyShadow(view);
                        }
                    }
                }
            }
        }

        public static void applyShadows(View view) {
            if (Build.VERSION.SDK_INT  < Build.VERSION_CODES.LOLLIPOP) {
                if (view instanceof ViewGroup) {
                    applyShadows((ViewGroup)view);
                } else {
                    applyShadow(view);
                }
            }
        }
        */

        public static void applyShadow(View view, int elevation) {
            if (Build.VERSION.SDK_INT  < Build.VERSION_CODES.LOLLIPOP && elevation > 0) {
                Context context = view.getContext();
                ViewGroup parent = (ViewGroup) view.getParent();

                View shadow = new View(context);
                shadow.setBackground(getDrawable(context, R.drawable.shadow));

                if (parent instanceof RelativeLayout) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

                    params.addRule(RelativeLayout.ALIGN_BOTTOM, view.getId());
                    params.height = Convert.dpToPx(context, 8);
                    shadow.setLayoutParams(params);
                }

                shadow.setMinimumHeight(elevation);

                int index = parent.indexOfChild(view);
                parent.addView(shadow, index + 1);
            }
        }
    }

    public static class Animation {
        public static float getAnimatedFraction(ValueAnimator animator) {
            float fraction = ((float) animator.getCurrentPlayTime()) / animator.getDuration();
            fraction = Math.min(fraction, 1f);
            fraction = animator.getInterpolator().getInterpolation(fraction);
            return fraction;
        }
    }

    public static class Convert {

        public static int dpToPx(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        public static int pxToDp(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }
    }
}
