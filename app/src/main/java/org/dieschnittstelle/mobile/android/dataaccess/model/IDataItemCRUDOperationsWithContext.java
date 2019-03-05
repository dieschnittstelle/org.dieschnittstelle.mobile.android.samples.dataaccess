package org.dieschnittstelle.mobile.android.dataaccess.model;

import android.content.Context;

public interface IDataItemCRUDOperationsWithContext extends IDataItemCRUDOperations {

	/*
	 * pass a context
	 */
	public void setContext(Context context);
	
	/*
	 * signal finalisation
	 */
	public void finalise();

}
