package shuang_cs160.prg02;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TweetExcited extends ActionBarActivity {

    public static final int CAMERA_REQUEST = 100;
    public static final int TWEET_REQUEST = 110;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri snapExcited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("TweetExcited", "TweetExcited class called!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_excited);

        captureImage();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_excited, menu);
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

    private void captureImage() {
        Log.i("TweetExcited", "camera method opened");
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        snapExcited = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, snapExcited);
        Log.i("TweetExcited", "starting to open camera");
        startActivityForResult(openCamera, CAMERA_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // did the user choose ok, if so, the code inside these curly braces will execute
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                // WE ARE HEARING BACK FROM THE CAMERA
                Bitmap cameraImage = (Bitmap) data.getExtras().get("data");
                // at this point we have the image from the camera

                Intent tweetExcited = new TweetComposer.Builder(this)
                        .text("#cs160excited")
                        .image(snapExcited)
                        .createIntent();

                startActivityForResult(tweetExcited, TWEET_REQUEST);

            }
        } else if (requestCode == TWEET_REQUEST) {
            Intent findTweet = new Intent(this, FindTweet.class);
            startService(findTweet);
            finish();
        } else {
            finish();
        }
    }

    /**
     * The methods getOutputMediaFileUri() and getOutputMediaFile() work in conjunction to save
     *
     * @param type
     * @return
     */

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "TweetExcited");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("TweetExcited", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
}
