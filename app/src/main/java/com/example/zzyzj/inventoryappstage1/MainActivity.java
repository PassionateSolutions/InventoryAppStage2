package com.example.zzyzj.inventoryappstage1;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// Used code from Udacity's Google Pet Starting Point App as the base for the code
// and adjusted the code accordingly :)

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.zzyzj.inventoryappstage1.data.InventoryContract;

/**
 * Displays list of products that were entered and stored in the app.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;

    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView inventoryListView = findViewById(R.id.text_view_inventory);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        // setup adapter to create a list item for each row in the pet data in the Cursor.
        // there is no pet data until the loader finishes so pass in null for the Cursor
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(mCursorAdapter);

        // adds onclick listener with intent
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // creates new intent to go from the Main to the EditorActivity
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                // Creates new Uri to gather the CONTENT_URI along with the id
                Uri currentInventoryUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                // sets data from the currentInventoryUri within the intent variable
                intent.setData(currentInventoryUri);
                // starts the intent variable
                startActivity(intent);
            }
        });

        // kick off loader
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);

    }

    // for testing if the app is working correctly when selecting "insert dummy data"

    private void insertInventory(){
        // Create a ContentValues object where column names are the keys,
        // and Smooth Q's product attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, "Smooth Q");
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, "$99.00");
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, "10");
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, "Zhiyun");
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE, "1-800-555-5555");
        // Insert a new row for Smooth Q in the database, returning the ID of that new row.
        // The first argument for db.insert() is the inventory table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Smooth Q.
        Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

    }

    /**
     * Helper method to delete all inventory in the database.
     */
    private void deleteAllInventory() {
        int rowsDeleted = getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from inventory database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertInventory();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllInventory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // define a projection that specifies the columns from the table we care about
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRICE,
                InventoryContract.InventoryEntry.COLUMN_QUANTITY};

        // this loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                InventoryContract.InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // update {@link InventoryCursorAdapter} with this new cursor containing updated inventory data
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // callback called when data needs to be deleted
        mCursorAdapter.swapCursor(null);

    }
}