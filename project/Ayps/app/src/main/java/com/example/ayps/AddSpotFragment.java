package com.example.ayps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddSpotFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = AddSpotFragment.class.getName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RESULT_OK = -1;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_PERMISSION_CODE = 12;

    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath = "";

    private static final int mapboxRequestCode = 203;
    private static final int GALLERY_REQUEST_CODE = 2;

    private static final int READ_STORAGE_PERMISSION_CODE = 501;

    // Firebase Storage
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference uploadedImgRef;

    // Firebase Authentication
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    // Captured Image UUID
    private String imgUUID = "";
    private String uplodadImgUrl = "";

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

    public AddSpotFragment() { }

    public static AddSpotFragment newInstance( String param1, String param2 ) {
        AddSpotFragment fragment = new AddSpotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {

        View view = inflater.inflate(R.layout.fragment_add_spot, container, false);
        ButterKnife.bind(this, view);

        // Firebase Storage Initialization
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        //  Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if ( currentUser == null ){
            Intent intent = new Intent( requireContext(), FirebaseSignIn.class );
            startActivity( intent );
            getActivity().finish();
        }

        // Open select location map activity
        placeLayout.setEndIconOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult( new Intent(getActivity(), SelectLocation.class), mapboxRequestCode );
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

                    if ( mCurrentPhotoPath != null && !mCurrentPhotoPath.equals("") ) {
                        imgUUID = UUID.randomUUID().toString();
                        try {
                            uploadPicture();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    SpotModel spotModel = new SpotModel(
                            inputTitle.getText().toString().trim(),
                            inputDescription.getText().toString().trim(),
                            "images/spots/" + imgUUID + ".jpg",
                            placeName,
                            locality,
                            place,
                            region,
                            country,
                            String.valueOf( latitude ),
                            String.valueOf( longitude ),
                            tags.toString(),
                            currentUser.getUid(),
                            currentUser.getDisplayName(),
                            currentUser.getPhotoUrl().toString()
                    );

                    // Upload Spot to FireStore Database
                    spotModel.addSpot();

                    AddSpotFragment addSpotFragment = new AddSpotFragment();

//                    getActivity().getSupportFragmentManager()

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove( ( (HomeActivity) requireActivity() ).addSpotFragment )
                            .commit();

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add( ( (ViewGroup) getView().getParent()).getId() , addSpotFragment )
                            .hide( addSpotFragment )
                            .commit();

                    ( (HomeActivity) requireActivity() ).addSpotFragment = addSpotFragment;
                    ( (HomeActivity) requireActivity() ).active = ( (HomeActivity) requireActivity() ).addSpotFragment;

                    ( (HomeActivity) getActivity() ).bottomNavigationView.setSelectedItemId( R.id.explore );

                    /*getActivity().getSupportFragmentManager().beginTransaction()
                            .remove( getActivity().getSupportFragmentManager().findFragmentByTag("2") )
                            .commit();*/


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

        if ( resultCode == AddSpotFragment.RESULT_OK ) {
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

                case REQUEST_IMAGE_CAPTURE:

                    ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(), Uri.parse( "file:" + mCurrentPhotoPath ));
                    try {
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        spotImgIV.setImageBitmap( bitmap );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    /*try {
                        mImageBitmap = MediaStore.Images.Media.getBitmap( requireActivity().getContentResolver(), Uri.parse("file:" + mCurrentPhotoPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    spotImgIV.setImageBitmap(mImageBitmap);*/

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

                    File spotImg = new File(imgPath);

                    try {
                        Bitmap b = BitmapFactory.decodeStream( new FileInputStream(spotImg) );
                        spotImgIV.setImageBitmap( b );
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;
            }

        } else {
            Log.i(TAG, "Result is not ok");
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

        final CharSequence[] options = { "Hacer Foto", "Escoger desde galeria", "Cancelar" };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Escoge tu foto de perfil");
//                setIcon(R.drawable.ic_camera_36);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @SuppressLint("IntentReset")
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Hacer Foto")) {


                    /* Check for Granted Permissions */
                    if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
                    } else {
                        captureFromCamera();
                    }

                } else if (options[item].equals("Escoger desde la galeria")) {

                    if ( ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions( requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_CODE);
                    } else {
                        captureFromGallery();
                    }

                } else if (options[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if ( hasAllPermissionsGranted(grantResults) ) {

            if (requestCode == CAMERA_PERMISSION_CODE ) {
                captureFromCamera();
            } else {
                captureFromGallery();
            }

        }
    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void captureFromCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity( getActivity().getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
                Log.i(TAG, "IOException: " + ex.getCause() );
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI );
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
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

    private void uploadPicture() throws FileNotFoundException {

        Uri uri = Uri.fromFile( new File( Uri.decode( mCurrentPhotoPath ) ) );

//        InputStream inputStream = new FileInputStream( new File(mCurrentPhotoPath) );
        StorageReference storageReference = storageRef.child( "images/spots/" + imgUUID + ".jpg" );
        UploadTask uploadTask = storageReference.putFile( uri );

        uploadTask
                .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i("firebase", "Picture uploaded successfully");
                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("firebase", "Picture upload failure");
                    }
                })
                .addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });
    }

    private File createImageFile() throws IOException {

        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }


}