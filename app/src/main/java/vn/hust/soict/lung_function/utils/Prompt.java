package vn.hust.soict.lung_function.utils;

import android.content.Context;
import android.widget.Toast;

public class Prompt {
    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void show(Context context, int stringId) {
        Toast.makeText(context, context.getString(stringId), Toast.LENGTH_LONG).show();
    }
}
