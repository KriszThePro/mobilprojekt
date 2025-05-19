package hu.szollosikrisztian.mobilprojekt.controllers;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import hu.szollosikrisztian.mobilprojekt.interfaces.ISimpleCallback;
import hu.szollosikrisztian.mobilprojekt.models.UserModel;
import hu.szollosikrisztian.mobilprojekt.repositories.UserRepository;

public class UserController {

    private final FirebaseFirestore firestore;
    private final UserRepository userRepository;
    private final AuthController auth;

    public UserController() {
        this.firestore = FirebaseFirestore.getInstance();
        this.userRepository = new UserRepository();
        this.auth = new AuthController();
    }

    public void storeUserData(UserModel user, ISimpleCallback listener) {
        this.userRepository.storeUserData(this.auth.getAuth(), user, listener);
    }

    public void getUsername(ISimpleCallback listener) {
        this.userRepository.getUsername(auth.getAuth(), listener);
    }

    public CollectionReference usersCollectionReference() {
        return this.firestore.collection("users");
    }

    public DocumentReference currentUserDetails() {
        return this.usersCollectionReference().document(this.auth.getCurrentUserId());
    }

    public void getUserById(String userId, ISimpleCallback listener) {
        this.userRepository.getUserById(userId, this.firestore, listener);
    }
}
