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
import com.google.android.material.button.MaterialButton;
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
        Log.i("debug", "Height: " + height );

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

    public void add( final int position, final SpotModel spotModel ) {
        spotArrayList.add( position, spotModel );
        notifyItemInserted( position );
    }


    public void remove( final int position ) {
        spotArrayList.remove( position );
        notifyItemRemoved( position );
    }
}
