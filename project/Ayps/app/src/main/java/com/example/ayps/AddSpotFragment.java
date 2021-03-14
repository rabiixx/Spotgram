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

import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.play.core.internal.ch;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

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

    private static int SpannedLength = 0;
    private static int lastChipSize = 0;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.inputTagsLayout ) TextInputLayout inputTagsLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_tags ) TextInputEditText inputTags;

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
        ButterKnife.bind(this, view);

        inputPlace = view.findViewById( R.id.input_place );

        final TextInputLayout placeLayout = view.findViewById( R.id.place_layout );
        placeLayout.setEndIconOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult( new Intent(getActivity(), MapBoxTest.class), requestCode );
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.list_item, TAGS);

        MultiAutoCompleteTextView autoCompleteTextView = view.findViewById( R.id.tags_dropdown );

        autoCompleteTextView.setAdapter( arrayAdapter );
        autoCompleteTextView.setTokenizer( new MultiAutoCompleteTextView.CommaTokenizer() );


        inputTags.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.i("debug", "Start: " + i );
                Log.i("debug", "End: " + i1 );
                Log.i("debug", "Count: " + i2 );

//                if ( charSequence.length() == SpannedLength - chipLength) {
//                    SpannedLength = charSequence.length();
//                }
            }

            @Override
            public void afterTextChanged( Editable editable ) {

                if ( editable.subSequence( SpannedLength, editable.length() ).toString().endsWith(" ") ) {

                    Log.i("debug", "illo illo" );

                    ChipDrawable chip = ChipDrawable.createFromResource( getContext(), R.xml.standalone_chip);
                    chip.setText( editable.subSequence( SpannedLength, editable.length() ).toString().trim() );
                    chip.setBounds(0, 0, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());
                    ImageSpan span = new ImageSpan( chip );
                    editable.setSpan(span, SpannedLength, editable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    lastChipSize = editable.length() - SpannedLength;
                    SpannedLength = editable.length();

                    Log.i("debug", "lastChipSize: " + lastChipSize);
                    Log.i("debug", "SpannedLenght: " + SpannedLength);

                }

            }
        });



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

    private static final String[] TAGS = new String[] {
            "Amanecer", "Anochecer", "Lugar Abandonado", "Paseo", "Picnic"
    };

}