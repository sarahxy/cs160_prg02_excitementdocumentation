package shuang_cs160.prg02;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FindCameraNode extends IntentService {

    private GoogleApiClient mGoogleApiClient;

    private static final String CAPABILITY_NAME = "CAMERA";
    private static final String RECEIVER_SERVICE_PATH = "receiver_service";

    private Set<Node> nodes = null;
    private Node bestNode = null;

    public FindCameraNode() {
        super("FindCameraNode");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("FindCameraNode", "starting GoogleAPIClient");

        // setting capabilities and GoogleAPIClient
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        // Do something
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                        // Do something
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        // Do something
                    }
                })
                .addApi(Wearable.API)
                .build();
        this.mGoogleApiClient.connect();

        CapabilityApi.GetCapabilityResult capResult = Wearable.CapabilityApi.getCapability(mGoogleApiClient,
                CAPABILITY_NAME, CapabilityApi.FILTER_REACHABLE).await();

        Log.i("FindCameraNode", "capResult empty: " + capResult.getCapability().getNodes().isEmpty());
        //String bestNode = pickBestNodeId(capResult.getCapability().getNodes());

        nodes = capResult.getCapability().getNodes();
        if (nodes.size() > 0) {
            bestNode = nodes.iterator().next();
        }

        Log.i("FindCameraNode", "sending message...with node " + bestNode.getId());
        Wearable.MessageApi.sendMessage(mGoogleApiClient, bestNode.getId(), RECEIVER_SERVICE_PATH, null);
    }
}
