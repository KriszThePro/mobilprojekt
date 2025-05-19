package hu.szollosikrisztian.mobilprojekt.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import hu.szollosikrisztian.mobilprojekt.interfaces.ISimpleCallback;
import hu.szollosikrisztian.mobilprojekt.interfaces.IUserRepository;
import hu.szollosikrisztian.mobilprojekt.models.UserModel;
import hu.szollosikrisztian.mobilprojekt.utils.LogUtil;

public final class UserRepository implements IUserRepository {

    @Override
    public void storeUserData(FirebaseAuth auth, UserModel user, ISimpleCallback listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        firestore.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    LogUtil.d(UserRepository.this, "User data stored successfully");
                    if (listener != null) {
                        listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    LogUtil.e(UserRepository.this, "Failed to store user data: " + e.getMessage(), e);
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    @Override
    public void getUsername(FirebaseAuth auth, ISimpleCallback listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        if (listener != null) {
                            listener.onSuccess(username);
                        }

                        return;
                    }

                    if (listener != null) {
                        listener.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    @Override
    public void getUserById(String userId, FirebaseFirestore firestore, ISimpleCallback listener) {
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        assert user != null;
                        if (listener != null) {
                            listener.onSuccess(user);
                        }

                        return;
                    }

                    if (listener != null) {
                        listener.onFailure(new Exception("User not found"));
                    }

                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }
}
