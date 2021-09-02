package com.usda.fmsc.android;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
public class AndroidUtils {
    public static class App {
        private static boolean playServicesAvailable;

        public static boolean isPackageInstalled(Context context, String packagename) {
            PackageManager pm = context.getPackageManager();
            try {
                pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }

        public static boolean isServiceRunning(Context ctx, Class serviceClass) {
            ActivityManager manager = (ActivityManager) ctx.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            if (manager != null) {
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if (serviceClass.getName().equals(service.service.getClassName())) {
                        return true;
                    }
                }
            }
            return false;
        }


        private static Intent getOpenFileIntent(Context context, String mimeType, String[] extraMimes, String initialUri) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            intent.setType(mimeType != null ? mimeType : "*/*");

            intent.addCategory(Intent.CATEGORY_OPENABLE);

            if (initialUri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri);
                }
            }

            if (extraMimes != null && extraMimes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimes);
            }

            // special intent for Samsung file manager
            Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            sIntent.putExtra("CONTENT_TYPE", mimeType != null ? mimeType : "*/*");
            sIntent.addCategory(Intent.CATEGORY_DEFAULT);

            Intent chooserIntent;
            if (context.getPackageManager().resolveActivity(sIntent, 0) != null) {
                // it is device with samsung file manager
                chooserIntent = Intent.createChooser(sIntent, "Open file");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
            } else {
                chooserIntent = Intent.createChooser(intent, "Open file");
            }

            return chooserIntent;
        }


        public static void openFileIntent(Activity activity, int resultCode) {
            openFileIntent(activity, null, null, null, resultCode);
        }

        public static void openFileIntent(Activity activity, String mimeType, int resultCode) {
            openFileIntent(activity, mimeType, null, null, resultCode);
        }

        public static void openFileIntent(Activity activity, String mimeType, String initialUri, int resultCode) {
            openFileIntent(activity, mimeType, null, initialUri, resultCode);
        }

        public static void openFileIntent(Activity activity, String mimeType, String[] extraMimes, String initialUri, int resultCode) {
            try {
                activity.startActivityForResult(getOpenFileIntent(activity, mimeType, extraMimes, initialUri), resultCode);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(activity.getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
            }
        }


//        public static void openFileIntentFromFragment(Fragment fragment, String mimeType, String initialUri, int resultCode) {
//            openFileIntentFromFragment(fragment, mimeType, null, initialUri, resultCode);
//        }
//
//        public static void openFileIntentFromFragment(Fragment fragment, String mimeType, int resultCode) {
//            openFileIntentFromFragment(fragment, mimeType, null, null, resultCode);
//        }
//
//        public static void openFileIntentFromFragment(Fragment fragment, int resultCode) {
//            openFileIntentFromFragment(fragment, null, null, null, resultCode);
//        }
//
//        public static void openFileIntentFromFragment(Fragment fragment, String mimeType, String[] extraMimes, String initialUri, int resultCode) {
//            try {
//                fragment.startActivityForResult(getOpenFileIntent(fragment.getActivity(), mimeType, extraMimes, initialUri), resultCode);
//            } catch (android.content.ActivityNotFoundException ex) {
//                Toast.makeText(fragment.getActivity(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
//            }
//        }


        public static int checkPlayServices(Activity activity, int resultCode) {
            int result = 0;

            if (!playServicesAvailable) {
                GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
                result = googleApi.isGooglePlayServicesAvailable(activity);

                if (result == ConnectionResult.SUCCESS || (!googleApi.isUserResolvableError(result) && !googleApi.showErrorDialogFragment(activity, result, resultCode))) {
                    playServicesAvailable = true;
                }
            }

            return result;
        }


        public static boolean checkPermission(Context context, String permission) {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        }

        public static boolean checkPermissions(Context context, String[] permissions) {
            for (String p : permissions) {
                if (ContextCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }

            return true;
        }


        public static boolean checkLocationPermission(Context context) {
            return checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        }

        @RequiresApi(29)
        public static boolean checkBackgroundLocationPermission(Context context) {
            return checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    checkPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        public static boolean checkNetworkPermission(Context context) {
            return checkPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) &&
                    checkPermission(context, Manifest.permission.INTERNET);
        }

        public static boolean checkStoragePermission(Context context) {
            return checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        public static boolean checkBluetoothPermission(Context context) {
            return checkPermission(context, Manifest.permission.BLUETOOTH) &&
                    checkPermission(context, Manifest.permission.BLUETOOTH_ADMIN);
        }

        public static boolean checkPhonePermission(Context context) {
            return checkPermission(context, Manifest.permission.READ_PHONE_STATE);
        }

        public static boolean checkCameraPermission(Context context) {
            return checkPermission(context, Manifest.permission.CAMERA);
        }



        public static boolean requestPermissionOld(final Activity activity, final String permission, final int requestCode, String explanation) {
            return requestPermissionOld(activity, new String[] { permission }, requestCode, explanation);
        }

        public static boolean requestPermissionOld(final Activity activity, final String[] permissions, final int requestCode, String explanation) {
            if (!checkPermissions(activity, permissions)) {
                if (explanation != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                    new AlertDialog.Builder(activity)
                            .setMessage(explanation)
                            .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(activity, permissions, requestCode))
                            .show();

                } else {
                    ActivityCompat.requestPermissions(activity, permissions, requestCode);
                }
            } else {
                return true;
            }

            return false;
        }

        public static boolean requestPermissionOld2(final ComponentActivity activity, final String permission, String explanation, ActivityResultCallback<Boolean> callback) {
            if (!checkPermission(activity, permission)) {
                ActivityResultLauncher<String> requestPermission = activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), callback);

            if (explanation != null) {
                new AlertDialog.Builder(activity)
                        .setMessage(explanation)
                        .setPositiveButton(R.string.str_ok, (dialog1, which) -> requestPermission.launch(permission))
                        .setNeutralButton(R.string.str_cancel, null)
                        .show();
            } else {
                requestPermission.launch(permission);
            }

            return false;
        }

            return true;
        }


        public static boolean requestPermission(final ComponentActivity activity, final String permission, ActivityResultLauncher<String> requestPermission, String explanation) {
            if (!checkPermission(activity, permission)) {

                if (explanation != null) {
                    new AlertDialog.Builder(activity)
                            .setMessage(explanation)
                            .setPositiveButton(R.string.str_ok, (dialog1, which) -> requestPermission.launch(permission))
                            .setNeutralButton(R.string.str_cancel, null)
                            .show();
                } else {
                    requestPermission.launch(permission);
                }

                return false;
            }

            return true;
        }

        public static boolean requestPermissions(final ComponentActivity activity, final String[] permissions, ActivityResultLauncher<String[]> requestPermissions, String explanation) {
            if (!checkPermissions(activity, permissions)) {

                if (explanation != null) {
                    new AlertDialog.Builder(activity)
                            .setMessage(explanation)
                            .setPositiveButton(R.string.str_ok, (dialog1, which) -> requestPermissions.launch(permissions))
                            .setNeutralButton(R.string.str_cancel, null)
                            .show();
                } else {
                    requestPermissions.launch(permissions);
                }

                return false;
            }

            return true;
        }


        @RequiresApi(29)
        public static boolean requestLocationPermission(final ComponentActivity activity, ActivityResultLauncher<String[]> requestPermissions, String explanation) {
            return requestPermissions(activity,
                    new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, requestPermissions, explanation);
        }

//
//        @RequiresApi(29)
//        public static boolean requestBackgroundLocationPermission(final ComponentActivity activity, ActivityResultLauncher<String[]> requestPermissions, String explanation) {
//            requestPermissions(activity,
//                    new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION }, requestPermissions, explanation);
//            return false;
//        }

        @RequiresApi(29)
        public static boolean requestBackgroundLocationPermission(final ComponentActivity activity, ActivityResultLauncher<String> requestPermission, String explanation) {
            return requestPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION, requestPermission, explanation);
        }

//        public static boolean requestNetworkPermission(final Activity activity, final int requestCode) {
//            return requestPermissionOld(activity,
//                    new String[] { Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.INTERNET},
//                    requestCode, null);
//        }
//
//        public static boolean requestStoragePermission(final Activity activity, final int requestCode) {
//            return requestPermissionOld(activity,
//                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
//                    requestCode, null);
//        }
//
//        public static boolean requestBluetoothPermission(final Activity activity, final int requestCode) {
//            return requestPermissionOld(activity,
//                    new String[] { Manifest.permission.BLUETOOTH , android.Manifest.permission.BLUETOOTH_ADMIN},
//                    requestCode, null);
//        }
//
//        public static boolean requestPhonePermission(final Activity activity, final int requestCode) {
//            return requestPermissionOld(activity,
//                    new String[] { Manifest.permission.READ_PHONE_STATE },
//                    requestCode, null);
//        }
//
//        public static boolean requestPhonePermission(final Activity activity, final int requestCode, String explanation) {
//            return requestPermissionOld(activity,
//                    new String[] { Manifest.permission.READ_PHONE_STATE },
//                    requestCode, explanation);
//        }
//
//        public static boolean requestCameraPermission(final Activity activity, final int requestCode) {
//            return requestPermissionOld(activity,
//                    new String[] { Manifest.permission.CAMERA },
//                    requestCode, null);
//        }



        public static void navigateAppStore(Context context, String packageName) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
        }


        public static String getAppVersion(Context context) {
            try {
                PackageInfo pi =  context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                return String.format("%s-%s", pi.versionName.substring(0, pi.versionName.indexOf('-')), pi.versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                //
            }

            return null;
        }




    }

    public static class Device {
        public static void vibrate(Context context, long millisecond) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(millisecond);
            }
        }

        public static void vibrate(Context context, long[] pattern) {
            vibrate(context, pattern, -1);
        }

        public static void vibrate(Context context, long[] pattern, int repeat) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(pattern, repeat);
            }
        }


        public static void playSound(Context context, int sound) {
            final MediaPlayer player = MediaPlayer.create(context, sound);

            player.setOnCompletionListener(mediaPlayer -> {
                player.reset();
                player.release();
            });

            player.start();
        }

        public static void playSound(Context context, Uri sound) {
            final MediaPlayer player = MediaPlayer.create(context, sound);

            player.setOnCompletionListener(mediaPlayer -> {
                player.reset();
                player.release();
            });

            player.start();
        }

        public static boolean isInternetAvailable(final Context context) {
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            } catch (Exception e) {
                return false;
            }
        }


//        public static String getAndroidID(Context context) {
//            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//        }

//        public static String getDeviceID(Context context) {
//            if (App.checkPhonePermission(context)) {
//                final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//                final String tmDevice, tmSerial, androidId;
//
//                if (tm != null) {
//                    tmDevice = tm.getDeviceId();
//                    tmSerial = tm.getSimSerialNumber();
//                    androidId = getAndroidID(context);
//
//                    return new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode()).toString();
//                }
//
//                throw new RuntimeException("Unable to get TELEPHONY_SERVICE");
//            } else {
//                throw new RuntimeException("No Phone Permission");
//            }
//        }


        public static boolean isFullOrientationAvailable(Context context) {
            PackageManager pm = context.getPackageManager();
            return pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS) && pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
        }

        public static boolean isCameraAvailable(Context context) {
            return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        }
    }

    public static class UI {
        public static final float ENABLED_ALPHA = 1.0f;
        public static final float DISABLED_ALPHA = 0.5f;

        public static final int ENABLED_ICON_ALPHA = (int)(ENABLED_ALPHA * 255);
        public static final int DISABLED_ICON_ALPHA = (int)(DISABLED_ALPHA * 255);
        
        public static void removeSelectionOnUnfocus(final EditText editText) {
            removeSelectionOnUnfocus(editText, false);
        }

        public static void removeSelectionOnUnfocus(final EditText editText, final boolean setEndSelected) {
            editText.setOnFocusChangeListener((view, hasFocus) -> {
                if (!hasFocus) {
                    editText.setSelection(setEndSelected ? editText.getText().length() : 0);
                    editText.clearFocus();
                }
            });
        }

        public static void hideKeyboardOnSelect(final View view, final EditText editText) {
            hideKeyboardOnSelect(view, editText, false);
        }

        public static void hideKeyboardOnSelect(final View view, final EditText editText, final boolean setEndSelected) {
            view.setOnClickListener(view1 -> {
                InputMethodManager imm = (InputMethodManager) view1.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }

                editText.setSelection(setEndSelected ? editText.getText().length() : 0);
                editText.clearFocus();
            });
        }


        public static void hideKeyboardOnTouch(final View view, final EditText editText) {
            hideKeyboardOnTouch(view, editText, false);
        }


        @SuppressLint("ClickableViewAccessibility")
        public static void hideKeyboardOnTouch(final View view, final EditText editText, final boolean setEndSelected) {
            view.setOnTouchListener((v, event) -> {
                if (editText.hasFocus()) {
                    InputMethodManager imm = (InputMethodManager) view.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    editText.setSelection(setEndSelected ? editText.getText().length() : 0);
                    editText.clearFocus();
                }
                return false;
            });
        }

        public static void hideKeyboardOnSelect(final View view, final EditText[] editTexts) {
            hideKeyboardOnSelect(view, editTexts, false);
        }

        public static void hideKeyboardOnSelect(final View view, final EditText[] editTexts, final boolean setEndSelected) {
            view.setOnClickListener(view1 -> {
                InputMethodManager imm = (InputMethodManager) view1.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }

                for (final EditText editText : editTexts) {
                    editText.setSelection(setEndSelected ? editText.getText().length() : 0);
                    editText.clearFocus();
                }
            });
        }

        public static void hideKeyboardOnTouch(final View view, final EditText[] editTexts) {
            hideKeyboardOnTouch(view, editTexts, false);
        }

        @SuppressLint("ClickableViewAccessibility")
        public static void hideKeyboardOnTouch(final View view, final EditText[] editTexts, final boolean setEndSelected) {
            view.setOnTouchListener(( View v, MotionEvent event) -> {
                InputMethodManager imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                for (final EditText editText : editTexts) {
                    editText.setSelection(setEndSelected ? editText.getText().length() : 0);
                    editText.clearFocus();
                }
                return false;
            });
        }

        public static void hideKeyboard(Activity activity) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
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
            setContentDescToast(view, view.getContentDescription(), false);
        }

        public static void setContentDescToast(View view, boolean vibrate) {
            setContentDescToast(view, view.getContentDescription(), vibrate);

        }

        public static void setContentDescToast(View view, CharSequence desc) {
            setContentDescToast(view, desc, false);
        }

        public static void setContentDescToast(View view, final CharSequence desc, final boolean vibrate) {
            if (desc.length() > 0) {
                view.setOnLongClickListener(view1 -> {
                    Toast.makeText(view1.getContext(), desc, Toast.LENGTH_SHORT).show();
                    if (vibrate) {
                        Device.vibrate(view1.getContext(), 100);
                    }
                    return true;
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

        public static Bitmap scaleBitmap(Bitmap realImage, float maxImageSize, boolean filter) {
            float ratio = Math.min(
                    maxImageSize / realImage.getWidth(),
                    maxImageSize / realImage.getHeight());
            int width = Math.round(ratio * realImage.getWidth());
            int height = Math.round(ratio * realImage.getHeight());

            return Bitmap.createScaledBitmap(realImage, width, height, filter);
        }

        public static Bitmap scaleMinBitmap(Bitmap realImage, float minImageSize, boolean filter) {
            float ratio = Math.min(
                    minImageSize / realImage.getWidth(),
                    minImageSize / realImage.getHeight());
            int width = Math.round(ratio * realImage.getWidth());
            int height = Math.round(ratio * realImage.getHeight());

            return Bitmap.createScaledBitmap(realImage, width, height, filter);
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

        public static View getChildAtPosition(final AdapterView view, final int position) {
            final int index = position - view.getFirstVisiblePosition();
            if ((index >= 0) && (index < view.getChildCount())) {
                return view.getChildAt(index);
            } else {
                return null;
            }
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
                return vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + view.getWidth() >= middleX;
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
                return vLocationOnScreen[1] <= middleY && vLocationOnScreen[1] + view.getHeight() >= middleY;
            }
            return false;
        }


        public static void addIconsToPopupMenu(PopupMenu popupMenu) {
            try {
                Field[] fields = popupMenu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popupMenu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper
                                .getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod(
                                "setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void addIconsToMenu(Menu menu) {
            if (menu != null) {
                if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                    try {
                        @SuppressLint("PrivateApi") Method m = menu.getClass().getDeclaredMethod(
                                "setOptionalIconsVisible", Boolean.TYPE);
                        m.setAccessible(true);
                        m.invoke(menu, true);
                    } catch (Exception e) {
                        //Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                    }
                }
            }
        }

        public static Drawable getDrawable(Context context, @DrawableRes int id) {
            return ContextCompat.getDrawable(context, id);
        }

        @ColorInt
        public static int getColor(Context context, @ColorRes int id) {
            return ContextCompat.getColor(context, id);
        }

        public static void setOverscrollColor(Resources resources, Context context, @ColorRes  int resColorId) {
            int color = getColor(context, resColorId);

            int glowDrawableId = resources.getIdentifier("overscroll_glow", "drawable", "android");
            getDrawable(context, glowDrawableId).setColorFilter(color, PorterDuff.Mode.MULTIPLY);

            final int edgeDrawableId = resources.getIdentifier("overscroll_edge", "drawable", "android");
            getDrawable(context, edgeDrawableId).setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        }

        public static void setHomeIndicatorIcon(AppCompatActivity activity, @DrawableRes int drawable) {
            ActionBar actionBar = activity.getSupportActionBar();

            if (actionBar != null){
                actionBar.setHomeAsUpIndicator(drawable);
            } else {
                throw new RuntimeException("SupportActionBar not set.");
            }
        }


        public static void setSnackbarTextColor(Snackbar snackbar, @ColorInt int color) {
            ((TextView) (snackbar.getView().findViewById(R.id.snackbar_text))).setTextColor(color);
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

        public static void setNumberPickerColor(NumberPicker picker, @ColorRes int colorId) {
            int color = getColor(picker.getContext(), colorId);

            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        ColorDrawable colorDrawable = new ColorDrawable(color);
                        pf.set(picker, colorDrawable);
                    } catch (Exception e) {
                        //
                    }
                    break;
                }
            }
        }

        public static void setTextSizeForWidth(Paint paint, float desiredWidth, String text) {
            final float testTextSize = 48f;

            // Get the bounds of the text, using our testTextSize.
            paint.setTextSize(testTextSize);
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);

            // Calculate the desired size as a proportion of our testTextSize.
            float desiredTextSize = testTextSize * desiredWidth / bounds.width();

            // Set the paint for that size.
            paint.setTextSize(desiredTextSize);
        }


        public static void enableButton(Button button) {
            button.setEnabled(true);
            button.setAlpha(ENABLED_ALPHA);
        }

        public static void disableButton(Button button) {
            button.setEnabled(false);
            button.setAlpha(DISABLED_ALPHA);
        }


        public static void enableMenuItem(MenuItem menuItem) {
            menuItem.setEnabled(true);

            Drawable icon = menuItem.getIcon();

            if (icon != null) {
                icon.mutate().setAlpha(ENABLED_ICON_ALPHA);
            }
        }

        public static void enableMenuItem(MenuItem menuItem, int id) {
            menuItem.setEnabled(true);

            try {
                menuItem.setIcon(id);
                menuItem.getIcon().mutate().setAlpha(ENABLED_ICON_ALPHA);
            } catch (Exception e) {
                //
            }
        }

        public static void enableMenuItem(MenuItem menuItem, Drawable drawable) {
            menuItem.setEnabled(true);

            try {
                drawable.mutate().setAlpha(ENABLED_ICON_ALPHA);
                menuItem.setIcon(drawable);
            } catch (Exception e) {
                //
            }
        }


        public static void disableMenuItem(MenuItem menuItem) {
            menuItem.setEnabled(false);

            Drawable icon = menuItem.getIcon();

            if (icon != null) {
                icon.mutate().setAlpha(DISABLED_ICON_ALPHA);
            }
        }

        public static void disableMenuItem(MenuItem menuItem, int id) {
            menuItem.setEnabled(false);

            try {
                menuItem.setIcon(id);
                menuItem.getIcon().mutate().setAlpha(DISABLED_ICON_ALPHA);
            } catch (Exception e) {
                //
            }
        }

        public static void disableMenuItem(MenuItem menuItem, Drawable drawable) {
            menuItem.setEnabled(false);

            try {
                drawable.mutate().setAlpha(DISABLED_ICON_ALPHA);
                menuItem.setIcon(drawable);
            } catch (Exception e) {
                //
            }
        }

        public static void createToastForToolbarTitle(final Activity activity, Toolbar toolbar) {
            createToastForToolbarTitle(activity, toolbar, null);
        }

        public static void createToastForToolbarTitle(final Activity activity, final Toolbar toolbar, final String text) {
            try {
                Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
                f.setAccessible(true);

                final TextView titleView = (TextView) f.get(toolbar);

                titleView.setOnLongClickListener(v -> {
                    Toast.makeText(activity, text == null ? titleView.getText() : text, Toast.LENGTH_SHORT).show();
                    return false;
                });
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

/*    public static class Internal {
        @SuppressWarnings("JavaReflectionInvocation")
        public static void registerOnActivityDestroyListener(Object obj, PreferenceManager preferenceManager) {
            try {
                @SuppressLint("PrivateApi") Method method = preferenceManager.getClass().getDeclaredMethod(
                        "registerOnActivityDestroyListener",
                        PreferenceManager.OnActivityDestroyListener.class);
                method.setAccessible(true);
                method.invoke(preferenceManager, obj);
            } catch (Exception e) {
                //
            }
        }
    }*/

    public static class Animation {
        public static float getAnimatedFraction(ValueAnimator animator) {
            float fraction = ((float) animator.getCurrentPlayTime()) / animator.getDuration();
            fraction = Math.min(fraction, 1f);
            fraction = animator.getInterpolator().getInterpolation(fraction);
            return fraction;
        }

        public static void collapseTextView(TextView tv, int maxCollapsedLines) {
            //animateTextLines(tv, maxCollapsedLines);
            final int height = tv.getMeasuredHeight();
            tv.setHeight(0);
            tv.setMaxLines(maxCollapsedLines); //expand fully
            tv.measure(View.MeasureSpec.makeMeasureSpec(tv.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            final int newHeight = tv.getMeasuredHeight();
            ObjectAnimator animation = ObjectAnimator.ofInt(tv, "height", height, newHeight);
            animation.setDuration(300).start();
            tv.setEllipsize(TextUtils.TruncateAt.END);
        }

        public static void expandTextView(TextView tv) {
            //animateTextLines(tv, Integer.MAX_VALUE);
            final int height = tv.getMeasuredHeight();
            tv.setHeight(height);
            tv.setMaxLines(Integer.MAX_VALUE); //expand fully
            tv.measure(View.MeasureSpec.makeMeasureSpec(tv.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            final int newHeight = tv.getMeasuredHeight();
            ObjectAnimator animation = ObjectAnimator.ofInt(tv, "height", height, newHeight);
            animation.setDuration(300).start();
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

        public static float rgbToHsvHue(@ColorInt int color) {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            return hsv[0];
        }
    }

    public static class Files {
        private static final int BUFFER = 1024;

        public static Uri getUri(Activity activity, String appId, String filePath) {
            return getUri(activity, appId, new File(filePath));
        }

        public static Uri getUri(Activity activity, String appId, File file) {
            return FileProvider.getUriForFile(activity,
                    appId + ".provider",
                    file);
        }


        public static Uri getDocumentsUri(Context context) {
            return getDocumentsUri(context, null);
        }

        public static Uri getDocumentsUri(Context context, String subDir) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
            {
                StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

                Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                String startDir = "Documents";

                Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

                String scheme = uri.toString();

                scheme = scheme.replace("/root/", "/document/");

                //scheme += "%3A" + startDir;
                scheme += "/" + startDir;

                if (subDir != null) {
                    scheme += "%3A" + subDir.replace("/" ,"%3A");
                    scheme += "/" + subDir;
                }

                return Uri.parse(scheme);
            }

            return null;
        }


        public static boolean fileOrFolderExistsInTree(Context context, Uri tree, String path) {
            DocumentFile childDoc = getDocumentFromTree(context, tree, path);

            return childDoc != null && childDoc.exists();
        }

        public static DocumentFile getDocumentFromTree(Context context, Uri tree, String path) {
            String id = DocumentsContract.getTreeDocumentId(tree);

            if (!path.startsWith("/"))
                id = id + "/" + path;
            else
                id = id + path;

            Uri childUri = DocumentsContract.buildDocumentUriUsingTree(tree, id);
            return DocumentFile.fromSingleUri(context, childUri);
        }


        public static void copyFile(Context context, Uri source, Uri dest) throws IOException {
            ContentResolver resolver = context.getContentResolver();

            InputStream input = resolver.openInputStream(source);
            OutputStream output = resolver.openOutputStream(dest);

            byte[] buffer = new byte[1024];
            while (input.read(buffer, 0, buffer.length) >= 0){
                output.write(buffer, 0, buffer.length);
            }
        }


        public static boolean fileExists(Context context, Uri filePath) {
            if (filePath == null)
                return false;

            try {
                context.getContentResolver().openInputStream(filePath).available();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        public static boolean fileExists(Context context, String filePath) {
            if (filePath == null) throw new NullPointerException("filePath");
            File file = new File(filePath);
            return file.exists() && file.isFile();
        }


        public static boolean folderExists(Context context, Uri uri) {
            return DocumentFile.fromTreeUri(context, uri).exists();
        }
    }

//    public static class Media {
//        public static Bitmap rotateImage(Bitmap bitmap) {
//            ExifInterface ei = new ExifInterface(bitmap);
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_UNDEFINED);
//
//            Bitmap rotatedBitmap = null;
//            switch(orientation) {
//
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotatedBitmap = rotateImage(bitmap, 90);
//                    break;
//
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotatedBitmap = rotateImage(bitmap, 180);
//                    break;
//
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotatedBitmap = rotateImage(bitmap, 270);
//                    break;
//
//                case ExifInterface.ORIENTATION_NORMAL:
//                default:
//                    rotatedBitmap = bitmap;
//            }
//
//            return bitmap;
//        }
//    }
}
