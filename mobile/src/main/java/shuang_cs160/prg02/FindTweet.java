package shuang_cs160.prg02;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FindTweet extends Service {
    public FindTweet() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        findExcited();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    protected void findExcited() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        // Can also use Twitter directly: Twitter.getApiClient()
        SearchService tweetsService = twitterApiClient.getSearchService();
        tweetsService.tweets("#cs160excited", null, null, null, "recent", 15,
                null, null, null, true, new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {
                //Do something with result, which provides a Tweet inside of result.data
                List<Tweet> tweetList = result.data.tweets;
                TwitterSession userSession = Twitter.getSessionManager().getActiveSession();

                for (Tweet tweet : tweetList) {
                    if (tweet.user.id != userSession.getUserId()) {

                        String imageUrl = tweet.entities.media.get(0).mediaUrl;
                        String text = tweet.text;
                        long id = tweet.id;

                        Bitmap image = getBitmapFromURL(imageUrl);
                        showTweet(image, text, id);
                        break;
                    }
                }
            }

            public void failure(TwitterException exception) {
                //Do something on failure
            }
        });
    }

    private void showTweet(Bitmap tweetImg, String tweet, long tweetId) {

        Log.i("FindTweet", "sending notification");

        Intent findIntent = new Intent(this, ViewTweet.class);
        // Specify data you want this activity to have access to
        findIntent.putExtra("tweetId", tweetId);
        findIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent findPendingIntent = PendingIntent.getActivity(this, 0, findIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Create a WearableExtender to add functionality for wearables
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(tweetImg);

        // Create a NotificationCompat.Builder to build a standard notification
        // then extend it with the WearableExtender
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Check out other exciting things!")
                .setContentText(tweet)
                .setContentIntent(findPendingIntent)
                .extend(wearableExtender)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(tweetImg));

        int notificationId = 001;

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Build the notification and issue with manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private Bitmap getBitmapFromURL(String url) {
        try {
            URL src = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) src.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
