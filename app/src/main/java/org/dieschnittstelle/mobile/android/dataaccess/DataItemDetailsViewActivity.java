package org.dieschnittstelle.mobile.android.dataaccess;

import org.dieschnittstelle.mobile.android.dataaccess.model.DataItem;
import org.dieschnittstelle.mobile.android.dataaccess.model.IDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.dataaccess.model.IDataItemCRUDOperationsFactory;
import org.dieschnittstelle.mobile.android.dataaccess.model.IDataItemCRUDOperationsWithContext;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DataItemDetailsViewActivity extends Activity {

	public static final String ARG_ITEM_OBJECT = "itemObject";

	/**
	 * the logger
	 */
	protected static final String logger = DataItemDetailsViewActivity.class
			.getName();

	/**
	 * the delegate
	 */
	private IDataItemCRUDOperations businessDelegate;

	/**
	 * the item that is being created or edited
	 */
	private DataItem item;

	/**
	 * track whether we are creating a new item
	 */
	private boolean itemCreationMode;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemview);

		try {
			// obtain the ui elements
			final EditText itemName = (EditText) findViewById(R.id.item_name);
			final EditText itemDescription = (EditText) findViewById(R.id.item_description);
			Button saveButton = (Button) findViewById(R.id.saveButton);
			Button deleteButton = (Button) findViewById(R.id.deleteButton);

			// instantiate the delegate
			this.businessDelegate = ((IDataItemCRUDOperationsFactory) getApplication())
					.getDataItemCRUDOperationsImplForName(getIntent()
							.getStringExtra(
									DataAccessActivity.ARG_DELEGATE_CLASS));
			// pass ourselves
			if (this.businessDelegate instanceof IDataItemCRUDOperationsWithContext) {
				((IDataItemCRUDOperationsWithContext)this.businessDelegate).setContext(this);
			}

			// set the title of the activity given the accessor class
			setTitle(this.businessDelegate.getClass().getSimpleName());

			// check whether we have been passed an item
			this.item = this.businessDelegate.readDataItem(-1);

			if (this.item == null) {
				this.item = new DataItem(-1, "", "");
				this.itemCreationMode = true;
			} else {
				itemName.setText(this.item.getName());
				itemDescription.setText(this.item.getDescription());
			}

			// we only set a listener on the ok button that will collect the
			// edited fields and set the values on the item
			saveButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					processItemSave(itemName, itemDescription);
				}
			});

			// we also set a listener on the delete button
			// we only set a listener on the ok button that will collect the
			// edited fields and set the values on the item
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					processItemDelete();
				}
			});

		} catch (Exception e) {
			String err = "got exception: " + e;
			Log.e(logger, err, e);
			Toast.makeText(this, err, Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * save the item and finish
	 * 
	 * @param accessor
	 * @param name
	 * @param description
	 */
	protected void processItemSave(EditText name, EditText description) {
		// re-set the fields of the item
		this.item.setName(name.getText().toString());
		this.item.setDescription(description.getText().toString());

		// save the item
		if (this.itemCreationMode) {
			this.businessDelegate.createDataItem(this.item);
		} else {
			this.businessDelegate.updateDataItem(this.item);
		}

		// and finish
		finish();
	}

	/**
	 * delete the item and finish
	 * 
	 * @param accessor
	 * @param name
	 * @param description
	 */
	protected void processItemDelete() {
		// delete the item
		this.businessDelegate.deleteDataItem(this.item.getId());

		// and finish
		finish();
	}

}
