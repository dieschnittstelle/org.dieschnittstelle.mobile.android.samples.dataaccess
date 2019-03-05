package org.dieschnittstelle.mobile.android.dataaccess.model;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferencesDataItemCRUDOperationsImpl implements
		IDataItemCRUDOperationsWithContext {

	/**
	 * the logger
	 */
	protected static final String logger = PreferencesDataItemCRUDOperationsImpl.class
			.getName();

	/**
	 * the shared preferences id
	 */
	public static final String PREFERENCES_ID = "SharedPreferencesDataItem";

	/**
	 * the item id preference
	 */
	private static final String PREF_ITEM_ID = "dataItemId";

	/**
	 * the item name preference
	 */
	private static final String PREF_ITEM_NAME = "dataItemName";

	/**
	 * the item description preference
	 */
	private static final String PREF_ITEM_DESC = "dataItemDescription";

	/**
	 * the item
	 */
	private DataItem item;

	private SharedPreferences prefs;

	/**
	 * the activity that is passed to this accessor
	 */
	private Context context;

	@Override
	public DataItem createDataItem(DataItem item) {
		Log.i(logger, "createDataItem(): " + item);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(PREF_ITEM_ID, item.getId());
		editor.putString(PREF_ITEM_NAME, item.getName());
		editor.putString(PREF_ITEM_DESC, item.getDescription());

		editor.commit();

		return item;
	}

	@Override
	public List<DataItem> readAllDataItems() {
		throw new UnsupportedOperationException(
				"readAllDateItems() not supported by " + this.getClass());
	}

	@Override
	public DataItem readDataItem(long dateItemId) {
		Log.i(logger, "readDataItem()...");

		loadPreferences();
		if (this.item == null) {
			this.item = new DataItem(prefs.getLong(PREF_ITEM_ID, -1),
					prefs.getString(PREF_ITEM_NAME, ""), prefs.getString(
							PREF_ITEM_DESC, ""));

			Log.i(logger, "readItem(): " + this.item);
		}

		return this.item;
	}

	/**
	 * as we only manage a single item, we call the create method here
	 */
	@Override
	public DataItem updateDataItem(DataItem item) {
		return createDataItem(item);
	}

	@Override
	public boolean deleteDataItem(long dataItemId) {
		Log.i(logger, "deleteDataItem()...");
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(PREF_ITEM_ID);
		editor.remove(PREF_ITEM_NAME);
		editor.remove(PREF_ITEM_DESC);

		editor.commit();
		
		return true;
	}

	/**
	 * access the preferences
	 */
	private void loadPreferences() {
		if (this.prefs == null) {
			this.prefs = context.getSharedPreferences(PREFERENCES_ID,
					Activity.MODE_PRIVATE);
			Log.i(logger, "loadPreferences(): " + this.prefs.getAll());
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
