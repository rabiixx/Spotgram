package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.ViewHolder> {

    // OnClickListener interface
    public interface SpotAdapterListener {
        void openMapBtnOnClick(View v, int position);
        void openGMapBtnOnClick(View v, int position);
        void openAuthorProfileBtnClick( View v, int position );
        void saveSpotBtnClick( View v, int position );
    }

    private static final String TAG = SpotAdapter.class.getName();

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    public SpotAdapterListener onClickListener;

    private ArrayList<SpotModel> spotArrayList;
    private LayoutInflater mInflater;
    Context context;

    // Constructor
    public SpotAdapter(Context context, ArrayList<SpotModel> spotArrayList, SpotAdapterListener listener ) {
        this.context = context;
        this.mInflater = LayoutInflater.from( context );
        this.spotArrayList = spotArrayList;
        this.onClickListener = listener;
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
        @BindView(R.id.open_map)
        MaterialButton openMapBtn;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.open_gmaps)
        MaterialButton openGMapBtn;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.save_spot)
        CheckBox saveSpotBtn;

        public ViewHolder( @NonNull View itemView ) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }

    }

    @NonNull
    @Override
    public SpotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.spot_list_item, parent,false );

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpotAdapter.ViewHolder holder, int position) {

        holder.openMapBtn.setOnClickListener(v -> onClickListener.openMapBtnOnClick( v, position ));
        holder.openGMapBtn.setOnClickListener(v -> onClickListener.openGMapBtnOnClick( v, position ));
        holder.authorUsername.setOnClickListener(v-> onClickListener.openAuthorProfileBtnClick( v, position ));
        holder.saveSpotBtn.setOnClickListener(v-> onClickListener.saveSpotBtnClick( v, position ));

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

        SpotModel spot = spotArrayList.get( position );

        Log.i(TAG, "Spot Title: " + spot.getTitle() );
        Log.i(TAG, "Spot ID: " + spot.getSpotId() );

        // Firebase auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document( currentUser.getUid() )
                .collection("savedSpots")
                .document( spot.getSpotId() )
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Document does not exist!");
                                holder.saveSpotBtn.setChecked( true );
                            } else {
                                Log.d(TAG, "Document does not exist!");
                                holder.saveSpotBtn.setChecked( false );
                            }
                        } else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });


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
    }

    @Override
    public int getItemCount() {
        return spotArrayList.size();
    }

    public void add( final int position, final SpotModel spot ) {
        spotArrayList.add( position, spot );
        notifyItemInserted( position );
    }


    public void remove( final int position ) {
        spotArrayList.remove( position );
        notifyItemRemoved( position );
    }

}
