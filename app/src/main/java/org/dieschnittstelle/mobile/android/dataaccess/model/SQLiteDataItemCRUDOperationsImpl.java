package org.dieschnittstelle.mobile.android.dataaccess.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.dieschnittstelle.mobile.android.dataaccess.model.DataItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

/**
 * Read out data items from an SQLite database
 * 
 * @author Joern Kreutel
 * 
 */
public class SQLiteDataItemCRUDOperationsImpl implements IDataItemCRUDOperationsWithContext {

	/**
	 * the logger
	 */
	protected static final String logger = SQLiteDataItemCRUDOperationsImpl.class
			.getName();

	/**
	 * the db name
	 */
	public static final String DBNAME = "dataItems.db";

	/**
	 * the initial version of the db based on which we decide whether to create
	 * the table or not
	 */
	public static final int INITIAL_DBVERSION = 0;

	/**
	 * the table name
	 */
	public static final String TABNAME = "dataitems";

	/**
	 * the column names
	 * 
	 * the _id column follows the convention required by the CursorAdapter usage
	 */
	public static final String COL_ID = "_id";
	public static final String COL_NAME = "name";
	public static final String COL_DESCRIPTION = "description";

	/**
	 * the creation query
	 */
	public static final String TABLE_CREATION_QUERY = "CREATE TABLE "
			+ TABNAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n"
			+ COL_NAME + " TEXT,\n" + COL_DESCRIPTION
			+ " TEXT);";

	/**
	 * the where clause for item deletion
	 */
	private static final String WHERE_IDENTIFY_ITEM = COL_ID + "=?";

	/**
	 * the database
	 */
	SQLiteDatabase db;

	/**
	 * the context
	 */
	private Context context;


	@Override
	public DataItem createDataItem(DataItem item) {
		Log.i(logger, "createDataItem(): " + item);

		ContentValues insertItem = createDBDataItem(item);
		long newItemId = this.db.insert(TABNAME, null, insertItem);
		Log.i(logger, "addItemToDb(): got new item id after insertion: "
				+ newItemId);
		item.setId(newItemId);
		
		return item;
	}

	@Override
	public List<DataItem> readAllDataItems() {
		List<DataItem> items = new ArrayList<DataItem>();
		
		// we make a query, which possibly will return an empty list
		SQLiteQueryBuilder querybuilder = new SQLiteQueryBuilder();
		querybuilder.setTables(TABNAME);
		// we specify all columns
		String[] asColumsToReturn = { COL_ID,COL_NAME,
				COL_DESCRIPTION};
		// we specify an ordering
		String ordering = COL_ID + " ASC";

		Cursor c = querybuilder.query(this.db, asColumsToReturn, null, null,
				null, null, ordering);

		Log.i(logger, "getAdapter(): got a cursor: " + c);

		c.moveToFirst();
		while (!c.isAfterLast()) {
			// create a new item and add it to the list
			items.add(createItemFromCursor(c));
			c.moveToNext();
		}

		Log.i(logger, "readOutItemsFromDatabase(): read out items: " + items);

		return items;		
	}

	@Override
	public DataItem readDataItem(long dateItemId) {
		throw new UnsupportedOperationException(
				"readDataItem() currently not supported by " + this.getClass());
	}

	@Override
	public DataItem updateDataItem(DataItem item) {
		Log.i(logger, "updateDataItem(): " + item);

		// do the update in the db
		this.db.update(TABNAME, createDBDataItem(item), WHERE_IDENTIFY_ITEM,
				new String[] { String.valueOf(item.getId()) });
		Log.i(logger, "updateItemInDb(): update has been carried out");
		
		return item;
	}

	@Override
	public boolean deleteDataItem(long dataItemId) {
		Log.i(logger, "removeItemFromDb(): " + dataItemId);

		// we first delete the item
		this.db.delete(TABNAME, WHERE_IDENTIFY_ITEM,
				new String[] { String.valueOf(dataItemId) });
		Log.i(logger, "deleteDataItem(): deletion in db done");		
		return true;
	}

	@Override
	public void setContext(Context context) {
		this.context = context;
		prepareSQLiteDatabase();
	}
	
	/*
	 * helper methods
	 */

	/**
	 * create a ContentValues object which can be passed to a db query
	 * 
	 * @param item
	 * @return
	 */
	private ContentValues createDBDataItem(DataItem item) {
		ContentValues insertItem = new ContentValues();
		insertItem.put(COL_NAME, item.getName());
		insertItem.put(COL_DESCRIPTION, item.getDescription());

		return insertItem;
	}


	/**
	 * create an item from the cursor
	 * 
	 * @param c
	 * @return
	 */
	public DataItem createItemFromCursor(Cursor c) {
		// create the item
		DataItem currentItem = new DataItem();

		// then populate the item with the results from the cursor
		currentItem.setId(c.getLong(c.getColumnIndex(COL_ID)));
		currentItem.setName(c.getString(c.getColumnIndex(COL_NAME)));
		currentItem.setDescription(c.getString(c
				.getColumnIndex(COL_DESCRIPTION)));

		return currentItem;
	}

	/**
	 * prepare the database
	 */
	protected void prepareSQLiteDatabase() {

		this.db = context.openOrCreateDatabase(DBNAME,
				SQLiteDatabase.CREATE_IF_NECESSARY, null);

		// we need to check whether it is empty or not...
		Log.d(logger, "db version is: " + db.getVersion());
		if (this.db.getVersion() == INITIAL_DBVERSION) {
			Log.i(logger,
					"the db has just been created. Need to create the table...");
			db.setLocale(Locale.getDefault());
			//db.setLockingEnabled(true);
			db.setVersion(INITIAL_DBVERSION + 1);
			db.execSQL(TABLE_CREATION_QUERY);
		} else {
			Log.i(logger, "the db exists already. No need for table creation.");
		}

	}

	@Override
	public void finalise() {
		this.db.close();
		Log.i(logger, "db has been closed");
	}
	
	/**
	 * provide the db (to the cursor adapter activity)
	 */
	public SQLiteDatabase getDB() {
		return this.db;
	}

}
