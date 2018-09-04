package com.example.zzyzj.inventoryappstage1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzyzj.inventoryappstage1.data.InventoryContract;

// Also my button code was inspired by a version of
// https://stackoverflow.com/questions/44034208/updating-listview-with-cursoradapter-after-an-onclick-changes-a-value-in-sqlite

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of inventory data as its data source. This adapter knows
 * how to create list items for each row of inventory data in the {@link Cursor}.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the inventory data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current inventory can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find fields to populate
        TextView nameTextView = view.findViewById(R.id.name);
        TextView summaryTextView = view.findViewById(R.id.summary);
        final TextView quantityTextView = view.findViewById(R.id.quantity);
        // find the column of the data we are interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
        // extract properties from cursor
        String inventoryName = cursor.getString(nameColumnIndex);
        String inventoryPrice = cursor.getString(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        // If the inventory price is empty string or null, then use some default text
        // that says "Unknown price", so the TextView isn't blank.
        if (TextUtils.isEmpty(inventoryPrice)) {
            inventoryPrice = context.getString(R.string.unknown_price);
        }
        // populate the fields with extracted properties
        nameTextView.setText(String.valueOf(inventoryName));
        summaryTextView.setText(String.valueOf(inventoryPrice));
        quantityTextView.setText(Integer.toString(quantity));

        // Quantity Button
        final Button saleButton = view.findViewById(R.id.sale_button);
        // Get the current items ID
        int currentId = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));
        // Make the content uri for the current Id
        final Uri contentUri = Uri.withAppendedPath(InventoryContract.InventoryEntry.CONTENT_URI, Integer.toString(currentId));

        // Change the quantity when you click the button
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.valueOf(quantityTextView.getText().toString());

                if (quantity > 0) {
                    quantity = quantity - 1;
                }
                else {
                    Toast.makeText(context, "Oops.  Click product to add inventory.", Toast.LENGTH_SHORT).show();
                }
                // Content Values to update quantity
                ContentValues values = new ContentValues();
                values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, quantity);

                // update the database
                context.getContentResolver().update(contentUri, values, null, null);
            }
        });
    }
}