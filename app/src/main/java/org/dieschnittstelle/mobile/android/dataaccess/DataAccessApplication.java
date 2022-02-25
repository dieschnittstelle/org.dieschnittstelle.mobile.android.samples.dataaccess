package org.dieschnittstelle.mobile.android.dataaccess;

import org.dieschnittstelle.mobile.android.dataaccess.model.IDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.dataaccess.model.IDataItemCRUDOperationsFactory;
import org.dieschnittstelle.mobile.android.dataaccess.model.IDataItemCRUDOperationsWithURL;
import org.dieschnittstelle.mobile.android.dataaccess.model.contentprovider.DataItemContract;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

import android.app.Application;
import android.util.Log;


public class DataAccessApplication extends Application implements
		IDataItemCRUDOperationsFactory {

	protected static String logger = DataAccessApplication.class.getName();

	public static final String WEBAPP_BASEURL_LOCALHOST_FROM_ANDROIDSTUDIO_EMULATOR = "http://10.0.2.2:8080/api/";
	public static final String WEBAPP_BASEURL_LOCALHOST_FROM_GENYMOTION_EMULATOR = "http://10.0.3.2:8080/api/";
	// TODO: change this url using the ip address of the machine on which the web application is started
	public static final String WEBAPP_BASEURL_IN_LOCAL_NETWORK = "http://192.168.2.101:8080/api/";

	/**
	 * the baseUrl - TODO: assign the required value for your local development setup
	 */
	private String baseUrl = WEBAPP_BASEURL_IN_LOCAL_NETWORK;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(logger,"onCreate(): will try to pass baseUrl to content provider...");
		
		// onCreate, we pass the baseUrl to the content provider
		getContentResolver().call(DataItemContract.CONTENT_URI, DataItemContract.METHOD_SET_BASE_URL, this.baseUrl, null);
	}
	
	
	@Override
	public IDataItemCRUDOperations getDataItemCRUDOperationsImplForName(
			String implName) {
		try {
			// if we are using the JAX-RS access, we create a resteasy client using the ProxyFactory and passing the annotated interface
			if ("org.dieschnittstelle.mobile.android.dataaccess.model.JAXRSDataItemCRUDOperationsImpl".equals(implName)) {
				return ProxyFactory.create(IDataItemCRUDOperations.class,
						baseUrl,
						new ApacheHttpClient4Executor());
			} 
			// otherwise we use reflection to obtain a new instance of the class whose name has been passed
			else {
				Class<?> implClass = Class.forName(implName);
				IDataItemCRUDOperations instance = (IDataItemCRUDOperations) implClass.newInstance();
				
				// if the implementation requires a url, we pass it
				if (instance instanceof IDataItemCRUDOperationsWithURL) {
					((IDataItemCRUDOperationsWithURL)instance).setBaseUrl(this.baseUrl);
				}
				return instance;
			}
		} catch (Exception e) {
			Log.e(logger, "got exception: " + e, e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
