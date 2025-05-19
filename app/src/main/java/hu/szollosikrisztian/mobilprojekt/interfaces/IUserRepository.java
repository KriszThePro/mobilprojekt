package hu.szollosikrisztian.mobilprojekt.interfaces;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import hu.szollosikrisztian.mobilprojekt.models.UserModel;

public interface IUserRepository {

    void storeUserData(FirebaseAuth auth, UserModel user, ISimpleCallback listener);

    void getUsername(FirebaseAuth auth, ISimpleCallback listener);

    void getUserById(String userId, FirebaseFirestore firestore, ISimpleCallback listener);
}
