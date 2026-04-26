package com.mrghost077.revenantrx.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.mrghost077.revenantrx.R;
import com.mrghost077.revenantrx.utils.TokenManager;
import com.mrghost077.revenantrx.network.ApiClient;
import com.mrghost077.revenantrx.network.ApiServices;
import com.mrghost077.revenantrx.models.LoginRequest;
import com.mrghost077.revenantrx.models.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView signUpText;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Check if user is already logged in before showing the UI
        tokenManager = new TokenManager(this);
        if (tokenManager.getToken() != null) {
            navigateToMain();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // 2. Initialize UI Components
        initViews();
        setupSignUpLink();

        // 3. Set Login Action
        loginButton.setOnClickListener(v -> handleLogin());
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.loginProgress);
        signUpText = findViewById(R.id.signUpText);
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Basic Validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // UI Feedback: Show progress, hide button
        setLoadingState(true);

        // 4. API Networking Logic
        ApiServices apiService = ApiClient.getClient().create(ApiServices.class);
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                setLoadingState(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Success: Save the JWT and move to Dashboard
                    tokenManager.saveToken(response.body().getToken());
                    Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    // Error: 401 Unauthorized or 404
                    Toast.makeText(LoginActivity.this, "Invalid credentials. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                setLoadingState(false);
                // System level failure (No internet or Server Offline)
                Toast.makeText(LoginActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        loginButton.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        loginButton.setEnabled(!isLoading);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Removes Login from the backstack
    }

    private void setupSignUpLink() {
        String fullText = "Don't have an account? Sign Up";
        String linkText = "Sign Up";
        SpannableString spannableString = new SpannableString(fullText);

        int startIndex = fullText.indexOf(linkText);
        int endIndex = startIndex + linkText.length();

        // Style: Blue Color
        spannableString.setSpan(
                new ForegroundColorSpan(Color.parseColor("#3B82F6")),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Style: Bold
        spannableString.setSpan(
                new StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Interaction: Optional click listener for the Sign Up link
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        signUpText.setText(spannableString);
        signUpText.setMovementMethod(LinkMovementMethod.getInstance());
        signUpText.setHighlightColor(android.graphics.Color.TRANSPARENT);
    }
}