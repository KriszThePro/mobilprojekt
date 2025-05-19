package hu.szollosikrisztian.mobilprojekt.utils;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class FirestoreLiveData<T> extends LiveData<List<T>> {

    private final Query query;
    private final Class<T> modelClass;
    private ListenerRegistration registration;

    public FirestoreLiveData(Query query, Class<T> modelClass) {
        this.query = query;
        this.modelClass = modelClass;
    }

    @Override
    protected void onActive() {
        registration = query.addSnapshotListener((snapshots, e) -> {
            if (e != null || snapshots == null) {
                setValue(null);
                return;
            }

            List<T> items = new ArrayList<>();
            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                T item = doc.toObject(modelClass);
                items.add(item);
            }
            setValue(items);
        });
    }

    @Override
    protected void onInactive() {
        if (registration != null) {
            registration.remove();
        }
    }
}

