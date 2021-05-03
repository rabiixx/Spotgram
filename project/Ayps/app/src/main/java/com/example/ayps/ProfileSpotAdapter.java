package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSpotAdapter extends RecyclerView.Adapter<ProfileSpotAdapter.ViewHolder> {

    private static final String TAG = ProfileSpotAdapter.class.getName();

    private ArrayList<SpotModel> spotArrayList;
    private LayoutInflater mInflater;
    Context context;

    // Constructor
    public ProfileSpotAdapter(Context context, ArrayList<SpotModel> spotArrayList ) {
        this.context = context;
        this.mInflater = LayoutInflater.from( context );
        this.spotArrayList = spotArrayList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Spot author data
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.author_profile_img)
        CircleImageView authorIV;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.author_username)
        TextView authorUsername;

        // Post data
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.spot_img)
        ImageView spotImg;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.spot_num_likes)
        TextView spotNumLikes;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.spot_title)
        TextView spotTitle;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.spot_desc)
        TextView spotDesc;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.like_icon)
        CheckBox likeIcon;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.popup_menu_btn)
        Button popupMenuBtn;


        public ViewHolder( @NonNull View itemView ) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }

    }

    @NonNull
    @Override
    public ProfileSpotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.profile_spot_list_item, parent,false );

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        Log.i("debug", "Height: " + height );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileSpotAdapter.ViewHolder holder, int position) {

//        Resources res = holder.itemView.getContext().getResources();

//        Log.i("ProfileFragment", "Spot position: " + position );

        SpotModel spot = spotArrayList.get( position );

        holder.authorUsername.setText( spot.getAuthorUsername() );
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);


        Glide.with( context ).load( spot.getAuthorProfileImg() ).apply(options).into( holder.authorIV );


        holder.spotTitle.setText( spot.getTitle() );
        holder.spotDesc.setText( spot.getDescription() );
        holder.spotNumLikes.setText( String.valueOf( spot.getNumLikes() ) );

        StorageReference storageReference = FirebaseStorage.getInstance().getReference( spot.getSpotImg() );

        Glide.with( context )
                .load( storageReference )
                .into( holder.spotImg );

        // Post likes control
        holder.likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int tmp = Integer.parseInt( holder.spotNumLikes.getText().toString() );

                if ( holder.likeIcon.isChecked() ) {
                    holder.spotNumLikes.setText( String.valueOf( tmp + 1 ) );
                } else {
                    holder.spotNumLikes.setText( String.valueOf( tmp - 1 ) );
                }
            }
        });

        // Open spot menu
        holder.popupMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(context, holder.popupMenuBtn);

                popup.getMenuInflater()
                        .inflate(R.menu.spot_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if ( item.getItemId() == R.id.edit_spot ) {
                            // Start new fregament in which user can change selected spot
                            //editSpot();
                            Log.i(TAG, "Edit spot detected");
                        } else if ( item.getItemId() == R.id.delete_spot ) {
                            Log.i(TAG, "Delete spot detected");
                            //                            deleteSpot( spot );
                        }

                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return spotArrayList.size();
    }

    public void add( final int position, final SpotModel spotModel ) {
        spotArrayList.add( position, spotModel );
        notifyItemInserted( position );
    }


    public void remove( final int position ) {
        spotArrayList.remove( position );
        notifyItemRemoved( position );
    }

    private void deleteSpot( SpotModel spot ) {

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Initiazlize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference spotImgRef = storage.getReference( spot.getSpotImg() );

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currenUser = mAuth.getCurrentUser();

        /*if ( currenUser == null ) {
//            Intent intent = new Intent( ProfileSpotAdapter.this, FirebaseSignIn.class );
        }*/

        // Delete spot image from storage
        spotImgRef.delete().
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Image deleted successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Error deleting " + spot.getSpotImg() + ": " + e.getMessage() );
                    }
                });

        // Delete spot from spots collection and users sub-collection
        db.collection("spots").document( spot.getSpotId() )
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        // Delete spot from user spots sub-collection
        db.collection("users").document( currenUser.getUid() )
                .collection("spots").document( spot.getSpotId() )
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Spot deleted successfully from user spot sub-collection");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Error deleting spot from user spot sub-collection: ");
                    }
                });


    }
}
