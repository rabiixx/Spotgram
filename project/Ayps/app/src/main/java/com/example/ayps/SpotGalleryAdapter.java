package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpotGalleryAdapter extends RecyclerView.Adapter<SpotGalleryAdapter.ViewHolder> {

    private ArrayList<SpotModal> spotArrayList;
    private LayoutInflater mInflater;

    // Constructor
    public SpotGalleryAdapter(Context context, ArrayList<SpotModal> spotArrayList ) {
        this.mInflater = LayoutInflater.from( context );
        this.spotArrayList = spotArrayList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder( @NonNull View itemView ) {
            super( itemView );
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
        SpotModal spot = spotArrayList.get( position );

        //holder.authorIV.setImageDrawable( res.getDrawable( R.drawable.default_spot )  );

    }

    @Override
    public int getItemCount() {
        return spotArrayList.size();
    }

    public void add( final int position, final SpotModal spotModal ) {
        spotArrayList.add( position, spotModal );
        notifyItemInserted( position );
    }


    public void remove( final int position ) {
        spotArrayList.remove( position );
        notifyItemRemoved( position );
    }
}
