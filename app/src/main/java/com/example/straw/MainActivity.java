package com.example.straw;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import android.content.ContentValues;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationSource, AMapLocationListener {

    // Variables needed by map
    private MapView mMapView = null;
    private AMap aMap;
    private MapView mapView;//The map controls
    private DrawerLayout drawerLayout;

    // variables needed by locate
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;//Positioning parameters
    private OnLocationChangedListener mListener = null;//Location monitor

    //Identity, used to determine whether the location information is displayed only once and the user is relocated
    private boolean isFirstLoc = true;

    // bottons on the main screen
    private Button helpbtn;
    private FloatingActionButton menubtn;

    // This is mendatory for SMS sending
    private SmsManager sManager;

    // This List is designed for saving phone numbers for contacts
    ArrayList<User> sendlist = new ArrayList<User>();

    // The location for sending SMS
    private String location;

    //Here is the NavigationView
    private NavigationView NaviView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //show the map
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        //Get map object
        aMap = mapView.getMap();


        //Set the display location button and can be clicked
        final UiSettings settings = aMap.getUiSettings();
        //Set up position monitoring
        aMap.setLocationSource(this);
        // Whether to display the positioning button
        settings.setMyLocationButtonEnabled(true);
        // Can you trigger the location and display the location layer
        aMap.setMyLocationEnabled(true);


        //Small icon for positioning
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pin));
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);

        //start the positioning
        initLoc();


        // Search in the database
        UtilDao list = new UtilDao(MainActivity.this);
        sendlist = (ArrayList<User>) list.inquireData();

        // get the permission from the user to send SMS
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);

        // Here is the sending part
        sManager = SmsManager.getDefault();
        helpbtn = findViewById(R.id.button);

        helpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (User user : sendlist) {
                    // Create a PendingIntent object
                    PendingIntent pi = PendingIntent.getActivity(
                            MainActivity.this, 0, new Intent(), 0);
                    // Send SMS
                    sManager.sendTextMessage(user.getPhone(), null, "SOS! I'm in the " + location + " PLease help me!", pi, null);
                }
                Toast.makeText(getApplicationContext(), "Message has been send successfully!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        // here is the menu button
        menubtn = findViewById(R.id.floatbutton);
        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout = findViewById(R.id.drawer);
                drawerLayout.openDrawer(Gravity.LEFT);
            }

        });

        // here is the Drawerview
        NaviView = findViewById(R.id.NaviView);
        NaviView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent i;
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.nav_info:
                        i = new Intent(MainActivity.this, contact_activity.class);
                        startActivity(i);
                        break;

                    case R.id.nav_news:
                        i = new Intent(MainActivity.this, NewsActivity.class);
                        startActivity(i);
                        break;

                    case R.id.nav_setting:
                        i = new Intent(MainActivity.this, settingActivity.class);
                        startActivity(i);
                        break;

                    case R.id.nav_share:
                        i = new Intent(MainActivity.this, shareActivity.class);
                        startActivity(i);
                        break;

                    case R.id.nav_about:
                        i = new Intent(MainActivity.this, aboutActivity.class);
                        startActivity(i);
                        break;

                    default:
                        Toast.makeText(MainActivity.this, "Coming soon", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });

    }




    private void initLoc() {
        //initial the positioning
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //Set up the position callback listener, where the AMapLocationListener interface can only be implemented by the onLocationChanged method and is used to receive the position result returned asynchronously, with AMapLocation type as the argument.
        mLocationClient.setLocationListener(this);
        //initial the parameters of positioning
        mLocationOption = new AMapLocationClientOption();
        //The positioning mode is Hight_Accuracy and precision mode, Battery_Saving is low power mode, Device_Sensors is device only mode
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //Sets whether to return address information (default return address information)
        mLocationOption.setNeedAddress(true);
        //Sets whether to position only once, which defaults to false
        mLocationOption.setOnceLocation(false);
        //Sets whether to force refresh WIFI. The default is to force refresh
        mLocationOption.setWifiActiveScan(true);
        //Sets whether the simulated location is allowed. The default is false. The simulated location is not allowed
        mLocationOption.setMockEnable(false);
        //Set the location interval in milliseconds, which defaults to 2000ms
        mLocationOption.setInterval(2000);
        //Set the location parameters for the location client object
        mLocationClient.setLocationOption(mLocationOption);
        //initialize the positioning
        mLocationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        //mLocationClient.stopLocation();//stop the positioning
        mLocationClient.onDestroy();//Destroy the locating client.
        //After destroying the location client, to reopen the location, New an AMapLocationClient object.
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Mmapview.onresume () is executed when the activity executes onResume to achieve map life cycle management
        mapView.onResume();
        //initialize the positioning
        initLoc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Mmapview.onpause () is performed when an activity performs onPause to implement map life cycle management
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //When the activity implement onSaveInstanceState execution mMapView. OnSaveInstanceState (outState), life cycle management implementation maps
        mapView.onSaveInstanceState(outState);
    }

    private MarkerOptions getMarkerOptions(AMapLocation amapLocation) {
        //Set the pushpin option
        MarkerOptions options = new MarkerOptions();
        //icon
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin));
        //location
        options.position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
        StringBuffer buffer = new StringBuffer();
        buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
        //title
        options.title(buffer.toString());
        //subtitle
        options.snippet("here you are");
        //Sets the number of frames to refresh the image resource once
        options.period(60);

        return options;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //Locate the successful callback information and set the relevant message
                aMapLocation.getLocationType();//For sources of current location results, such as network location results, see the official location type table
                aMapLocation.getLatitude();//To obtain the latitude
                aMapLocation.getLongitude();//Obtain longitude
                aMapLocation.getAccuracy();//Acquisition of precision information
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);// positioning time
                aMapLocation.getAddress();//Address. If isNeedAddress is set as false in option, there will be no such result. Address information will be included in the network positioning result, but GPS positioning will not return address information.
                aMapLocation.getCountry();//Information of the country
                aMapLocation.getProvince();//Provincial information
                aMapLocation.getCity();//Cities' information
                aMapLocation.getDistrict();// City information
                aMapLocation.getStreet();//The street information
                aMapLocation.getStreetNum();//Street number information
                aMapLocation.getCityCode();//City code
                aMapLocation.getAdCode();//Area code

                // If the flag bit is not set, then when you drag the map, it moves the map to its current location
                if (isFirstLoc) {
                    //Set the zoom level
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //Move the map to the anchor point
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //Clicking the Location button moves the center of the map to the registration point
                    mListener.onLocationChanged(aMapLocation);
                    //Get location information
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(aMapLocation.getCountry() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getCity() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getDistrict() + ""
                            + aMapLocation.getStreet() + ""
                            + aMapLocation.getStreetNum());

                    location = buffer.toString();
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    isFirstLoc = false;
                }
            } else {
                //ErrCode is the error code, errInfo is the error message, see the error code table.
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
//                Toast.makeText(getApplicationContext(), "Positioning failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}
