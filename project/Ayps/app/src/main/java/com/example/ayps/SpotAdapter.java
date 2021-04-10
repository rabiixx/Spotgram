package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.ViewHolder> {

    private ArrayList<SpotModel> spotArrayList;
    private LayoutInflater mInflater;


    // Constructor
    public SpotAdapter(Context context, ArrayList<SpotModel> spotArrayList ) {
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

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.spot_num_likes)
        TextView spotNumLikes;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.spot_title)
        TextView spotTitle;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.spot_desc)
        TextView spotDesc;

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

        Resources res = holder.itemView.getContext().getResources();

        SpotModel spot = spotArrayList.get( position );

        holder.authorIV.setImageDrawable( res.getDrawable( R.drawable.default_spot )  );
        holder.authorUsername.setText( "rabiixx");

        holder.spotTitle.setText( spot.getTitle() );
        holder.spotDesc.setText( spot.getDescription() );
        holder.spotNumLikes.setText( String.valueOf( spot.getNumLikes() ) );

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
