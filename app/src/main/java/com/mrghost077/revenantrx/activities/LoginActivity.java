package com.mrghost077.revenantrx.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.mrghost077.revenantrx.R;

public class LoginActivity extends AppCompatActivity {

    private TextView signUpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Keeps the UI drawing behind the status bar for that iPhone look
        EdgeToEdge.enable(this);

        // 2. Load your custom UI
        setContentView(R.layout.activity_login);

        // 3. Initialize the Sign Up link styling
        signUpText = findViewById(R.id.signUpText);
        setupSignUpLink();
    }

    private void setupSignUpLink() {
        String fullText = "Don't have an account? Sign Up";
        String linkText = "Sign Up";
        SpannableString spannableString = new SpannableString(fullText);

        int startIndex = fullText.indexOf(linkText);
        int endIndex = startIndex + linkText.length();

        // Color it blue (#3B82F6)
        spannableString.setSpan(
                new ForegroundColorSpan(Color.parseColor("#3B82F6")),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Make it bold
        spannableString.setSpan(
                new StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        signUpText.setText(spannableString);
    }
}