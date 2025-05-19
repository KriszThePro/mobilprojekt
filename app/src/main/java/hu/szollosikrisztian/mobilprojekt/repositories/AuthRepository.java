package hu.szollosikrisztian.mobilprojekt.repositories;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import hu.szollosikrisztian.mobilprojekt.controllers.UserController;
import hu.szollosikrisztian.mobilprojekt.interfaces.IAuthRepository;
import hu.szollosikrisztian.mobilprojekt.interfaces.ISimpleCallback;
import hu.szollosikrisztian.mobilprojekt.models.UserModel;
import hu.szollosikrisztian.mobilprojekt.utils.LogUtil;

public class AuthRepository implements IAuthRepository {

    private final FirebaseAuth auth;

    public AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
    }

    @Override
    public void registerUser(String email, String username, String password, ISimpleCallback listener) {
        this.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // save user data
                        UserController userController = new UserController();
                        UserModel user = new UserModel(username);
                        userController.storeUserData(user, new ISimpleCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                LogUtil.d(AuthRepository.this, "User data stored successfully");
                                if (listener != null) {
                                    listener.onSuccess(null);
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                LogUtil.e(AuthRepository.this, "Failed to store user data: " + e.getMessage(), e);
                                if (listener != null) {
                                    listener.onSuccess(null);
                                }
                            }
                        });

                        LogUtil.d(AuthRepository.this, "Registration successful");
                        return;
                    }

                    LogUtil.e(AuthRepository.this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), task.getException());
                    if (listener != null) {
                        listener.onFailure(Objects.requireNonNull(task.getException()));
                    }
                });
    }

    @Override
    public void loginUser(String email, String password, ISimpleCallback listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        LogUtil.d(AuthRepository.this, "Login successful");
                        if (listener != null) {
                            listener.onSuccess(null);
                        }

                        return;
                    }

                    LogUtil.e(AuthRepository.this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), task.getException());
                    if (listener != null) {
                        listener.onFailure(Objects.requireNonNull(task.getException()));
                    }
                });
    }

    @Override
    public void logoutUser(ISimpleCallback listener) {
        if (listener != null) {
            listener.onSuccess(null);
        }

        auth.signOut();
        LogUtil.d(this, "Logout successful");
    }

    @Override
    public void resetUserPassword(String email, ISimpleCallback listener) {

    }

    @Override
    public FirebaseAuth getAuth() {
        return auth;
    }

    @Override
    public String getCurrentUserId() {
        return auth.getUid();
    }

    @Override
    public boolean isLoggedIn() {
        return getCurrentUserId() != null;
    }
}
