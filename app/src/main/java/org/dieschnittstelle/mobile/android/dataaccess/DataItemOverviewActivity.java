package org.dieschnittstelle.mobile.android.dataaccess;

import java.util.ArrayList;
import java.util.List;

import org.dieschnittstelle.mobile.android.dataaccess.model.DataItem;
import org.dieschnittstelle.mobile.android.dataaccess.model.IDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.dataaccess.model.IDataItemCRUDOperationsFactory;
import org.dieschnittstelle.mobile.android.dataaccess.model.IDataItemCRUDOperationsWithContext;
import org.dieschnittstelle.mobile.android.dataaccess.model.IInitialisable;
import org.dieschnittstelle.mobile.android.dataaccess.model.IntentDataItemCRUDOperationsImpl;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DataItemOverviewActivity extends Activity {

	protected static String logger = DataItemOverviewActivity.class.getName();

	/**
	 * the result code that indicates that some item was changed
	 */
	public static final int RESPONSE_ITEM_EDITED = 1;

	/**
	 * the result code that indicates that the item shall be deleted
	 */
	public static final int RESPONSE_ITEM_DELETED = 2;

	/**
	 * the result code that indicates that nothing has been changed
	 */
	public static final int RESPONSE_NOCHANGE = -1;

	/**
	 * the constant for the subview request
	 */
	public static final int REQUEST_ITEM_DETAILS = 2;

	/**
	 * the constant for the new item request
	 */
	public static final int REQUEST_ITEM_CREATION = 1;

	/*
	 * make these attributes protected in order to make them settable by subclasses
	 */
	
	/**
	 * the field for the listview
	 */
	protected ListView listview;

	/**
	 * the list of objects to be displayed
	 */
	protected List<DataItem> itemlist = new ArrayList<DataItem>();

	/**
	 * the adapter that mediates between the itemlist and the listview
	 */
	protected ArrayAdapter<DataItem> adapter;

	/**
	 * the delegate that provides the model operations
	 */
	protected IDataItemCRUDOperations businessDelegate;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemlistview);

		try {
			// obtain the business delegate from the application
			this.businessDelegate = ((IDataItemCRUDOperationsFactory) getApplication())
					.getDataItemCRUDOperationsImplForName(getIntent()
							.getStringExtra(
									DataAccessActivity.ARG_DELEGATE_CLASS));
			// pass ourselves as context to the delegate
			if (this.businessDelegate instanceof IDataItemCRUDOperationsWithContext) {
				((IDataItemCRUDOperationsWithContext)this.businessDelegate).setContext(this);
			}

			Log.i(logger, "using business delegate impl: " + this.businessDelegate);

			// access the listview
			listview = (ListView) findViewById(R.id.list);

			// the button for adding new items
			Button newitemButton = (Button) findViewById(R.id.newitemButton);

			// set the title of the activity given the delegate class
			setTitle(this.businessDelegate.getClass().getSimpleName());

			// instantiate the adapter and override the getView method
			this.adapter = new ArrayAdapter<DataItem>(this,
					R.layout.item_in_listview, this.itemlist) {

				@Override
				public View getView(int position, View itemView,
						ViewGroup parent) {
					// obtain the element from the list
					DataItem item = itemlist.get(position);

					// check whether we already have an itemView from a previous
					// display, then we can reuse it and only need to update the
					// content that is displayed
					if (itemView == null) {
						Log.d(logger, "creating new view for item at position "
								+ position + ": " + item);
						itemView = getLayoutInflater().inflate(
								R.layout.item_in_listview, null);
					} else {
						Log.d(logger,
								"using existing view for item at position "
										+ position + ": " + item);
					}
					// obtain the name field from the item view
					TextView nameView = (TextView) itemView
							.findViewById(R.id.item_name);

					// and set the item's name into the field
					nameView.setText(item.getName());

					// then return the itemView
					return itemView;
				}

			};

			// set the adapter on the list view
			listview.setAdapter(adapter);

			// set a listener that reacts to the selection of an element
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView,
						View itemView, int itemPosition, long itemId) {

					Log.i(logger, "onItemClick: position is: " + itemPosition
							+ ", id is: " + itemId);

					DataItem item = adapter.getItem(itemPosition);

					processItemSelection(item);
				}

			});

			// set the listview as scrollable
			listview.setScrollBarStyle(ListView.SCROLLBARS_INSIDE_OVERLAY);

			// set a listener for the newItemButton
			newitemButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					processNewItemRequest();
				}

			});

			// load the items, check whether the delegate needs to be initialised before
			if (businessDelegate instanceof IInitialisable) {
				Log.i(logger,"business delegate impl requires initialisation...");
				((IInitialisable)businessDelegate).initialise(() -> doLoadItems());
			}
			else {
				Log.i(logger,"business delegate impl does not require initialisation. Load items");
				doLoadItems();
			}

		} catch (Exception e) {
			String err = "got exception: " + e;
			Log.e(logger, err, e);
			Toast.makeText(this, err, Toast.LENGTH_LONG).show();
		}

	}

	/*
	 * processing user input
	 */

	protected void processNewItemRequest() {
		Log.i(logger, "processNewItemRequest()");
		Intent intent = new Intent(DataItemOverviewActivity.this,
				DataItemDetailsViewActivity.class);
		// we only specify the accessor class
		intent.putExtra(DataAccessActivity.ARG_DELEGATE_CLASS,
				IntentDataItemCRUDOperationsImpl.class.getName());

		// start the details activity with the intent
		startActivityForResult(intent, REQUEST_ITEM_CREATION);
	}

	protected void processItemSelection(DataItem item) {
		Log.i(logger, "processItemSelection(): " + item);
		// create an intent for opening the details view
		Intent intent = new Intent(DataItemOverviewActivity.this,
				DataItemDetailsViewActivity.class);
		// pass the item to the intent
		intent.putExtra(DataItemDetailsViewActivity.ARG_ITEM_OBJECT, item);
		// also specify the accessor class
		intent.putExtra(DataAccessActivity.ARG_DELEGATE_CLASS,
				IntentDataItemCRUDOperationsImpl.class.getName());

		// start the details activity with the intent
		startActivityForResult(intent, REQUEST_ITEM_DETAILS);
	}

	/**
	 * process the result of the item details subactivity, which may be the
	 * creation, modification or deletion of an item.
	 * 
	 * NOTE that is not necessary to invalidate the listview if changes are
	 * communicated to the adapter using notifyDataSetChanged()
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(logger, "onActivityResult(): " + data);

		DataItem item = data != null ? (DataItem) data
				.getSerializableExtra(DataItemDetailsViewActivity.ARG_ITEM_OBJECT)
				: null;

		// check which request we had
		if (requestCode == REQUEST_ITEM_DETAILS) {
			if (resultCode == RESPONSE_ITEM_EDITED) {
				Log.i(logger, "onActivityResult(): updating the edited item");
				doUpdateItem(item);
			} else if (resultCode == RESPONSE_ITEM_DELETED) {
				doDeleteItem(item);
			}
			// this.listview.invalidate();
		} else if (requestCode == REQUEST_ITEM_CREATION
				&& resultCode == RESPONSE_ITEM_EDITED) {
			Log.i(logger, "onActivityResult(): initiate item creation");
			doCreateItem(item);
		}

	}

	/*
	 * we use AsyncTask for all data access operations (we use minimalistic code
	 * here)
	 */
	protected void doLoadItems() {
		new AsyncTask<Void, Void, List<DataItem>>() {

			@Override
			protected List<DataItem> doInBackground(Void... params) {
				return businessDelegate.readAllDataItems();
			}

			@Override
			protected void onPostExecute(List<DataItem> items) {
				for (DataItem item : items) {
					adapter.add(item);
				}
			}

		}.execute();
	}

	protected void doCreateItem(DataItem item) {
		new AsyncTask<DataItem, Void, DataItem>() {

			@Override
			protected DataItem doInBackground(DataItem... params) {
				return businessDelegate.createDataItem(params[0]);
			}

			@Override
			protected void onPostExecute(DataItem item) {
				adapter.add(item);
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
					adapter.remove(item);
				} else {
//					Toast.makeText(
//							DataItemOverviewActivity.this,
//							"The item " + item.getName()
//									+ " could not be deleted!",
//							Toast.LENGTH_LONG).show();
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
				itemlist.get(itemlist.indexOf(item)).updateFrom(item);
				adapter.notifyDataSetChanged();
			}

		}.execute(item);
	}

	/**
	 * if we stop, we signal this to the accessor (which is necessary in order
	 * to avoid trouble when operating on dbs)
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(logger, "onDestroy(): will signal finalisation to the delegate");
		if (this.businessDelegate instanceof IDataItemCRUDOperationsWithContext) {
			((IDataItemCRUDOperationsWithContext)this.businessDelegate).finalise();
		}

		super.onStop();
	}

}
