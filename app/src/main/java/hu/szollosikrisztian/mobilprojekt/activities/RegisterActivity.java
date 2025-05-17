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
import hu.szollosikrisztian.mobilprojekt.utils.IntentUtil;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInputField;
    private EditText usernameInputField;
    private EditText passwordInputField;
    private EditText confirmPasswordInputField;
    private Button registerButton;
    private Button loginButton;
    private ProgressBar progressBar;

    private void initializeActivityComponents() {
        this.emailInputField = findViewById(R.id.email_input);
        this.usernameInputField = findViewById(R.id.username_input);
        this.passwordInputField = findViewById(R.id.password_input);
        this.confirmPasswordInputField = findViewById(R.id.password_again_input);
        this.registerButton = findViewById(R.id.register_button);
        this.loginButton = findViewById(R.id.login_button);
        this.progressBar = findViewById(R.id.progress_bar);
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

        this.progressBar.setVisibility(View.VISIBLE);
        String email = this.emailInputField.getText().toString();
        String username = this.usernameInputField.getText().toString();
        String password = this.passwordInputField.getText().toString();
        String confirmPassword = this.confirmPasswordInputField.getText().toString();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            this.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            this.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    this.progressBar.setVisibility(View.GONE);

                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    storeUsername(auth, username);
                    navigateToChats();
                });
    }

    private void setupRegisterButtonClickHandler() {
        this.registerButton.setOnClickListener(v -> handleRegister());
    }

    private void setupLoginButtonClickHandler() {
        this.loginButton.setOnClickListener(v -> navigateToLogin());
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