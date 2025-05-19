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

import hu.szollosikrisztian.mobilprojekt.R;
import hu.szollosikrisztian.mobilprojekt.controllers.AuthController;
import hu.szollosikrisztian.mobilprojekt.interfaces.ISimpleCallback;
import hu.szollosikrisztian.mobilprojekt.utils.IntentUtil;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInputField;
    private EditText usernameInputField;
    private EditText passwordInputField;
    private EditText confirmPasswordInputField;
    private Button registerButton;
    private Button loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        initializeActivityComponents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startupAuthCheck();

        setupRegisterButtonClickHandler();
        setupLoginButtonClickHandler();
    }

    private void initializeActivityComponents() {
        this.emailInputField = findViewById(R.id.emailInput);
        this.usernameInputField = findViewById(R.id.username_input);
        this.passwordInputField = findViewById(R.id.passwordInput);
        this.confirmPasswordInputField = findViewById(R.id.passwordAgainInput);
        this.registerButton = findViewById(R.id.registerButton);
        this.loginButton = findViewById(R.id.loginButton);
        this.progressBar = findViewById(R.id.progressBar);
    }

    private void startupAuthCheck() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;

        IntentUtil.navigate(this, ChatActivity.class);
    }

    private void handleRegister() {
        String email = this.emailInputField.getText().toString();
        String username = this.usernameInputField.getText().toString();
        String password = this.passwordInputField.getText().toString();
        String confirmPassword = this.confirmPasswordInputField.getText().toString();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        this.progressBar.setVisibility(View.VISIBLE);

        AuthController authController = new AuthController();
        authController.registerUser(email, username, password, new ISimpleCallback() {
            @Override
            public void onSuccess(Object result) {
                progressBar.setVisibility(View.GONE);
                IntentUtil.navigate(RegisterActivity.this, ChatActivity.class);
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupRegisterButtonClickHandler() {
        this.registerButton.setOnClickListener(v -> handleRegister());
    }

    private void setupLoginButtonClickHandler() {
        this.loginButton.setOnClickListener(v -> IntentUtil.navigate(this, LoginActivity.class));
    }
}