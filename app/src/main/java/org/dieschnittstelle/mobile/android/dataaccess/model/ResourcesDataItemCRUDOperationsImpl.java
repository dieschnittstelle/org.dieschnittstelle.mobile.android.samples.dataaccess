package org.dieschnittstelle.mobile.android.dataaccess.model;

import java.util.ArrayList;
import java.util.List;

import org.dieschnittstelle.mobile.android.dataaccess.R;

import android.content.Context;

public class ResourcesDataItemCRUDOperationsImpl implements
		IDataItemCRUDOperationsWithContext {

	private Context context;

	@Override
	public DataItem createDataItem(DataItem item) {
		return item;
	}

	@Override
	public List<DataItem> readAllDataItems() {
		// the options
		String[] items = context.getResources().getStringArray(
				R.array.item_list);
		// create a list
		List<DataItem> itemlist = new ArrayList<DataItem>();
		for (String item : items) {
			itemlist.add(new DataItem(-1, item, ""));
		}

		return itemlist;
	}

	@Override
	public DataItem readDataItem(long dateItemId) {
		throw new UnsupportedOperationException(
				"readDataItem() currently not supported by " + this.getClass());
	}

	@Override
	public DataItem updateDataItem(DataItem item) {
		return item;
	}

	@Override
	public boolean deleteDataItem(long dataItemId) {
		return true;
	}

	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public void finalise() {
		// TODO Auto-generated method stub
		
	}

}
