package org.dieschnittstelle.mobile.android.dataaccess.model;

import java.util.List;

import org.dieschnittstelle.mobile.android.dataaccess.DataItemDetailsViewActivity;
import org.dieschnittstelle.mobile.android.dataaccess.DataItemOverviewActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class IntentDataItemCRUDOperationsImpl implements
		IDataItemCRUDOperationsWithContext {

	// we need an activity as context
	private Activity context;

	/**
	 * we need an activity as context
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = (Activity) context;
	}

	@Override
	public DataItem createDataItem(DataItem item) {
		// and return to the calling activity
		Intent returnIntent = new Intent();

		// set the item
		returnIntent
				.putExtra(DataItemDetailsViewActivity.ARG_ITEM_OBJECT, item);

		// set the result code
		context.setResult(DataItemOverviewActivity.RESPONSE_ITEM_EDITED,
				returnIntent);

		return item;
	}

	@Override
	public List<DataItem> readAllDataItems() {
		throw new UnsupportedOperationException(
				"readAllDateItems() not supported by " + this.getClass());
	}

	@Override
	public DataItem readDataItem(long dateItemId) {
		return (DataItem) context.getIntent().getSerializableExtra(
				DataItemDetailsViewActivity.ARG_ITEM_OBJECT);
	}

	@Override
	public DataItem updateDataItem(DataItem item) {
		// and return to the calling activity
		Intent returnIntent = new Intent();

		// set the item
		returnIntent
				.putExtra(DataItemDetailsViewActivity.ARG_ITEM_OBJECT, item);

		// set the result code
		context.setResult(DataItemOverviewActivity.RESPONSE_ITEM_EDITED,
				returnIntent);

		return item;
	}

	@Override
	public boolean deleteDataItem(final long dataItemId) {
		// and return to the calling activity
		Intent returnIntent = new Intent();
		
		DataItem item = new DataItem();
		item.setId(dataItemId);

		// set the item
		returnIntent.putExtra(DataItemDetailsViewActivity.ARG_ITEM_OBJECT,item);

		// set the result code
		context.setResult(DataItemOverviewActivity.RESPONSE_ITEM_DELETED,
				returnIntent);
		
		return true;
	}

	@Override
	public void finalise() {
		// TODO Auto-generated method stub

	}

}
