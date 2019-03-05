package org.dieschnittstelle.mobile.android.dataaccess.model;

import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class PropertiesDataItemCRUDOperationsImpl implements
		IDataItemCRUDOperationsWithContext {

	/**
	 * the logger
	 */
	protected static final String logger = PropertiesDataItemCRUDOperationsImpl.class
			.getName();

	/**
	 * the filename
	 */
	private static final String PROPERTIES_FILE = "dataitem.txt";

	/**
	 * the item id property
	 */
	private static final String PROP_ITEM_ID = "dataItemId";

	/**
	 * the item name property
	 */
	private static final String PROP_ITEM_NAME = "dataItemName";

	/**
	 * the item description property
	 */
	private static final String PROP_ITEM_DESC = "dataItemDescription";

	/**
	 * the properties
	 */
	private Properties props;
	
	/**
	 * the context
	 */
	private Context context;

	@Override
	public DataItem createDataItem(DataItem item) {
		Log.i(logger, "createDataItem(): " + item);

		props.setProperty(PROP_ITEM_ID, String.valueOf(item.getId()));
		props.setProperty(PROP_ITEM_NAME, item.getName());
		props.setProperty(PROP_ITEM_DESC, item.getDescription());

		writeProperties();
		
		return item;
	}

	@Override
	public List<DataItem> readAllDataItems() {
		throw new UnsupportedOperationException("readAllDateItems() not supported by " + this.getClass());
	}

	@Override
	public DataItem readDataItem(long dateItemId) {
		Log.i(logger, "readDataItem()...");

		loadProperties();
		DataItem item = new DataItem(Long.parseLong(props.getProperty(
				PROP_ITEM_ID, "-1")), props.getProperty(PROP_ITEM_NAME, ""),
				props.getProperty(PROP_ITEM_DESC, ""));

		Log.i(logger, "readDataItem(): " + item);

		return item;
	}

	@Override
	public DataItem updateDataItem(DataItem item) {
		return createDataItem(item);
	}

	@Override
	public boolean deleteDataItem(long dataItemId) {
		Log.i(logger, "deleteDataItem()...");
		this.props.setProperty(PROP_ITEM_ID, "-1");
		this.props.setProperty(PROP_ITEM_NAME, "");
		this.props.setProperty(PROP_ITEM_DESC, "");

		writeProperties();
		return true;
	}
	
	private void writeProperties() {
		try {
			props.store(
					context.openFileOutput(PROPERTIES_FILE,
							Activity.MODE_APPEND), "");
			Log.i(logger, "writeProperties(): properties have been written: "
					+ props);
		} catch (Exception e) {
			String err = "got exception trying to write properties: " + e;
			Log.e(logger, err, e);
			Toast.makeText(context, err, Toast.LENGTH_LONG).show();
		}
	}

	private void loadProperties() {
		this.props = new Properties();
		try {
			props.load(context.openFileInput(PROPERTIES_FILE));
			Log.i(logger, "loadProperties(): properties have been loaded: "
					+ props);
		} catch (Exception e) {
			Log.w(logger, "got exception trying to read properties: " + e
					+ ". Maybe they do not exist so far. Try to create...", e);
			writeProperties();
		}
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
