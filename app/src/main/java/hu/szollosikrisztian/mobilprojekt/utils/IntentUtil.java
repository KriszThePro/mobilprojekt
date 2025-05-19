package hu.szollosikrisztian.mobilprojekt.utils;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public final class IntentUtil {

    private IntentUtil() { }

    public static void navigate(AppCompatActivity currentActivity, Class<? extends AppCompatActivity> targetActivityClass) {
        LogUtil.d(currentActivity, "Navigating to " + targetActivityClass.getSimpleName());

        Intent intent = new Intent(currentActivity, targetActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }
}
