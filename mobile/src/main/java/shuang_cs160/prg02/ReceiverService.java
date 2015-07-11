package shuang_cs160.prg02;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by Sarah on 7/9/2015.
 */
public class ReceiverService extends WearableListenerService {

    public static final String TAG = "ReceiverService";
    private static final String RECEIVER_SERVICE_PATH = "receiver_service";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "running WearableListenerService");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(RECEIVER_SERVICE_PATH)) {
            Log.i(TAG, "received message");

            TwitterSession twitterSession = Twitter.getSessionManager().getActiveSession();

            if (twitterSession != null) {
                Log.i(TAG, "opening camera");
                Intent startCamera = new Intent(getApplicationContext(), TweetExcited.class);
                startCamera.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startCamera);
            } else {
                Intent home = new Intent(getApplicationContext(), TwitterLogin.class);
                home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
            }
        }



    }
}
