package org.dieschnittstelle.mobile.android.dataaccess;

import org.dieschnittstelle.mobile.android.dataaccess.model.DataItem;
import org.dieschnittstelle.mobile.android.dataaccess.model.SQLiteDataItemCRUDOperationsImpl;
import org.dieschnittstelle.mobile.android.dataaccess.model.contentprovider.DataItemContentProvider;
import org.dieschnittstelle.mobile.android.dataaccess.model.contentprovider.DataItemContract;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;

public class DataItemOverviewActivityWithDataItemCursorAdapter extends
		DataItemOverviewActivityWithCursorAdapter {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// we call the superclass method - note that this results in two queries
		// being made initially: one by the superclass, and one by ourselves
		super.onCreate(savedInstanceState);

		this.cursor = new DataItemContentProvider(this.businessDelegate).query(
				null, null, null, null, null);

		this.startManagingCursor(this.cursor);

		/*
		 * create a cursor adapter that maps the "name" column in the cursor to the
		 * itemName element in the view
		 */
		this.adapter = new SimpleCursorAdapter(this, R.layout.item_in_listview,
				this.cursor,
				new String[] { DataItemContract.COLUMN_ITEM_NAME },
				new int[] { R.id.item_name });

		listview.setAdapter(this.adapter);

		// set a listener that reacts to the selection of an element and reads
		// out the item from the cursor
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View itemView,
					int itemPosition, long itemId) {

				Log.i(logger, "onItemClick: position is: " + itemPosition
						+ ", id is: " + itemId);

				DataItem item = ((SQLiteDataItemCRUDOperationsImpl) DataItemOverviewActivityWithDataItemCursorAdapter.this.businessDelegate)
						.createItemFromCursor((Cursor) DataItemOverviewActivityWithDataItemCursorAdapter.this.adapter
								.getItem(itemPosition));

				processItemSelection(item);
			}

		});
	}

}
