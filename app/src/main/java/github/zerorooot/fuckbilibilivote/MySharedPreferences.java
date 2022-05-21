package github.zerorooot.fuckbilibilivote;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    private final SharedPreferences sharedPreferences;

    public MySharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("bili", Context.MODE_WORLD_READABLE);
    }

    public void save(String key, String value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public String get(String key) {
        return sharedPreferences.getString(key, "");
    }
}
