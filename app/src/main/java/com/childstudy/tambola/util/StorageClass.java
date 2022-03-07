package com.childstudy.tambola.util;

import static android.content.Context.MODE_PRIVATE;
import android.app.Activity;
import android.content.SharedPreferences;

public class StorageClass {

    public static void saveName( String name, Activity activity)
    {
        SharedPreferences.Editor editor = activity.getSharedPreferences("USERNAME", MODE_PRIVATE).edit();
        editor.putString ("user_name", name);
        editor.apply();
    }
   public static String getName(Activity activity)
    {
        SharedPreferences sharedPref = activity.getSharedPreferences("USERNAME", MODE_PRIVATE);
        String defaultValue = "default_value";
        return sharedPref.getString ("user_name", defaultValue);
    }
}
