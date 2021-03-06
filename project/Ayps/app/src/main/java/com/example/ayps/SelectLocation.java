package com.example.ayps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class SelectLocation extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {

    private static final String TAG = SelectLocation.class.getName();

    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Button selectLocationButton;
    private PermissionsManager permissionsManager;
    private ImageView hoveringMarker;
    private Layer droppedMarkerLayer;

    private String locality = "Unknown";
    private String place = "Unknown";
    private String region = "Unknown";
    private String country = "Unknown";
    private String placeName = "Unknown";

    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView( R.layout.activity_select_location);

        // Initialize the mapboxMap view
        mapView = findViewById( R.id.mapView );
        mapView.onCreate( savedInstanceState );
        mapView.getMapAsync(this);

        FloatingActionButton submit = findViewById(R.id.submit);

        submit.setOnClickListener(v -> {

            if ( placeName.toLowerCase().trim().equals("unknown") ) {
                Toast.makeText(SelectLocation.this, "You must select a place", Toast.LENGTH_SHORT).show();
            } else {

                Intent data = new Intent();

                data.putExtra("locality", locality);
                data.putExtra("place", place);
                data.putExtra("region", region);
                data.putExtra("country", country);
                data.putExtra("placeName", placeName );

                Log.i("debug", "Latitude: " + latitude );
                Log.i("debug", "Longitude: " + longitude);

                data.putExtra("latitude", latitude );
                data.putExtra("longitude", longitude );

                setResult(RESULT_OK, data);

                finish();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        SelectLocation.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull final Style style) {
                enableLocationPlugin(style);

                hoveringMarker = new ImageView(SelectLocation.this);
                hoveringMarker.setImageResource(R.drawable.red_marker);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                hoveringMarker.setLayoutParams(params);
                hoveringMarker.getLayoutParams().height = 128;
                hoveringMarker.getLayoutParams().width = 128;
                mapView.addView(hoveringMarker);

                // Initialize, but don't show, a SymbolLayer for the marker icon which will represent a selected location.
                initDroppedMarker(style);

                // Button for user to drop marker or to pick marker back up.
                selectLocationButton = findViewById(R.id.select_location_button);
                selectLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (hoveringMarker.getVisibility() == View.VISIBLE) {

                            // Use the map target's coordinates to make a reverse geocoding search
                            final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

                            latitude = mapTargetLatLng.getLatitude();
                            longitude = mapTargetLatLng.getLongitude();

                            // Hide the hovering red hovering ImageView marker
                            hoveringMarker.setVisibility(View.INVISIBLE);

                            // Transform the appearance of the button to become the cancel button
                            selectLocationButton.setBackgroundColor(
                                    ContextCompat.getColor(SelectLocation.this, R.color.mapboxPink));

                            selectLocationButton.setText("Cancel");

                            // Show the SymbolLayer icon to represent the selected map location
                            if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                                if (source != null) {
                                    source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                                }
                                droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                                if (droppedMarkerLayer != null) {
                                    droppedMarkerLayer.setProperties(visibility(VISIBLE));
                                }
                            }

                            // Use the map camera target's coordinates to make a reverse geocoding search
                            reverseGeocode(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                        } else {

                            // Switch the button appearance back to select a location.
                            selectLocationButton.setBackgroundColor(
                                    ContextCompat.getColor(SelectLocation.this, R.color.mapboxGray));
                            selectLocationButton.setText("Select a location");

                            // Show the red hovering ImageView marker
                            hoveringMarker.setVisibility(View.VISIBLE);

                            // Hide the selected location SymbolLayer
                            droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                            if (droppedMarkerLayer != null) {
                                droppedMarkerLayer.setProperties(visibility(NONE));
                            }
                        }
                    }
                });
            }
        });
    }

    private void initDroppedMarker(@NonNull Style loadedMapStyle) {

        Bitmap icon = BitmapFactory.decodeResource( this.getResources(),
                R.drawable.blue_marker);

        Bitmap new_icon = getResizedBitmap( icon, 80);

        // Add the marker image to map
        loadedMapStyle.addImage("dropped-icon-image", new_icon );


        loadedMapStyle.addSource(new GeoJsonSource("dropped-marker-source-id"));
        loadedMapStyle.addLayer(new SymbolLayer(DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id").withProperties(
                iconImage("dropped-icon-image"),
                visibility(NONE),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        ));
    }

    public Bitmap getResizedBitmap( Bitmap bm, int newWidth ) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();

        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleWidth);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mapView.onStop();
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
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "User_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted && mapboxMap != null) {
            Style style = mapboxMap.getStyle();
            if (style != null) {
                enableLocationPlugin(style);
            }
        } else {
            Toast.makeText(this, "User_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * This method is used to reverse geocode where the user has dropped the marker.
     *
     * @param point The location to use for the search
     */

    private void reverseGeocode( final Point point ) {
        try {
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken( getString( R.string.mapbox_access_token ) )
                    .query( Point.fromLngLat( point.longitude(), point.latitude() ) )
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                    if (response.body() != null) {
                        List<CarmenFeature> results = response.body().features();

                        if (results.size() > 0) {
                            CarmenFeature feature = results.get(0);

                            Log.i("debug", feature.toJson());

                            Objects.requireNonNull(feature.context()).get(0).text();

                            // If the geocoder returns a result, we take the first in the list and show a Toast with the place name.
                            mapboxMap.getStyle( new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {

                                        if ( Objects.requireNonNull(response.body().features().get(0).context()).get(1) != null ) {
                                            locality = Objects.requireNonNull(response.body().features().get(0).context()).get(1).text();
                                        }

                                        if ( Objects.requireNonNull(response.body().features().get(0).context()).get(2) != null ) {
                                            place = Objects.requireNonNull(response.body().features().get(0).context()).get(2).text();
                                        }

                                        if ( Objects.requireNonNull(response.body().features().get(0).context()).get(3) != null ) {
                                            region = Objects.requireNonNull(response.body().features().get(0).context()).get(3).text();
                                        }

                                        if ( Objects.requireNonNull(response.body().features().get(0).context()).get(4) != null ) {
                                            country = Objects.requireNonNull(response.body().features().get(0).context()).get(4).text();
                                        }

                                        placeName = feature.placeName();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(SelectLocation.this,
                                    getString(R.string.location_picker_dropped_marker_snippet_no_results), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<GeocodingResponse> call, @NotNull Throwable throwable) {
                    Log.e(TAG, "Geocoding Failure: " + throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            Log.e(TAG, "Error geocoding: " + servicesException.toString());
            servicesException.printStackTrace();
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component. Adding in LocationComponentOptions is also an optional
            // parameter
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(
                    this, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.NORMAL);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
}
