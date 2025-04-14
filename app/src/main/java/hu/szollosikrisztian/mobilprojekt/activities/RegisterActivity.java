package hu.szollosikrisztian.mobilprojekt.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import hu.szollosikrisztian.mobilprojekt.R;
import hu.szollosikrisztian.mobilprojekt.util.IntentUtil;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmailInput;
    private EditText mUsernameInput;
    private EditText mPasswordInput;
    private EditText mConfirmPasswordInput;
    private Button mRegisterButton;
    private Button mLoginButton;
    private ProgressBar mProgressBar;

    private void initializeActivityComponents() {
        mEmailInput = findViewById(R.id.email_input);
        mUsernameInput = findViewById(R.id.username_input);
        mPasswordInput = findViewById(R.id.password_input);
        mConfirmPasswordInput = findViewById(R.id.password_again_input);
        mRegisterButton = findViewById(R.id.register_button);
        mLoginButton = findViewById(R.id.login_button);
        mProgressBar = findViewById(R.id.progress_bar);
    }

    private void navigateToLogin() {
        IntentUtil.navigate(this, LoginActivity.class);
    }

    private void navigateToChats() {
        IntentUtil.navigate(this, ChatsActivity.class);
    }

    private void startupAuthCheck() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;

        navigateToChats();
    }

    private void storeUsername(FirebaseAuth auth, String username) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("users");
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        users.child(userId).child("username").setValue(username);
    }

    private void handleRegister() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        mProgressBar.setVisibility(View.VISIBLE);
        String email = mEmailInput.getText().toString();
        String username = mUsernameInput.getText().toString();
        String password = mPasswordInput.getText().toString();
        String confirmPassword = mConfirmPasswordInput.getText().toString();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    mProgressBar.setVisibility(View.GONE);

                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    storeUsername(auth, username);
                    navigateToChats();
                });
    }

    private void setupRegisterButtonClickHandler() {
        mRegisterButton.setOnClickListener(v -> handleRegister());
    }

    private void setupLoginButtonClickHandler() {
        mLoginButton.setOnClickListener(v -> navigateToLogin());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        initializeActivityComponents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startupAuthCheck();

        setupRegisterButtonClickHandler();
        setupLoginButtonClickHandler();
    }
}