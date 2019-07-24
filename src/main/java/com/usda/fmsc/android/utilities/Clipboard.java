package com.usda.fmsc.android.utilities;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Clipboard {
    private static final String TEXT = "Text";
    private static final String URI = "Uri";
    private static final String HTML = "Html";
    private static final String INTENT = "Intent";

    private static ClipboardManager clipboard;

    private static boolean init(Context context) {
        if (clipboard == null)
            clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return clipboard != null;
    }


    public static boolean copyText(Context context, String text) {
        return copyText(context, TEXT, text);
    }

    public static boolean copyText(Context context, String label, String text) {
        if (init(context)) {
            clipboard.setPrimaryClip(ClipData.newPlainText(label, text));
            return true;
        }
        return false;
    }


    public static boolean copyUri(Context context, Uri uri) {
        return copyUri(context, URI, uri);
    }

    public static boolean copyUri(Context context, String label, Uri uri) {
        if (init(context)) {
            clipboard.setPrimaryClip(ClipData.newUri(context.getContentResolver(), label, uri));
            return true;
        }
        return false;
    }


    public static boolean copyIntent(Context context, Intent intent) {
        return copyIntent(context, INTENT, intent);
    }

    public static boolean copyIntent(Context context, String label, Intent intent) {
        if (init(context)) {
            clipboard.setPrimaryClip(ClipData.newIntent(label, intent));
            return true;
        }
        return false;
    }


    public static boolean copyHtml(Context context, String text, String html) {
        return copyHtml(context, HTML, text, html);
    }

    public static boolean copyHtml(Context context, String label, String text, String html) {
        if (init(context)) {
            clipboard.setPrimaryClip(ClipData.newHtmlText(label, text, html));
            return true;
        }
        return false;
    }



    public static CharSequence pasteText() {
        if (clipboard != null && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
            return clipboard.getPrimaryClip().getItemAt(0).getText();
        }
        return null;
    }

    public static CharSequence pasteText(String label) {
        if (clipboard != null && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
            ClipData item = clipboard.getPrimaryClip();
            if (label.contentEquals(item.getDescription().getLabel())) {
                return item.getItemAt(0).getText();
            }
        }
        return null;
    }


    public static Uri pasteUri() {
        if (clipboard != null && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST)) {
            return clipboard.getPrimaryClip().getItemAt(0).getUri();
        }
        return null;
    }

    public static Uri pasteUri(String label) {
        if (clipboard != null && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST)) {
            ClipData item = clipboard.getPrimaryClip();
            if (label.contentEquals(item.getDescription().getLabel())) {
                return item.getItemAt(0).getUri();
            }
        }
        return null;
    }


    public static String pasteHtml() {
        if (clipboard != null && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
            return clipboard.getPrimaryClip().getItemAt(0).getHtmlText();
        }
        return null;
    }

    public static String pasteHtml(String label) {
        if (clipboard != null && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
            ClipData item = clipboard.getPrimaryClip();
            if (label.contentEquals(item.getDescription().getLabel())) {
                return item.getItemAt(0).getHtmlText();
            }
        }
        return null;
    }


    public static CharSequence pasteHtmlText() {
        if (clipboard != null && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
            return clipboard.getPrimaryClip().getItemAt(0).getText();
        }
        return null;
    }

    public static CharSequence pasteHtmlText(String label) {
        if (clipboard != null && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
            ClipData item = clipboard.getPrimaryClip();
            if (label.contentEquals(item.getDescription().getLabel())) {
                return item.getItemAt(0).getText();
            }
        }
        return null;
    }


    public static Intent pasteIntent() {
        if (clipboard != null && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_INTENT)) {
            return clipboard.getPrimaryClip().getItemAt(0).getIntent();
        }
        return null;
    }

    public static Intent pasteIntent(String label) {
        if (clipboard != null && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_INTENT)) {
            ClipData item = clipboard.getPrimaryClip();
            if (label.contentEquals(item.getDescription().getLabel())) {
                return item.getItemAt(0).getIntent();
            }
        }
        return null;
    }
}
