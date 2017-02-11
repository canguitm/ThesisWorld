package com.example.sony.test3;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activity_offline_simple extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // JSON encoding/decoding
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

    // UI elements
    private static MapView mapView;
    private MapboxMap map;
    private ProgressBar progressBar;


    private boolean isEndNotified;
    private int regionSelected;

    // Offline objects
    private OfflineManager offlineManager;
    private OfflineRegion offlineRegion;
    private Button downloadButton;

    //Location
    private FloatingActionButton floatingActionButton;
    private static LocationServices locationServices;
    private static final int PERMISSIONS_LOCATION = 0;

    static DatabaseHelper myDb;

    private Button btn, requestBtn;

    static String getLong, getLat, result;

    MainActivity getSms;

    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    IconFactory iconFactory;

    Drawable green_weather, green_accident, green_unknown;
    Icon icon_green_weather, icon_green_accident, icon_green_unknown;

    Drawable yellow_weather, yellow_unknown, yellow_accident;
    Icon icon_yellow_weather,icon_yellow_unknown, icon_yellow_accident;

    Drawable red_weather, red_unknown, red_accident;
    Icon icon_red_weather, icon_red_unknown, icon_red_accident;

    private Context mContext;
    private Activity mActivity;

    private RelativeLayout mRelativeLayout;
    private Button mButton;

    private PopupWindow mPopupWindow;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox
        // token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        MapboxAccountManager.start(this, getString(R.string.access_token));

        // This contains the MapView in XML and needs to be called after the account manager
        setContentView(R.layout.activity_offline_simple);

        iconFactory = IconFactory.getInstance(activity_offline_simple.this);
        //GREEN
        green_weather = ContextCompat.getDrawable(activity_offline_simple.this, R.drawable.green_weather_marker);
        green_unknown = ContextCompat.getDrawable(activity_offline_simple.this, R.drawable.green_unknown_marker);
        green_accident = ContextCompat.getDrawable(activity_offline_simple.this, R.drawable.green_accident_marker);
        icon_green_weather = iconFactory.fromDrawable(green_weather);
        icon_green_unknown = iconFactory.fromDrawable(green_unknown);
        icon_green_accident = iconFactory.fromDrawable(green_accident);

        //YELLOW
        yellow_weather = ContextCompat.getDrawable(activity_offline_simple.this, R.drawable.yellow_weather_marker);
        yellow_unknown = ContextCompat.getDrawable(activity_offline_simple.this, R.drawable.yellow_unknown_marker);
        yellow_accident = ContextCompat.getDrawable(activity_offline_simple.this, R.drawable.yellow_accident_marker);
        icon_yellow_weather = iconFactory.fromDrawable(yellow_weather);
        icon_yellow_unknown = iconFactory.fromDrawable(yellow_unknown);
        icon_yellow_accident = iconFactory.fromDrawable(yellow_accident);

        //RED
        red_weather = ContextCompat.getDrawable(activity_offline_simple.this, R.drawable.red_weather_marker);
        red_unknown = ContextCompat.getDrawable(activity_offline_simple.this, R.drawable.red_unknown_marker);
        red_accident = ContextCompat.getDrawable(activity_offline_simple.this, R.drawable.red_accident_marker);
        icon_red_weather = iconFactory.fromDrawable(red_weather);
        icon_red_unknown = iconFactory.fromDrawable(red_unknown);
        icon_red_accident = iconFactory.fromDrawable(red_accident);

        getSms = new MainActivity();
        //Check Location
        if(checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }
        // Set up the MapView
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;


            }
        });

        // Assign progressBar for later use
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Set up the offlineManager
        offlineManager = OfflineManager.getInstance(this);

        checker();

        locationServices = LocationServices.getLocationServices(activity_offline_simple.this);


        //Get current GPS
        floatingActionButton = (FloatingActionButton) findViewById(R.id.location_toggle_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    toggleGps(!map.isMyLocationEnabled());
                }
            }
        });

        btn = (Button)findViewById(R.id.report);
        requestBtn = (Button)findViewById(R.id.request);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_offline_simple.this, MainActivity.class));
            }
        });

        myDb = new DatabaseHelper(this);

    }



    // Bottom navigation bar button clicks are handled here.
    // Download offline button


    // Override Activity lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        update_location();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void downloadRegionDialog() {
        // Set up download interaction. Display a dialog
        // when the user clicks download button and require
        // a user-provided region name
        AlertDialog.Builder builder = new AlertDialog.Builder(activity_offline_simple.this);



        // Build the dialog box
        builder.setTitle("Download the CDO Map")
                .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String regionName = "CDO";
                        // Require a region name to begin the download.
                        // If the user-provided string is empty, display
                        // a toast message and do not begin download.
                        if (regionName.length() == 0) {
                            Toast.makeText(activity_offline_simple.this, "Region name cannot be empty.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Begin download process
                            downloadRegion(regionName);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Display the dialog
        builder.show();
    }

    private void downloadRegion(final String regionName) {
        // Define offline region parameters, including bounds,
        // min/max zoom, and metadata

        // Start the progressBar
        startProgress();

        // Create offline definition using the current
        // style and boundaries of visible map area
        String styleUrl = map.getStyleUrl();
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        double minZoom = map.getCameraPosition().zoom;
        double maxZoom = map.getMaxZoom();
        float pixelRatio = this.getResources().getDisplayMetrics().density;
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                styleUrl, bounds, minZoom, maxZoom, pixelRatio);

        // Build a JSONObject using the user-defined offline region title,
        // convert it into string, and use it to create a metadata variable.
        // The metadata varaible will later be passed to createOfflineRegion()
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, regionName);
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception exception) {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }

        // Create the offline region and launch the download
        offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
            @Override
            public void onCreate(OfflineRegion offlineRegion) {
                Log.d(TAG, "Offline region created: " + regionName);
                activity_offline_simple.this.offlineRegion = offlineRegion;
                launchDownload();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error: " + error);
            }
        });
    }

    private void launchDownload() {
        // Set up an observer to handle download progress and
        // notify the user when the region is finished downloading
        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
            @Override
            public void onStatusChanged(OfflineRegionStatus status) {
                // Compute a percentage
                double percentage = status.getRequiredResourceCount() >= 0
                        ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                        0.0;

                if (status.isComplete()) {
                    // Download complete
                    endProgress("Region downloaded successfully.");
                    return;
                } else if (status.isRequiredResourceCountPrecise()) {
                    // Switch to determinate state
                    setPercentage((int) Math.round(percentage));
                }

                // Log what is being currently downloaded
                Log.d(TAG, String.format("%s/%s resources; %s bytes downloaded.",
                        String.valueOf(status.getCompletedResourceCount()),
                        String.valueOf(status.getRequiredResourceCount()),
                        String.valueOf(status.getCompletedResourceSize())));
            }

            @Override
            public void onError(OfflineRegionError error) {
                Log.e(TAG, "onError reason: " + error.getReason());
                Log.e(TAG, "onError message: " + error.getMessage());
            }

            @Override
            public void mapboxTileCountLimitExceeded(long limit) {
                Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
            }
        });

        // Change the region state
        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
    }


    // Progress bar methods
    private void startProgress() {
        // Disable buttons

        // Start and show the progress bar
        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }

    private void endProgress(final String message) {
        // Don't notify more than once
        if (isEndNotified) {
            return;
        }

        // Enable buttons

        // Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);

        // Show a toast
        Toast.makeText(activity_offline_simple.this, message, Toast.LENGTH_LONG).show();
    }

    private void checker() {
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(final OfflineRegion[] offlineRegions) {
                // Check result. If no regions have been
                // downloaded yet, notify user and return
                if (offlineRegions == null || offlineRegions.length == 0) {

                    downloadRegionDialog();
                }

                else
                {
                }


            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error: " + error);
            }
        });
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            if (!locationServices.areLocationPermissionsGranted()) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
        }
    }

    private void enableLocation(boolean enabled) {
        if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
            Location lastLocation = locationServices.getLastLocation();

            if (lastLocation != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 16));
            }

            locationServices.addLocationListener(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {

                        // Move the map camera to where the user location is and then remove the
                        // listener so the camera isn't constantly updating when the user location
                        // changes. When the user disables and then enables the location again, this
                        // listener is registered again and will adjust the camera once again.
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 16));
                        locationServices.removeLocationListener(this);
                    }
                }
            });
            floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(8.48222,124.64722), 10));
        }
        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);
    }

    public static String getLat()
    {
        Location lastLocation = locationServices.getLastLocation();
        result = String.valueOf(new LatLng(lastLocation));
        getLat = result.substring(result.indexOf("latitude=") + 9, result.indexOf(","));
        return getLat;

    }


    public static String getLong()
    {
        Location lastLocation = locationServices.getLastLocation();
        result = String.valueOf(new LatLng(lastLocation));
        getLong = result.substring(result.indexOf("longitude=") + 10, result.indexOf(", alt"));
        return getLong;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("SMS and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public void onRequestClick(View view) {

            SmsManager smsManager = SmsManager.getDefault();
        //+639283148474 - sms server, +639057767601 -mark
            smsManager.sendTextMessage("+639268241111", null, "Request", null, null);
            Toast.makeText(activity_offline_simple.this, "Request Sent", Toast.LENGTH_SHORT).show();




    }

    public void update_location()
    {


        Cursor res = myDb.getAllData();
        if (res.getCount() == 0){
            // Toast.makeText(MapsActivity.this, "NO DATA FOUND!", Toast.LENGTH_SHORT).show();
            //showMessage("No Data found", "Error");
            //startActivity(new Intent(MainActivity.this, MapsActivity.class));
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()){
            Integer id = Integer.parseInt(res.getString(0));
            final String timestamp = res.getString(1);
            final Double lat = Double.parseDouble(res.getString(2));
            final Double lang = Double.parseDouble(res.getString(3));
            final String severity = res.getString(4);
            final String cause = res.getString(5);


            mapView.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    //LIGHT
                    if (severity.equalsIgnoreCase("Light") && cause.equalsIgnoreCase("Weather")) {
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lang))
                                .title(cause)
                                .snippet(timestamp)
                                .icon(icon_green_weather));
                    }
                    if (severity.equalsIgnoreCase("Light") && cause.equalsIgnoreCase("Accident")) {
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lang))
                                .title(cause)
                                .snippet(timestamp)
                                .icon(icon_green_accident));/////
                    }
                    if (severity.equalsIgnoreCase("Light") && cause.equalsIgnoreCase("Unknown")) {
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lang))
                                .title(cause)
                                .snippet(timestamp)
                                .icon(icon_green_unknown));
                    }

                    //MODERATE
                    if (severity.equalsIgnoreCase("Moderate") && cause.equalsIgnoreCase("Weather")) {
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lang))
                                .title(cause)
                                .snippet(timestamp)
                                .icon(icon_yellow_weather));/////
                    }
                    if (severity.equalsIgnoreCase("Moderate") && cause.equalsIgnoreCase("Accident")) {
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lang))
                                .title(cause)
                                .snippet(timestamp)
                                .icon(icon_yellow_accident));
                    }
                    if (severity.equalsIgnoreCase("Moderate") && cause.equalsIgnoreCase("Unknown")) {
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lang))
                                .title(cause)
                                .snippet(timestamp)
                                .icon(icon_yellow_unknown));
                    }

                    //HEAVY
                    if (severity.equalsIgnoreCase("Heavy") && cause.equalsIgnoreCase("Weather")) {
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lang))
                                .title(cause)
                                .snippet(timestamp)
                                .icon(icon_red_weather));/////
                    }
                    if (severity.equalsIgnoreCase("Heavy") && cause.equalsIgnoreCase("Accident")) {
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lang))
                                .title(cause)
                                .snippet(timestamp)
                                .icon(icon_red_accident));
                    }
                    if (severity.equalsIgnoreCase("Heavy") && cause.equalsIgnoreCase("Unknown")) {
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lang))
                                .title(cause)
                                .snippet(timestamp)
                                .icon(icon_red_unknown));
                    }
                    //ELSE
/*
                    else {

                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lang))
                                .title(cause)
                                .snippet(timestamp));
                    }
*/

                }

            });//end of mapasync


        }



    }



}
