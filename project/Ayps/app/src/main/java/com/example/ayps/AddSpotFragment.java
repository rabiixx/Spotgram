package com.example.ayps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddSpotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddSpotFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RESULT_OK = -1;
    private static final int requestCode = 203;

    // MapBox Geocode Data
    private String placeName;
    private String locality;
    private String place;
    private String region;
    private String country;
    private Double latitude;
    private Double longitude;


    private TextInputEditText inputPlace;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddSpotFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddSpotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddSpotFragment newInstance(String param1, String param2) {
        AddSpotFragment fragment = new AddSpotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_spot, container, false);

        inputPlace = view.findViewById( R.id.input_place );

        final TextInputLayout placeLayout = view.findViewById( R.id.place_layout );
        placeLayout.setEndIconOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult( new Intent(getActivity(), MapBoxTest.class), requestCode );
            }
        });

        AutoCompleteTextView autoCompleteTextView = view.findViewById( R.id.tags_dropdown );
        String[] cheeses = {
                "Parmesan",
                "Ricotta",
                "Fontina",
                "Mozzarella",
                "Cheddar"
        };
        Adapter adapter = new ArrayAdapter<String>( this,  );

        return view;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == AddSpotFragment.requestCode ) {
            if ( resultCode == RESULT_OK) {

                placeName = data.getStringExtra( "placeName");
                locality = data.getStringExtra( "locality");
                place = data.getStringExtra( "place");
                region = data.getStringExtra( "region");
                country = data.getStringExtra( "country");

                latitude = data.getDoubleExtra("latitude", 0.0);
                longitude = data.getDoubleExtra("longitude", 0.0);

                Log.i("debug", "Latitude: " + latitude);
                Log.i("debug", "Longitude: " + longitude);

                inputPlace.setText( data.getStringExtra( "placeName") );

            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}