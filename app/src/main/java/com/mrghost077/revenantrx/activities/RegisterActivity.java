package com.mrghost077.revenantrx.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.mrghost077.revenantrx.R;
import com.mrghost077.revenantrx.models.LoginResponse;
import com.mrghost077.revenantrx.models.RegisterRequest;
import com.mrghost077.revenantrx.network.ApiClient;
import com.mrghost077.revenantrx.network.ApiServices;
import com.mrghost077.revenantrx.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEdit, emailEdit, phoneEdit, passwordEdit;
    private AutoCompleteTextView roleDropdown;
    private MaterialButton registerButton;
    private TextView loginLinkText;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // --- 1. Password Visibility Toggle ---
        EditText passwordEditText = findViewById(R.id.regPasswordEditText);
        ImageView toggleBtn = findViewById(R.id.togglePasswordVisibility);

        toggleBtn.setOnClickListener(v -> {
            // Toggle between dots and visible text
            if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleBtn.setImageResource(R.drawable.ic_visibility);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleBtn.setImageResource(R.drawable.ic_visibility_off);
            }
            // Keep the cursor at the end of the text after switching
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // --- 2. Login Link Spannable ---
        TextView loginLink = findViewById(R.id.loginLinkText);
        String part1 = getString(R.string.link_already_have_account); // "Already have an account? "
        String part2 = getString(R.string.link_log_in);               // "Log In"
        String full = part1 + part2;

        SpannableString span = new SpannableString(full);
        int start = part1.length();

        // Style the "Log In" portion
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.link_blue)),
                start, full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(Typeface.BOLD),
                start, full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginLink.setText(span);

        // Close RegisterActivity and return to LoginActivity
        loginLink.setOnClickListener(v -> finish());



        tokenManager = new TokenManager(this);
        initViews();
        setupRoleDropdown();
        setupSpannableLink();

        registerButton.setOnClickListener(v -> handleRegistration());
    }

    private void initViews() {
        nameEdit = findViewById(R.id.regNameEditText);
        emailEdit = findViewById(R.id.regEmailEditText);
        phoneEdit = findViewById(R.id.regPhoneEditText);
        passwordEdit = findViewById(R.id.regPasswordEditText);
        roleDropdown = findViewById(R.id.roleDropdown);
        registerButton = findViewById(R.id.registerButton);
        loginLinkText = findViewById(R.id.loginLinkText);
    }

    private void setupRoleDropdown() {
        // Matches your Node.js Enum: ["patient", "staff"]
        String[] roles = {"patient", "staff"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, roles);
        roleDropdown.setAdapter(adapter);
        roleDropdown.setText(roles[0], false); // Default selection
    }

    private void setupSpannableLink() {
        SpannableString text = new SpannableString("Already have an account? Log In");
        // Color #0E9B7F matches your teal brand color
        text.setSpan(new ForegroundColorSpan(Color.parseColor("#0E9B7F")), 25, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new StyleSpan(Typeface.BOLD), 25, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginLinkText.setText(text);
        loginLinkText.setOnClickListener(v -> finish()); // Pops back to LoginActivity
    }

    private void handleRegistration() {
        String name = nameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String role = roleDropdown.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Please fill all fields (Password min 6 chars)", Toast.LENGTH_SHORT).show();
            return;
        }

        registerButton.setEnabled(false);
        registerButton.setText("Signing up...");

        ApiServices apiService = ApiClient.getClient().create(ApiServices.class);
        RegisterRequest request = new RegisterRequest(name, email, password, phone, role);

        apiService.registerUser(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                registerButton.setEnabled(true);
                registerButton.setText("Sign Up");

                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body().getToken());

                    // Navigate to Main and clear the login/signup stack
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                registerButton.setEnabled(true);
                registerButton.setText("Sign Up");
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
