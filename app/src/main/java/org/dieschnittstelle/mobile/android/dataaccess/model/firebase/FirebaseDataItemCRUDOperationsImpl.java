package org.dieschnittstelle.mobile.android.dataaccess.model.firebase;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.dieschnittstelle.mobile.android.dataaccess.model.DataItem;
import org.dieschnittstelle.mobile.android.dataaccess.model.IDataItemCRUDOperationsWithContext;
import org.dieschnittstelle.mobile.android.dataaccess.model.IInitialisable;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * simple example for accessing firebase with email+pwd authentication
 */
public class FirebaseDataItemCRUDOperationsImpl implements IDataItemCRUDOperationsWithContext, IInitialisable {

    private static String logger = FirebaseDataItemCRUDOperationsImpl.class.getSimpleName();

    private static final String FIREBASE_COLLECTION_NAME = "todos";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    // your account credentials
    private String myemail = "s@bht.de";
    private String mypwd = "000000";

    private Context context;

    public FirebaseDataItemCRUDOperationsImpl() {
    }

    @Override
    public DataItem createDataItem(DataItem item) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            ((Activity)context).runOnUiThread(() -> Toast.makeText(this.context,"createDataItem(): will not access firestore. user does not seem to be logged in.",Toast.LENGTH_LONG).show());
            return item;
        }
        Log.i(logger,"createDataItem(): will access firestore using: " + firebaseFirestore);

        // as we implement a synchronous api, we use the task object returned from the crud operations on collections and the Tasks.await() method (further below)
        Task<DocumentReference> addTask = firebaseFirestore.collection(FIREBASE_COLLECTION_NAME).add(createFirebaseMapFromItem(item));
        addTask.addOnSuccessListener(doc -> {
            Log.i(logger,"createDataItem(): access to firestore succesfully completed. Got: " + doc);
        });
        addTask.addOnFailureListener(err -> {
            ((Activity)context).runOnUiThread(() -> Toast.makeText(context,"createDataItem(): failed to access firestore. Got error: " + err,Toast.LENGTH_LONG).show());
        });

        Log.i(logger,"createDataItem(): now accessing firestore...");
        try {
            Tasks.await(addTask);
            DocumentReference addResult = addTask.getResult();

            if (addResult != null) {
                Log.i(logger, "createDataItem(): access done. Got result with id: " + addResult.getId());
                // we create a new FirebaseDataItem, passing the firebase id
                item = new FirebaseDataItem(item, addResult.getId());
                Log.i(logger, "createDataItem(): created new data item with firebase id: " + item);
            }

            return item;
        }
        catch (Exception e) {
            String errmsg = "createDataItem(): got exception trying to access firestore: " + e;
            Log.e(logger,errmsg,e);
            ((Activity)context).runOnUiThread(() -> Toast.makeText(context,errmsg,Toast.LENGTH_LONG).show());
            return item;
        }
    }

    @Override
    public List<DataItem> readAllDataItems() {
        try {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) {
                ((Activity) context).runOnUiThread(() -> Toast.makeText(this.context, "readAllDataItems(): will not access firestore. user does not seem to be logged in.", Toast.LENGTH_LONG).show());
                return new ArrayList<>();
            }

            // access the db and read out all documents for the current user
            // as we implement a synchronous api, we use the task object returned from the crud operations on collections and the Tasks.await() method (further below)
            Task<QuerySnapshot> readTask = firebaseFirestore.collection(FIREBASE_COLLECTION_NAME).whereEqualTo("owner", currentUser.getUid()).get();
            readTask.addOnSuccessListener(docs -> {
                Log.i(logger, "readAllDataItems(): access to firestore succesfully completed. Got: " + docs);
            });
            readTask.addOnFailureListener(err -> {
                ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "readAllDataItems(): failed to access firestore. Got error: " + err, Toast.LENGTH_LONG).show());
            });
            Log.i(logger, "readAllDataItems(): created task object for accessing firebase: " + readTask);

            ArrayList<DataItem> items = new ArrayList<>();

            try {
                Tasks.await(readTask);
                QuerySnapshot readResult = readTask.getResult();
                if (readResult != null) {
                    for (QueryDocumentSnapshot document : readResult) {
                        items.add(createItemFromFirebaseDocument(document));
                    }
                }

                Log.i(logger, "readAllDataItems(): read items from firestore: " + items);

            } catch (Exception e) {
                String errmsg = "readAllDataItems(): got exception trying to access firestore: " + e;
                Log.e(logger, errmsg, e);
                ((Activity) context).runOnUiThread(() -> Toast.makeText(context, errmsg, Toast.LENGTH_LONG).show());
            }

            return items;
        }
        catch (Exception e) {
            Log.e(logger,"readAllDataItems(): got exception: " + e,e);
            return new ArrayList<>();
        }
    }

    @Override
    public DataItem readDataItem(long dateItemId) {
        ((Activity)context).runOnUiThread(() -> Toast.makeText(this.context,"readDataItem() operation is not supported by this implementation",Toast.LENGTH_LONG).show());
        return null;
    }

    @Override
    public DataItem updateDataItem(DataItem item) {
        ((Activity)context).runOnUiThread(() -> Toast.makeText(this.context,"updateDataItem() operation is not supported by this implementation",Toast.LENGTH_LONG).show());
        return item;
    }

    @Override
    public boolean deleteDataItem(long dataItemId) {
        ((Activity)context).runOnUiThread(() -> Toast.makeText(this.context,"deleteDataItem() operation is not supported by this implementation",Toast.LENGTH_LONG).show());
        return false;
    }

    @Override
    public void setContext(Context context) {

        if (!(context instanceof Activity)) {
            Toast.makeText(FirebaseDataItemCRUDOperationsImpl.this.context, "Cannot instantiate firebase access. Context is not an Activity: " + context,
                    Toast.LENGTH_LONG).show();
            return;
        }

        this.context = context;
    }

    @Override
    public void finalise() {

    }

    @Override
    public void initialise(Runnable oninitialised) {
        Toast.makeText(FirebaseDataItemCRUDOperationsImpl.this.context, "Initialise firebase authentication...",Toast.LENGTH_LONG).show();

        this.firebaseAuth = FirebaseAuth.getInstance();

        Log.i(logger,"initialise(): now trying to authenticate user...");

        firebaseAuth.signInWithEmailAndPassword(myemail, mypwd)
                .addOnCompleteListener((Activity)this.context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(logger, "initialise(): signInWithEmailAndPassword(): success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(FirebaseDataItemCRUDOperationsImpl.this.context, "Authentication successful. Got user: " + user,
                                    Toast.LENGTH_LONG).show();
                            // we initialise the local access to firestore
                            firebaseFirestore = FirebaseFirestore.getInstance();
                            Log.i(logger, "initialise(): instantiated firestore: " + firebaseFirestore);
                            oninitialised.run();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(logger, "initialise(): signInWithEmailAndPassword(): failure", task.getException());
                            Toast.makeText(FirebaseDataItemCRUDOperationsImpl.this.context, "Authentication failed. Got: " + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(result -> {
                    Toast.makeText(FirebaseDataItemCRUDOperationsImpl.this.context, "Authentication failed. Got: " + result + " with " + result.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    // the following methods convert between data item objects and the map representation expected by firebase

    public DataItem createItemFromFirebaseDocument(QueryDocumentSnapshot fbdoc) {
        FirebaseDataItem item = new FirebaseDataItem();

        item.setName((String)fbdoc.getData().get("name"));
        item.setDescription((String)fbdoc.getData().get("description"));

        item.setFirebaseId(fbdoc.getId());

        return item;
    }

    public Map<String,Object> createFirebaseMapFromItem(DataItem item) {
        Map fbmap = new HashMap();
        fbmap.put("name",item.getName());
        if (item.getDescription() != null) {
            fbmap.put("description",item.getDescription());
        }
        // we need to add the current user as owner
        fbmap.put("owner",firebaseAuth.getCurrentUser().getUid());

        return fbmap;
    }

}
