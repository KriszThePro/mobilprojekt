package hu.szollosikrisztian.mobilprojekt.interfaces;

import com.google.firebase.auth.FirebaseAuth;

public interface IAuthRepository {

    void registerUser(String email, String username, String password, ISimpleCallback listener);

    void loginUser(String email, String password, ISimpleCallback listener);

    void logoutUser(ISimpleCallback listener);

    void resetUserPassword(String email, ISimpleCallback listener);

    FirebaseAuth getAuth();

    String getCurrentUserId();

    boolean isLoggedIn();
}
