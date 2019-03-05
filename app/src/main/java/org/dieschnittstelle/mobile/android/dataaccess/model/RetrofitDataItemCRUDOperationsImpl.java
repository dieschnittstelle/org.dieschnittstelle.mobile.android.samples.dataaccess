package org.dieschnittstelle.mobile.android.dataaccess.model;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/*
 * this class demonstrates access to web apis using the Retrofit framework
 * see: https://square.github.io/retrofit/
 */
public class RetrofitDataItemCRUDOperationsImpl implements IDataItemCRUDOperationsWithURL, IInitialisable {

    private static String logger = RetrofitDataItemCRUDOperationsImpl.class.getSimpleName();

    private String baseUrl;
    private DataItemCRUDWebAPI webAPIProxy;

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void initialise(Runnable oninitialised) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // we instantiate the retroft proxy to the remote api we want to access
        this.webAPIProxy = retrofit.create(DataItemCRUDWebAPI.class);

        Log.i(logger,"created proxy for baseUrl " + this.baseUrl + ": " + this.webAPIProxy);

        oninitialised.run();
    }

    // declare the interface using retrofit annotations and signatures
    private static interface DataItemCRUDWebAPI {

        @POST("dataitems")
        public Call<DataItem> createDataItem(@Body DataItem item);

        @GET("dataitems")
        public Call<List<DataItem>> readAllDataItems();

        @GET("dataitems/{dataItemId}")
        public Call<DataItem> readDataItem(@Path("dataItemId") long dataItemId);

        @PUT("dataitems")
        public Call<DataItem> updateDataItem(@Body DataItem item);

        @DELETE("dataitems/{dataItemId}")
        public Call<Boolean> deleteDataItem(@Path("dataItemId") long dataItemId);

    }

    public RetrofitDataItemCRUDOperationsImpl() {

    }

    @Override
    public DataItem createDataItem(DataItem item) {
        try {
            return this.webAPIProxy.createDataItem(item).execute().body();
        }
        catch (Exception e) {
            String err = "got exception trying to access retrofit: " + e;
            Log.e(logger,err,e);
            throw new RuntimeException(err,e);
        }
    }

    @Override
    public List<DataItem> readAllDataItems() {
        try {
            Log.i(logger,"readAllDataItems()");
            List<DataItem> items = this.webAPIProxy.readAllDataItems().execute().body();
            Log.i(logger,"readAllDataItems(): " + items);

            return items;
        }
        catch (Exception e) {
            String err = "got exception trying to access retrofit: " + e;
            Log.e(logger,err,e);
            throw new RuntimeException(err,e);
        }
    }

    @Override
    public DataItem readDataItem(long dataItemId) {
        try {
            return this.webAPIProxy.readDataItem(dataItemId).execute().body();
        }
        catch (Exception e) {
            String err = "got exception trying to access retrofit: " + e;
            Log.e(logger,err,e);
            throw new RuntimeException(err,e);
        }
    }

    @Override
    public DataItem updateDataItem(DataItem item) {
        try {
            return this.webAPIProxy.updateDataItem(item).execute().body();
        }
        catch (Exception e) {
            String err = "got exception trying to access retrofit: " + e;
            Log.e(logger,err,e);
            throw new RuntimeException(err,e);
        }
    }

    @Override
    public boolean deleteDataItem(long dataItemId) {
        try {
            return this.webAPIProxy.deleteDataItem(dataItemId).execute().body();
        }
        catch (Exception e) {
            String err = "got exception trying to access retrofit: " + e;
            Log.e(logger,err,e);
            throw new RuntimeException(err,e);
        }
    }
}
