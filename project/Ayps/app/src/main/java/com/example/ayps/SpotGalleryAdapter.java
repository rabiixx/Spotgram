package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpotGalleryAdapter extends RecyclerView.Adapter<SpotGalleryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> spotsImgsRefs;
    private LayoutInflater mInflater;

    // Constructor
    public SpotGalleryAdapter(Context context, ArrayList<String> spotsImgsRefs ) {
        this.context = context;
        this.mInflater = LayoutInflater.from( context );
        this.spotsImgsRefs = spotsImgsRefs;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.siv)
        SquareImageView siv;

        public ViewHolder( @NonNull View itemView ) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }

    }

    @NonNull
    @Override
    public SpotGalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.profile_spot_gallery_item, parent,false );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpotGalleryAdapter.ViewHolder holder, int position) {

        //Resources res = holder.itemView.getContext().getResources();
        String spotImgRef = spotsImgsRefs.get( position );

        StorageReference storageReference = FirebaseStorage.getInstance().getReference( spotImgRef );

        Glide.with( context )
                .load( storageReference )
                .into( holder.siv );

    }

    @Override
    public int getItemCount() {
        return spotsImgsRefs.size();
    }

    public void add( final int position, final String spotImgRef ) {
        spotsImgsRefs.add( position, spotImgRef );
        notifyItemInserted( position );
    }


    public void remove( final int position ) {
        spotsImgsRefs.remove( position );
        notifyItemRemoved( position );
    }
}
