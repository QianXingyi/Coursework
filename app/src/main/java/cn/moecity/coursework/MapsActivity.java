package cn.moecity.coursework;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Intent intent = getIntent();
    private String tutName,tutId;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        intent = getIntent();
        tutName = intent.getStringExtra("NName");
        tutId = intent.getStringExtra("Id");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        BmobQuery<Location> eq1 = new BmobQuery<Location>();
        eq1.addWhereEqualTo("tutId", tutId);
        List<BmobQuery<Location>> andQuerys = new ArrayList<BmobQuery<Location>>();
        andQuerys.add(eq1);
        BmobQuery<Location> query = new BmobQuery<Location>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Location>() {
            @Override
            public void done(List<Location> object, BmobException e) {
                if (e == null) {
                    location = object.get(0);
                    LatLng sydney = new LatLng(Double.valueOf(location.getLat()), Double.valueOf(location.getLon()));
                    mMap.addMarker(new MarkerOptions().position(sydney).title(location.getTimestamp()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                } else {
                }
            }
        });
        // Add a marker in Sydney and move the camera

    }
}
