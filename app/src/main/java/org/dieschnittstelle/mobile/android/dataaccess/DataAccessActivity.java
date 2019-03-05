package org.dieschnittstelle.mobile.android.dataaccess;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DataAccessActivity extends Activity {

	public static final String ARG_DELEGATE_CLASS = "businessDelegateClass";
	
	public static final String MODEL_SUBPACKAGE = "model";

	/**
	 * the logger
	 */
	protected static final String logger = DataAccessActivity.class.getName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			// set the list view as content view
			setContentView(R.layout.listview);

			/*
			 * access the list view for the options to be displayed
			 */
			ListView listview = (ListView) findViewById(R.id.list);

			// read out the options
			final String[] menuItems = getResources().getStringArray(
					R.array.main_menu);

			/*
			 * create an adapter that allows for the view to access the list's
			 * content and that holds information about the visual
			 * representation of the list items
			 */
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, menuItems);

			// set the adapter on the list view
			listview.setAdapter(adapter);

			// set a listener that reacts to the selection of an element
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView,
						View selectedView, int itemPosition, long arg3) {

					String selectedItem = (String) adapterView.getAdapter()
							.getItem(itemPosition);

					Log.i(this.getClass().getName(), "got item selected: "
							+ selectedItem);

					handleSelectedItem(selectedItem);
				}

			});
		} catch (Exception e) {
			String err = "got an exception: " + e;
			Log.e(logger, err, e);
			Toast.makeText(this, err,Toast.LENGTH_LONG).show();
		}
	}

	protected void handleSelectedItem(String selectedItem) {
		/*
		 * depending on the selected item, we create an activity and pass it the
		 * accessor class to be used. The class name will be created given the
		 * selected item
		 * 
		 * that quite lengthy expressions do the following:
		 * 
		 * 1) determine the position of the selected item in the main_menu array
		 * resource
		 * 
		 * 2) read out that position from the main_menu_activities and
		 * main_menu_accessors resources, respectively
		 */
		try {
			Intent intent = new Intent(DataAccessActivity.this,
					Class.forName(this.getClass().getPackage().getName()
							+ "."
							+ getResources().getStringArray(
									R.array.main_menu_activities)[Arrays
									.asList(getResources().getStringArray(
											R.array.main_menu)).indexOf(
											selectedItem)]));
			intent.putExtra(
					ARG_DELEGATE_CLASS,
					DataAccessActivity.class.getPackage().getName()
							+ "."
							+ MODEL_SUBPACKAGE
							+ "."
							+ getResources().getStringArray(
									R.array.main_menu_delegates)[Arrays.asList(
									getResources().getStringArray(
											R.array.main_menu)).indexOf(
									selectedItem)]);

			/*
			 * start the activity
			 */
			startActivity(intent);
		} catch (Exception e) {
			String err = "got exception trying to handle selected item "
					+ selectedItem + ": " + e;
			Log.e(logger, err, e);
			Toast.makeText(this,err,Toast.LENGTH_LONG).show();
		}
	}
}
