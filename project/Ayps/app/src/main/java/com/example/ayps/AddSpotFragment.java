package com.example.ayps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.core.Amplify;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
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

    private static final int mapboxRequestCode = 203;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;

    private static final int READ_STORAGE_PERMISSION_CODE = 501;
    private static final int CAMERA_PERMISSION_CODE = 48;

    private File spotImg = null;
    private String spotImgPath = "";

    private FirebaseStorage storage;
    private StorageReference storageRef;

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

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.spot_img )
    ImageView spotImgIV;

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

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Open select location map activity
        placeLayout.setEndIconOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult( new Intent(getActivity(), MapBoxTest.class), mapboxRequestCode );
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

        submit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {

                getCurrentDate();

                if ( validateInput( inputTitle.getText().toString() )
                        && validateInput( inputDescription.getText().toString() )
                        && validateInput( inputTitle.getText().toString() )
                ) {

                    String imgUUID;
                    if ( spotImgPath.equals("") ) {
                        imgUUID = "no-img";
                    } else {
                        imgUUID = UUID.randomUUID().toString();
                        uploadPicture( view, imgUUID );
                    }

                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext() );
                    if ( account != null ) {
                        Log.i("debug", "User profile img: " + account.getPhotoUrl() );
                    }

                    Timestamp timestamp = new Timestamp( new Date() );
                    Log.i("debug", "Timestamp: " + timestamp.toString() );

                    SpotModel spotModel = new SpotModel(
                            inputTitle.getText().toString().trim(),
                            inputDescription.getText().toString().trim(),
                            0,
                            imgUUID,
                            placeName,
                            locality,
                            place,
                            region,
                            country,
                            String.valueOf( latitude ),
                            String.valueOf( longitude ),
                            tags.toString(),
                            "rabiixx",
                            "rabiixx"
                    );

                    // Upload Spot to FireStore Database
                    spotModel.addSpot();


                }

            }
        });


        spotImgIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProfileImage();
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

        final String USERNAME_PATTERN = "[a-z0-9]+";
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

    // Catch mapbox, gallery, camera activities callback data
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == AddSpotFragment.RESULT_OK ) {
            switch (requestCode) {

                case AddSpotFragment.mapboxRequestCode:

                    placeName = data.getStringExtra( "placeName");
                    locality = data.getStringExtra( "locality");
                    place = data.getStringExtra( "place");
                    region = data.getStringExtra( "region");
                    country = data.getStringExtra( "country");

                    latitude = data.getDoubleExtra("latitude", 0.0);
                    longitude = data.getDoubleExtra("longitude", 0.0);

                    inputPlace.setText( data.getStringExtra( "placeName") );
                    break;

                case CAMERA_REQUEST_CODE:

//                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                    spotImgIV.setImageBitmap(photo);

                    Bitmap mImageBitmap;
                    try {
                        mImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse( spotImgPath ) );
                        spotImgIV.setImageBitmap(mImageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case GALLERY_REQUEST_CODE:

                    Uri selectedImage = data.getData();

                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    assert selectedImage != null;
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                    String imgPath = cursor.getString( columnIndex );
                    cursor.close();

                    spotImg = new File( imgPath );

                    try {
                        Bitmap b = BitmapFactory.decodeStream( new FileInputStream( spotImg ) );
                        spotImgIV.setImageBitmap( b );
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;
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

    public void loadProfileImage() {
        final CharSequence[] options = { "Hacer Foto", "Escoger desde la galeria", "Cancelar" };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Escoge tu foto de perfil");
//                .setIcon(R.drawable.ic_camera_24dp);
//        builder.setIconAttribute("gravity=center_vertical", R.drawable.ic_camera_24dp);
//        builder.setIcon(R.drawable.ic_camera_24dp);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @SuppressLint("IntentReset")
            @Override
            public void onClick(DialogInterface dialog, int item) {
                Log.i("debug", "click");

                if (options[item].equals("Hacer Foto")) {

                    Log.i("debug", "foto");

                    /* Check for Granted Permissions */
                    if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
                    } else {
                        Log.i("AddSpotFragments", "captureFromCamera()" );
                        captureFromCamera();
                    }

                } else if (options[item].equals("Escoger desde la galeria")) {

                    if ( ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions( requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_CODE);
                    } else {
                        Log.i("AddSpotFragment", "captureFromGallery" );
                        captureFromGallery();
                    }

                } else if (options[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    /**
     * After requesting permissions, initializes camera or gallery
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if ( hasAllPermissionsGranted(grantResults) ) {
            Log.i("debug", "estamos dentro julio");

            if (requestCode == CAMERA_PERMISSION_CODE ) {
                captureFromCamera();
            } else {
                captureFromGallery();
            }

        } else {
            Log.i("debug", "estamos fuera julio");
        }
    }

    /**
     * Check if all required permissions are granted
     * @param grantResults
     * @return
     */
    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void captureFromCamera() {

        Intent takePictureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );

        if ( takePictureIntent.resolveActivity( requireActivity().getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if ( photoFile != null ) {

                Uri photoURI = FileProvider.getUriForFile( requireContext(),
                        "com.example.ayps.fileprovider",
                        photoFile );


                takePictureIntent.putExtra( MediaStore.EXTRA_OUTPUT, photoURI );
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void captureFromGallery() {

        Intent intent= new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

        String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    /**
     * Creates an image file
     * @return File
     * @throws IOException
     */
    private File createImageFile() throws IOException {

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = requireActivity().getExternalFilesDir( Environment.DIRECTORY_PICTURES );

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        spotImgPath  = image.getAbsolutePath();

        return image;
    }


    private void uploadPicture( View view, String uuid ) {

        Uri uri = Uri.fromFile( new File( spotImgPath ) );

        StorageReference storageReference = storageRef.child("images/" + uuid );
        storageReference.putFile( uri ).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i("firebase", "Picture uploaded successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("firebase", "Picture upload failure");
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });



        /*Uri photoURI = FileProvider.getUriForFile( requireContext(),
                "com.example.ayps.fileprovider",
                new File( spotImgPath ) );*/
    }
}