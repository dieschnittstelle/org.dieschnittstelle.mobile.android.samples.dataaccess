package org.dieschnittstelle.mobile.android.dataaccess.model.firebase;

import org.dieschnittstelle.mobile.android.dataaccess.model.DataItem;

/*
 * extend data item and add a new attribute for the firebase id
 */
public class FirebaseDataItem extends DataItem {

    private String firebaseId;

    public FirebaseDataItem() {

    }

    public FirebaseDataItem(DataItem item,String firebaseId) {
        this.setName(item.getName());
        this.setDescription(item.getDescription());
        this.setFirebaseId(firebaseId);
    }


    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String toString() {
        return "{FirebaseDataItem " + super.toString()  + "}";
    }
}
