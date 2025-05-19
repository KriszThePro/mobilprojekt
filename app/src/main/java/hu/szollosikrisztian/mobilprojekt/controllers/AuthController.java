package hu.szollosikrisztian.mobilprojekt.controllers;

import com.google.firebase.auth.FirebaseAuth;

import hu.szollosikrisztian.mobilprojekt.interfaces.ISimpleCallback;
import hu.szollosikrisztian.mobilprojekt.repositories.AuthRepository;
import hu.szollosikrisztian.mobilprojekt.interfaces.IAuthRepository;

public final class AuthController implements IAuthRepository {

    private final AuthRepository authRepository;

    public AuthController() {
        this.authRepository = new AuthRepository();
    }

    public FirebaseAuth getAuth() {
        return this.authRepository.getAuth();
    }

    @Override
    public String getCurrentUserId() {
        return this.authRepository.getCurrentUserId();
    }

    @Override
    public boolean isLoggedIn() {
        return this.authRepository.isLoggedIn();
    }

    @Override
    public void registerUser(String email, String username, String password, ISimpleCallback listener) {
        this.authRepository.registerUser(email, username, password, listener);
    }

    @Override
    public void loginUser(String email, String password, ISimpleCallback listener) {
        this.authRepository.loginUser(email, password, listener);
    }

    public void logoutUser(ISimpleCallback listener) {
        authRepository.logoutUser(listener);
    }

    @Override
    public void resetUserPassword(String email, ISimpleCallback listener) {

    }
}
