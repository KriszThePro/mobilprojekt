package hu.szollosikrisztian.mobilprojekt.activities;

import android.content.Intent;
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

import hu.szollosikrisztian.mobilprojekt.R;
import hu.szollosikrisztian.mobilprojekt.controllers.AuthController;
import hu.szollosikrisztian.mobilprojekt.interfaces.ISimpleCallback;
import hu.szollosikrisztian.mobilprojekt.utils.IntentUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInputField;
    private EditText passwordInputField;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        this.initializeActivityComponents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.setupLoginButtonClickHandler();
        this.setupRegisterButtonClickHandler();
    }

    private void initializeActivityComponents() {
        this.emailInputField = findViewById(R.id.emailInput);
        this.passwordInputField = findViewById(R.id.passwordInput);
        this.loginButton = findViewById(R.id.loginButton);
        this.registerButton = findViewById(R.id.registerButton);
        this.progressBar = findViewById(R.id.progressBar);
    }

    private void handleLogin() {
        String email = this.emailInputField.getText().toString();
        String password = this.passwordInputField.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        this.progressBar.setVisibility(View.VISIBLE);

        AuthController authController = new AuthController();
        authController.loginUser(email, password, new ISimpleCallback() {
            @Override
            public void onSuccess(Object result) {
                LoginActivity.this.progressBar.setVisibility(View.GONE);
                IntentUtil.navigate(LoginActivity.this, RegisterActivity.class);
            }

            @Override
            public void onFailure(Exception e) {
                LoginActivity.this.progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupLoginButtonClickHandler() {
        this.loginButton.setOnClickListener(v -> handleLogin());
    }

    private void setupRegisterButtonClickHandler() {
        this.registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}