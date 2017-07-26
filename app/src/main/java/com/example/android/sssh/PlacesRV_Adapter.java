package com.example.android.sssh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.PlaceBuffer;

/**
 * Created by aadi on 26/7/17.
 */

public class PlacesRV_Adapter extends RecyclerView.Adapter<PlacesRV_Adapter.PlacesHolder>{
    private static final String TAG = PlacesRV_Adapter.class.getSimpleName();
    private Context mContext;
    private PlaceBuffer mPlaces;

    public PlacesRV_Adapter(Context context, PlaceBuffer places){
        this.mContext = context;
        this.mPlaces = places;
    }

    @Override
    public PlacesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.place_item, parent, false);
        return new PlacesHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesHolder holder, int position) {

        String nameOfPlace = mPlaces.get(position).getName().toString();
        String addressOfPlace = mPlaces.get(position).getAddress().toString();
        Log.e(TAG, nameOfPlace);
        holder.nameTextView.setText(nameOfPlace);
        holder.addressTextView.setText(addressOfPlace);

    }

    @Override
    public int getItemCount() {
        if (mPlaces==null)return 0;
        return mPlaces.getCount();
    }

    public void swapPlaces(PlaceBuffer newPlaces){
        mPlaces = newPlaces;
        if (mPlaces != null){
            this.notifyDataSetChanged();
        }
    }

     class PlacesHolder extends RecyclerView.ViewHolder{

        TextView nameTextView;
        TextView addressTextView;
         public PlacesHolder(View itemView){
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            addressTextView = (TextView) itemView.findViewById(R.id.address_text_view);
        }
    }
}
