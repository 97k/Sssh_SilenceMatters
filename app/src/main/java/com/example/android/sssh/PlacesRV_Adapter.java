package com.example.android.sssh;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sssh.provider.PlaceContract;
import com.google.android.gms.location.places.PlaceBuffer;

/**
 * Created by aadi on 26/7/17.
 */

public class PlacesRV_Adapter extends RecyclerView.Adapter<PlacesRV_Adapter.PlacesHolder> {
    private static final String TAG = PlacesRV_Adapter.class.getSimpleName();
    private Context mContext;
    private PlaceBuffer mPlaces;
    ItemLongClickListener mClickListener;

    public PlacesRV_Adapter(Context context, PlaceBuffer places, ItemLongClickListener listener) {
        this.mContext = context;
        this.mPlaces = places;
        this.mClickListener = listener;
    }


    public interface ItemLongClickListener {
        void onLongClick(int pos);
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

        int value = position + 1;
        // Getting the URi of the clicked position.
        Uri uri = ContentUris.withAppendedId(PlaceContract.PlaceEntry.CONTENT_URI, value);
        Log.i(TAG, "Uri is " + uri);
        holder.nameTextView.setText(nameOfPlace);

        if (uri != null) {
            // Projection, that specifies which columns from the database.

            String[] projection = {PlaceContract.PlaceEntry.COLUMN_PLACE_NAME_BY_USER};
            Cursor data = mContext.getContentResolver().query(uri, projection, null, null, null);
            if (data != null && data.moveToFirst()) {
                String name;
                if (data != null) {
                    name = data.getString(data.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACE_NAME_BY_USER));
                    if (name != null)
                        holder.nameTextView.setText(name);
                }
            }
        }

        holder.addressTextView.setText(addressOfPlace);


    }

    @Override
    public int getItemCount() {
        if (mPlaces == null) return 0;
        return mPlaces.getCount();
    }

    public void swapPlaces(PlaceBuffer newPlaces) {
        mPlaces = newPlaces;
        if (mPlaces != null) {
            this.notifyDataSetChanged();
        }
    }

    class PlacesHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView nameTextView;
        TextView addressTextView;

        public PlacesHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            addressTextView = (TextView) itemView.findViewById(R.id.address_text_view);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            int clickedPos = getAdapterPosition();
            mClickListener.onLongClick(clickedPos);
            return false;
        }
    }
}
