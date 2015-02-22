package zcamara.washington.edu.awty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // For our recurring task, we'll just display a message
        Bundle mBundle = intent.getExtras();
        String phone = mBundle.getString("phone", "mBundle fail");
        String message = mBundle.getString("message", "MBundle fail");
        Toast.makeText(context, phone+": "+message, Toast.LENGTH_SHORT).show();
    }
}