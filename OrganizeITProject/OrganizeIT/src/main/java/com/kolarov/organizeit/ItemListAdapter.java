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
 * An adapter for setup list item data.
 */
public class ItemListAdapter extends BaseAdapter {
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    protected static final int DIALOG_REMOVE_ITEM = 1000;

    private long mItemIdToDelete;
    private int mItemPositionToDelete;

    private ArrayList<ItemModel> mEntries = new ArrayList<ItemModel>();

    public ItemListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        ImageView imageView = (ImageView) itemView.findViewById(R.id.listImage);
        TextView titleText = (TextView) itemView.findViewById(R.id.listTitle);
        TextView descriptionText = (TextView) itemView.findViewById(R.id.listDescription);
        String title = mEntries.get(position).title;
        String imageFile = getImageFileName(position);

        loadTypeImage(imageView, imageFile);
        titleText.setText(title);
        handleDeleteButton(position, itemView, title);
        setupDescription(position, descriptionText);

        return itemView;
    }

    private void handleDeleteButton(int position, RelativeLayout itemView, String title) {
        String levelUpTitle = mContext.getString(R.string.level_up_title);
        if (title.equals(levelUpTitle) && mEntries.get(position).childcount == 0) {
            hideDeleteButton(itemView);
        } else {
            showDeleteButton(itemView);
        }
    }

    private void setupDescription(int position, TextView descriptionText) {
        String description;
        int childsCount = mEntries.get(position).childcount;
        if (childsCount > 0)
            description = childsCount + mContext.getString(R.string.items_label);
        else
            description = mContext.getString(R.string.empty_string);

        descriptionText.setText(description);
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

    private void deleteItem(View view, int itemPosition) {
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
        if (type == 1) {
            imageFile = mContext.getString(R.string.item_type_asset_name);
        } else {
            imageFile = mContext.getString(R.string.item_element_asset_name);
        }
        return imageFile;
    }

    public void upDateEntries(ArrayList<ItemModel> entries) {
        if (entries == null) {
            mEntries = new ArrayList<ItemModel>();
        } else {
            mEntries = entries;
        }

        notifyDataSetChanged();
    }

    private Dialog createDialogRemoveConfirm(final int dialogRemove) {
        return new AlertDialog.Builder(mContext)
                .setIcon(mContext.getResources().getDrawable(R.drawable.abc_ic_clear_normal))
                .setTitle(mContext.getString(R.string.delete_confirmation_title))
                .setPositiveButton(mContext.getString(R.string.confirm_delete_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        handleRemoveConfirm(dialogRemove);
                    }
                })
                .setNegativeButton(mContext.getString(R.string.cancel_delete_button), null)
                .create();
    }

    private void handleRemoveConfirm(int dialogRemove) {
        if (dialogRemove == DIALOG_REMOVE_ITEM) {
            mEntries.remove(this.mItemPositionToDelete);
            this.upDateEntries(mEntries);
            DeleteItemTask deleteItemTask = new DeleteItemTask((Activity) mContext, (int) this.mItemIdToDelete);
            deleteItemTask.execute();
        }
    }
}
