package aub.hopin;

import android.content.Context;

/**
 * Created by cmps on 4/24/16.
 */
public class GlobalContext {
    private static Context context;

    public static void init(Context ctx) {
        context = ctx;
    }

    public static Context get() {
        return context;
    }
}
