package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.play.core.internal.ch;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

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

    // MapBox Geocode Data
    private String placeName;
    private String locality;
    private String place;
    private String region;
    private String country;
    private Double latitude;
    private Double longitude;


    @BindView( R.id.input_title) TextInputEditText inputTitle;
    @BindView( R.id.input_title) TextInputLayout titleLayout;

    @BindView( R.id.input_description ) TextInputEditText inputDescription;
    @BindView( R.id.input_description ) TextInputLayout descriptionLayout;


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
                validateTitleDesc();
            }
        });

        return view;
    }


    private boolean validateTitleDesc() {

        if ( inputTitle.getText() != null && TextUtils.isEmpty( inputTitle.getText()) ) {
            titleLayout.setError("Title cannot be empty.");
            return Boolean.FALSE;
        }

        final String title = inputTitle.getText().toString().trim();

        final String USERNAME_PATTERN = "[A-Z0-9]+";
        Pattern pattern = Pattern.compile( USERNAME_PATTERN );
        Matcher matcher = pattern.matcher( title );

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

//                Log.i("debug", "Latitude: " + latitude);
//                Log.i("debug", "Longitude: " + longitude);

                inputPlace.setText( data.getStringExtra( "placeName") );

            }
        }
    }

    // Creates a chip and is added to chip group
    @SuppressLint("SetTextI18n")
    private void addChip(final String text ) {

        Chip chip = new Chip( requireContext() );

        chip.setOnCloseIconClickListener( chipCloseListener );

        chip.setText( "#" + text );
        chip.setBackgroundColor( getResources().getColor( R.color.orange ) );
        chip.setCloseIconVisible( true );

        tagsGroup.addView( chip );
    }

    @Override
    public void onClick(View v) {

    }

    // Removes selected chip from chip group
    private final View.OnClickListener chipCloseListener = new View.OnClickListener () {
        @Override
        public void onClick( View v ) {
            tagsGroup.removeView( v );
        }
    };

}