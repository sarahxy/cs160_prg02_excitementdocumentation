package shuang_cs160.prg02;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class SendingService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private long lastAccel = 0;

    public SendingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        // setting accelerometer sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor  = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        Log.i("SendingService", "sensor listening");

        return START_STICKY;
    }

    /**
     * The method onSensorChanged() senses changes in acceleration (jumping up and down would be 9.8),
     * and determine whether to send a notification.
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (System.currentTimeMillis() - lastAccel > 200000) {
            Log.i("SendingService", "sensor changed");
            if ((Math.abs(event.values[0]) > 9.8) || (Math.abs(event.values[1]) > 9.8) || (Math.abs(event.values[2]) > 9.8)) {
                Log.i("SendingService", "accelerated!");
                lastAccel = System.currentTimeMillis();
                alertUser();
            }
        }
        Log.i("SendingService", "exit sensor");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        return;
    }

    private void alertUser() {

        Log.i("SendingService", "processing notification");

        Intent notifIntent = new Intent(this, Excitement.class);
        // Specify data you want this activity to have access to
        PendingIntent notifPendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);

        int notificationId = 001;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(SendingService.this)
                .setContentTitle("Excited?")
                .setContentText("Record it!")
                .setContentIntent(notifPendingIntent);

        // Initialize GoogleApiFinder in FindCameraNode class and look for handheld camera
        Intent camIntent = new Intent(getApplicationContext(), FindCameraNode.class);
        PendingIntent camPendingIntent = PendingIntent.getService(getApplicationContext(), 0, camIntent, 0);

        NotificationCompat.Action openCamera = new NotificationCompat.Action.Builder(
                R.drawable.dslr, "Take a pic", camPendingIntent).extend(new NotificationCompat.Action.WearableExtender()
                .setAvailableOffline(false)).build();

        notificationBuilder.addAction(openCamera);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Build the notification and issue with manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
        Log.i("SendingService", "sending notification");
    }

    /**
     * The method onDestroy() closes the sensor
     */
    @Override
    public void onDestroy() {
        Log.i("SendingService", "onDestroy() called");
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // new UnsupportedOperationException("Not yet implemented");
        return null;
    }

}

