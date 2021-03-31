package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okio.GzipSink;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddSpotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddSpotFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RESULT_OK = -1;
    private static final int requestCode = 203;

    private JSONObject postData;
    private ArrayList<String> tags = new ArrayList<>();

    // MapBox Geocode Data
    private String placeName;
    private String locality;
    private String place;
    private String region;
    private String country;
    private Double latitude;
    private Double longitude;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_title) TextInputEditText inputTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.title_layout) TextInputLayout titleLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_description ) TextInputEditText inputDescription;
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.description_layout ) TextInputLayout descriptionLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.place_layout ) TextInputLayout placeLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_place ) TextInputEditText inputPlace;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.tags_layout ) TextInputLayout tagsLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_tag ) TextInputEditText inputTag;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.tags_group) ChipGroup tagsGroup;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.submit ) Button submit;

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
    public static AddSpotFragment newInstance( String param1, String param2 ) {
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
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {

        View view = inflater.inflate(R.layout.fragment_add_spot, container, false);
        ButterKnife.bind(this, view);

        // Open select location map activity
        placeLayout.setEndIconOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult( new Intent(getActivity(), MapBoxTest.class), requestCode );
            }
        });

        // Add tag to chip group
        tagsLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( !inputTag.getText().toString().trim().isEmpty() ) {
                    addChip( inputTag.getText().toString() );
                    inputTag.setText("");
                }

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getCurrentDate();

                if ( validateInput( inputTitle.getText().toString() )
                        && validateInput( inputDescription.getText().toString() )
                        && validateInput( inputTitle.getText().toString() )
                ) {

                    try {

                        postData.put("title", inputTitle.getText().toString().trim() );
                        postData.put("description", inputDescription.getText().toString().trim() );
                        postData.put("longitude", String.valueOf( longitude ));
                        postData.put("latitude", String.valueOf( latitude ) );
                        postData.put("placeName", placeName );
                        postData.put("locality", locality );
                        postData.put("place", place );
                        postData.put("region", region );
                        postData.put("country",  country );
                        postData.put("created_at", getCurrentDate() );

                        Spot spot = new Spot();
                        spot.postSpot( postData.toString().getBytes() );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        return view;
    }

    private String getCurrentDate() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());
        return df.format(c);

    }


    private boolean validateInput( final String data ) {

        if ( data != null && TextUtils.isEmpty( data ) ) {
            titleLayout.setError("Title cannot be empty.");
            return Boolean.FALSE;
        }

        final String USERNAME_PATTERN = "[A-Z0-9]+";
        Pattern pattern = Pattern.compile( USERNAME_PATTERN );
        Matcher matcher = pattern.matcher( data );

        if ( !matcher.matches() ) {
            titleLayout.setError("Enter a valid title.");
            return Boolean.FALSE;
        }

        if ( titleLayout.isErrorEnabled() )
            titleLayout.setError("");

        return Boolean.TRUE;
    }

    // Catch select location activity callback data
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

                inputPlace.setText( data.getStringExtra( "placeName") );

            }
        }
    }

    // Creates a chip and is added to chip group
    @SuppressLint("SetTextI18n")
    private void addChip( final String text ) {

        Chip chip = new Chip( requireContext() );

        chip.setOnCloseIconClickListener( chipCloseListener );

        chip.setText( "#" + text );
        chip.setBackgroundColor( getResources().getColor( R.color.orange ) );
        chip.setCloseIconVisible( true );

        tagsGroup.addView( chip );

        tags.add( text );

    }

    @Override
    public void onClick(View v) {

    }

    // Removes selected chip from chip group
    private final View.OnClickListener chipCloseListener = new View.OnClickListener () {
        @Override
        public void onClick( View v ) {

            tagsGroup.removeView( v );

            String text = ((Chip) v).getText().toString();
            tags.remove( text.replaceFirst("#", "") );

        }
    };

}