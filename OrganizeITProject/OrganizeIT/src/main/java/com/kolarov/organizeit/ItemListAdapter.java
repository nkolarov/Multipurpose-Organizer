package com.kolarov.organizeit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kolarov.organizeit.Models.ItemModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by N.Kolarov on 13-11-16.
 */
public class ItemListAdapter extends BaseAdapter{
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    private ArrayList<ItemModel> mEntries = new ArrayList<ItemModel>();

    public ItemListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return mEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        RelativeLayout itemView;
        if (convertView == null) {
            itemView = (RelativeLayout) mLayoutInflater.inflate(
                    R.layout.item_row, parent, false);

        } else {
            itemView = (RelativeLayout) convertView;
        }

        ImageView imageView = (ImageView)
                itemView.findViewById(R.id.listImage);
        TextView titleText = (TextView)
                itemView.findViewById(R.id.listTitle);
        TextView descriptionText = (TextView)
                itemView.findViewById(R.id.listDescription);

        String imageFile = getImageFileName(position);

        loadTypeImage(imageView, imageFile);

        String title = mEntries.get(position).title;
        titleText.setText(title);

        String description;
        int childsCount = mEntries.get(position).childcount;
        if (childsCount > 0)
            description = childsCount + " items";
        else
            description = "";

        if (description.trim().length() != 0) {
            descriptionText.setText(description);
        }

        return itemView;
    }

    private void loadTypeImage(ImageView imageView, String imageFile) {
        InputStream ims = null;
        try {
            ims = mContext.getAssets().open(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // load image as Drawable
        Drawable d = Drawable.createFromStream(ims, null);
        // set image to ImageView
        imageView.setImageDrawable(d);
    }

    private String getImageFileName(int position) {
        String imageFile;
        if (mEntries.get(position).itemtype == 0){
            imageFile = mContext.getString(R.string.item_type_asset_name);
        }
        else {
            imageFile = mContext.getString(R.string.item_element_asset_name);
        }
        return imageFile;
    }

    public void upDateEntries(ArrayList<ItemModel> entries) {
        mEntries = entries;
        notifyDataSetChanged();
    }
}
