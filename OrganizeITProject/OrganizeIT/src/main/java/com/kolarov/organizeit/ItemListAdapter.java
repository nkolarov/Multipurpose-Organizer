package com.kolarov.organizeit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kolarov.organizeit.Models.ItemModel;
import com.kolarov.organizeit.Tasks.DeleteItemTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by N.Kolarov on 13-11-16.
 */
public class ItemListAdapter extends BaseAdapter{
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    protected static final int DIALOG_REMOVE_ITEM = 1000;

    private long mItemIdToDelete;
    private int mItemPositionToDelete;

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
        return mEntries.get(position).id;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        RelativeLayout itemView;
        final int currentPostition = position;
        if (convertView == null) {
            itemView = (RelativeLayout) mLayoutInflater.inflate(
                    R.layout.item_row, parent, false);

        } else {
            itemView = (RelativeLayout) convertView;
        }

        ImageButton deleteButton = (ImageButton) itemView.findViewById(R.id.buttonDeleteItem);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(view, currentPostition);
            }
        });

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

        String levelUpTitle = mContext.getString(R.string.level_up_title);
        if(title.equals(levelUpTitle) && mEntries.get(position).childcount == 0){
            hideDeleteButton(itemView);
        } else {
            showDeleteButton(itemView);
        }

        String description;
        int childsCount = mEntries.get(position).childcount;
        if (childsCount > 0)
            description = childsCount + " items";
        else
            description = "";

        // if (description.trim().length() != 0)
            descriptionText.setText(description);


        return itemView;
    }

    private void showDeleteButton(RelativeLayout itemView) {
        ImageButton deleteBtn = (ImageButton)
                itemView.findViewById(R.id.buttonDeleteItem);
        deleteBtn.setVisibility(View.VISIBLE);
    }

    private void hideDeleteButton(RelativeLayout itemView) {
        ImageButton deleteBtn = (ImageButton)
                itemView.findViewById(R.id.buttonDeleteItem);
        deleteBtn.setVisibility(View.INVISIBLE);
    }

    private void deleteItem(View view, int itemPosition){
        this.mItemPositionToDelete = itemPosition;
        this.mItemIdToDelete = getItemId(itemPosition);
        createDialogRemoveConfirm(DIALOG_REMOVE_ITEM).show();
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
        int type = mEntries.get(position).itemtype;
        if (type == 1){
            imageFile = mContext.getString(R.string.item_type_asset_name);
        }
        else {
            imageFile = mContext.getString(R.string.item_element_asset_name);
        }
        return imageFile;
    }

    public void upDateEntries(ArrayList<ItemModel> entries) {
        if (entries == null){
            mEntries = new ArrayList<ItemModel>();
        }
        else {
            mEntries = entries;
        }

        notifyDataSetChanged();
    }

    private Dialog createDialogRemoveConfirm(final int dialogRemove) {
        return new AlertDialog.Builder(mContext)
                .setIcon(mContext.getResources().getDrawable(R.drawable.abc_ic_clear_normal))
                .setTitle("Delete item and all its content?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        handleRemoveConfirm(dialogRemove);
                    }
                })
                .setNegativeButton("No", null)
                .create();
    }

    private void handleRemoveConfirm(int dialogRemove) {
        if(dialogRemove == DIALOG_REMOVE_ITEM){
            mEntries.remove(this.mItemPositionToDelete);
            this.upDateEntries(mEntries);
            DeleteItemTask deleteItemTask = new DeleteItemTask((Activity) mContext, (int)this.mItemIdToDelete);
            deleteItemTask.execute();
        }
    }
}
