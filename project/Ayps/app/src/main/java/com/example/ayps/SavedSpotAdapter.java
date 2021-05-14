package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class SavedSpotAdapter extends RecyclerView.Adapter<SavedSpotAdapter.ViewHolder> {

    // OnClickListener interface
    public interface SavedSpotAdapterListener {
        void openGMapBtnOnClick(View v, int position);
        void removeSpotFromSavedSpotsOnClick( View v, int position );
    }

    private static final String TAG = SavedSpotAdapter.class.getName();

    public SavedSpotAdapterListener onClickListener;

    private ArrayList<SpotModel> spotArrayList;
    private LayoutInflater mInflater;
    Context context;

    // Constructor
    public SavedSpotAdapter(Context context, ArrayList<SpotModel> spotArrayList, SavedSpotAdapterListener listener ) {
        this.context = context;
        this.mInflater = LayoutInflater.from( context );
        this.spotArrayList = spotArrayList;
        this.onClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Post data
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.spot_img)
        ImageView spotImg;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.spot_title)
        TextView spotTitle;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.spot_desc)
        TextView spotDesc;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.open_gmaps)
        ImageView openGMapBtn;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.remove_saved_spot)
        CheckBox removeSavedSpotBtn;

        public ViewHolder( @NonNull View itemView ) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }

    }

    @NonNull
    @Override
    public SavedSpotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.saved_spots_list_item, parent,false );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedSpotAdapter.ViewHolder holder, int position) {

        holder.removeSavedSpotBtn.setChecked(true);

        holder.openGMapBtn.setOnClickListener(v -> onClickListener.openGMapBtnOnClick(v, position));
        holder.removeSavedSpotBtn.setOnClickListener(v -> onClickListener.removeSpotFromSavedSpotsOnClick(v, position));

        SpotModel spot = spotArrayList.get( position );

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        holder.spotTitle.setText( spot.getTitle() );
        holder.spotDesc.setText( spot.getDescription() );

        StorageReference storageReference = FirebaseStorage.getInstance().getReference( spot.getSpotImg() );

        Glide.with( context )
                .load( storageReference )
                .into( holder.spotImg );
    }

    @Override
    public int getItemCount() {
        return spotArrayList.size();
    }

    public void remove( final int position ) {
        spotArrayList.remove( position );
        notifyItemRemoved( position );
    }

}
