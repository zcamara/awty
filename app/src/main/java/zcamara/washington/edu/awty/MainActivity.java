package zcamara.washington.edu.awty;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

    private boolean running;
    private PendingIntent pendingIntent;
    private String phone;
    private String interval;
    private String message;
    private AlarmManager alarmManager;
    private Intent alarmIntent;
    private Bundle mBundle;

    @Override
    protected void onDestroy() {
        if(!running) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button startBtn = (Button) findViewById(R.id.startBtn);
        final EditText intervalTxt = (EditText) findViewById(R.id.interval);
        final EditText phoneTxt = (EditText) findViewById(R.id.phoneNum);
        final EditText messageTxt = (EditText) findViewById(R.id.message);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        messageTxt.setText("Are we there yet?");

        boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 0,
                new Intent(MainActivity.this, AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp)
        {
            startBtn.setText("Stop");
            running = true;
        } else {
            alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            running = false;
        }

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    cancel();
                    startBtn.setText("Start");
                } else if(conditionsMet(intervalTxt, phoneTxt, messageTxt)) {
                    phone = phoneTxt.getText().toString();
                    phone = phone.replaceAll("\\D+", "");
                    phone = phone.substring(0,3) + "-" + phone.substring(3,6) +"-"+ phone.substring(6);
                    phone = "(" + phone.substring(0,3) + ") " + phone.substring(4);
                    phoneTxt.setText(phone);
                    interval = intervalTxt.getText().toString();
                    message = messageTxt.getText().toString();
                    start(Integer.parseInt(interval));
                    startBtn.setText("Stop");
                }
            }
        });
    }

    //Use onSaveInstanceState(Bundle) and onRestoreInstanceState
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        super.onSaveInstanceState(savedInstanceState);
        EditText phoneTxt = (EditText) findViewById(R.id.phoneNum);
        EditText intervalTxt = (EditText) findViewById(R.id.interval);
        EditText messageTxt = (EditText) findViewById(R.id.message);
        phone = phoneTxt.getText().toString();
        interval = intervalTxt.getText().toString();
        message = messageTxt.getText().toString();
        savedInstanceState.putString("phone", phone);
        savedInstanceState.putString("interval", interval);
        savedInstanceState.putString("message", message);
        savedInstanceState.putBundle("mBundle", mBundle);
    }

    //onRestoreInstanceState
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        phone = savedInstanceState.getString("phone");
        interval = savedInstanceState.getString("interval");
        message = savedInstanceState.getString("message");
        mBundle = savedInstanceState.getBundle("mBundle");
        EditText phoneTxt = (EditText) findViewById(R.id.phoneNum);
        EditText intervalTxt = (EditText) findViewById(R.id.interval);
        EditText messageTxt = (EditText) findViewById(R.id.message);
        phoneTxt.setText(phone);
        intervalTxt.setText(interval);
        messageTxt.setText(message);
    }

    private boolean conditionsMet(EditText interval, EditText phone, EditText message) {
        if(!phone.getText().toString().isEmpty()) {
            String phoneNum = phone.getText().toString();
            phoneNum = phoneNum.replaceAll("\\D+", "");
            phone.setText(phoneNum);
            if(phoneNum.length() != 10) {
                Toast.makeText(this, "Please enter 10 digits", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(interval.getText().toString().isEmpty() || message.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill in the required fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void start(int intervals) {
        running = true;
        alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        mBundle = new Bundle();
        mBundle.putString("phone", phone);
        mBundle.putString("interval", interval);
        mBundle.putString("message", message);
        alarmIntent.putExtras(mBundle);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (intervals * 1000 * 60), pendingIntent);
        Toast.makeText(this, "Alarm Set for every: "+interval+" minute(s)", Toast.LENGTH_SHORT).show();
    }

    public void cancel() {
        if(pendingIntent == null) {
            alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        running = false;
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
        alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
