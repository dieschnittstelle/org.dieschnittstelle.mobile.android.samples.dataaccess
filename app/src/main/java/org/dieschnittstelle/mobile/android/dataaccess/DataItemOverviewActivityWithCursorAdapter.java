package org.dieschnittstelle.mobile.android.dataaccess;

import org.dieschnittstelle.mobile.android.dataaccess.model.DataItem;
import org.dieschnittstelle.mobile.android.dataaccess.model.SQLiteDataItemCRUDOperationsImpl;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DataItemOverviewActivityWithCursorAdapter extends
		DataItemOverviewActivity {

	protected Cursor cursor;

	protected CursorAdapter adapter;

	public void onCreate(Bundle savedInstanceState) {
		// we call the superclass method
		super.onCreate(savedInstanceState);

		// we will use the SQLiteDataItemCRUDOperationsImpl for doing the db
		// access and just use our own cursor
		SQLiteQueryBuilder querybuilder = new SQLiteQueryBuilder();
		querybuilder.setTables(SQLiteDataItemCRUDOperationsImpl.TABNAME);
		// we specify all columns
		String[] asColumsToReturn = { SQLiteDataItemCRUDOperationsImpl.COL_ID,
				SQLiteDataItemCRUDOperationsImpl.COL_NAME,
				SQLiteDataItemCRUDOperationsImpl.COL_DESCRIPTION };
		// we specify an ordering
		String ordering = SQLiteDataItemCRUDOperationsImpl.COL_ID + " ASC";

		this.cursor = querybuilder.query(
				((SQLiteDataItemCRUDOperationsImpl) this.businessDelegate)
						.getDB(), asColumsToReturn, null, null, null, null,
				ordering);

		this.startManagingCursor(this.cursor);

		/*
		 * create a cursor adapter that maps the "name" column in the db to the
		 * itemName element in the view
		 * 
		 * (i.e. using this adapter there is no need to create DataItem objects
		 * for all items that are contained in the db)
		 */
		this.adapter = new SimpleCursorAdapter(this, R.layout.item_in_listview,
				this.cursor,
				new String[] { SQLiteDataItemCRUDOperationsImpl.COL_NAME },
				new int[] { R.id.item_name });

		listview.setAdapter(this.adapter);

		// set a listener that reacts to the selection of an element and reads out the item from the cursor
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View itemView,
					int itemPosition, long itemId) {

				Log.i(logger, "onItemClick: position is: " + itemPosition
						+ ", id is: " + itemId);

				DataItem item = ((SQLiteDataItemCRUDOperationsImpl) DataItemOverviewActivityWithCursorAdapter.this.businessDelegate)
						.createItemFromCursor((Cursor) DataItemOverviewActivityWithCursorAdapter.this.adapter
								.getItem(itemPosition));

				processItemSelection(item);
			}

		});
	}

	/*
	 * we use implement all data access operations ourselves
	 */

	protected void doLoadItems() {
		// this is dealt with by the adapter
	}

	protected void doCreateItem(DataItem item) {
		new AsyncTask<DataItem, Void, DataItem>() {

			@Override
			protected DataItem doInBackground(DataItem... params) {
				return businessDelegate.createDataItem(params[0]);
			}

			@Override
			protected void onPostExecute(DataItem item) {
				DataItemOverviewActivityWithCursorAdapter.this.cursor.requery();
			}

		}.execute(item);
	}

	protected void doDeleteItem(final DataItem item) {
		new AsyncTask<DataItem, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(DataItem... params) {
				return businessDelegate.deleteDataItem(item.getId());
			}

			@Override
			protected void onPostExecute(Boolean deleted) {
				if (deleted) {
					DataItemOverviewActivityWithCursorAdapter.this.cursor
							.requery();
				} else {
					Toast.makeText(
							DataItemOverviewActivityWithCursorAdapter.this,
							"The item " + item.getName()
									+ " could not be deleted!",
							Toast.LENGTH_LONG).show();
				}
			}

		}.execute(item);
	}

	protected void doUpdateItem(DataItem item) {
		new AsyncTask<DataItem, Void, DataItem>() {

			@Override
			protected DataItem doInBackground(DataItem... params) {
				businessDelegate.updateDataItem(params[0]);
				return params[0];
			}

			@Override
			protected void onPostExecute(DataItem item) {
				DataItemOverviewActivityWithCursorAdapter.this.cursor.requery();
			}

		}.execute(item);
	}

}
