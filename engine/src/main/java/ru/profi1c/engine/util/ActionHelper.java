package ru.profi1c.engine.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;

import ru.profi1c.engine.Dbg;

public final class ActionHelper {

    private static final String URL_SCHEME_TEL = "tel:";
    private static final String URL_SCHEME_MAILTO = "mailto:";
    private static final String MIME_TYPE_TEXT_PLAIN = "text/plain";

    private static void openIntent(Context context, Intent i) {
        try {
            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            Dbg.printStackTrace(e);
        }
    }

    /**
     * Open dialler
     *
     * @param context
     * @param url     phone scheme as "tel:+79050000000"
     */
    public static void dial(Context context, final String url) {
        openIntent(context, new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
    }

    /**
     * Parse url (and send a mail)
     *
     * @param context
     * @param url     mail scheme as "maito:test@mail.ru"
     */
    public static void sendMal(Context context, final String url) {

        MailTo mt = MailTo.parse(url);
        if (mt != null) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType(MIME_TYPE_TEXT_PLAIN);
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{mt.getTo()});
            i.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
            i.putExtra(Intent.EXTRA_CC, mt.getCc());
            i.putExtra(Intent.EXTRA_TEXT, mt.getBody());

            openIntent(context, i);
        }
    }

    public static void openUrl(Context context, final String url) {
        openIntent(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /**
     * Detect url action (tel, mail, view) and open it
     *
     * @param context
     * @param url
     */
    public static void actionUrl(Context context, final String url) {
        if (url.startsWith(URL_SCHEME_TEL)) {
            dial(context, url);
        } else if (url.startsWith(URL_SCHEME_MAILTO)) {
            sendMal(context, url);
        } else {
            openUrl(context, url);
        }
    }

    private ActionHelper() {
    }
}
