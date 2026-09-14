package com.shofiqul.test_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText edbuy, edsell;
    private Button hisabbutton;
    private TextView textdisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edbuy = findViewById(R.id.edbuy);
        edsell = findViewById(R.id.edsell);
        hisabbutton = findViewById(R.id.hisabbutton);
        textdisplay = findViewById(R.id.textdisplay);

        hisabbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sBuy = edbuy.getText().toString().trim();
                String sSell = edsell.getText().toString().trim();

                if (sBuy.isEmpty()) {
                    edbuy.setError("ক্রয়মূল্য লিখুন");
                    edbuy.requestFocus();
                    return;
                }

                if (sSell.isEmpty()) {
                    edsell.setError("বিক্রয়মূল্য লিখুন");
                    edsell.requestFocus();
                    return;
                }

                try {
                    float buy = Float.parseFloat(sBuy);
                    float sell = Float.parseFloat(sSell);

                    if (buy <= 0) {
                        edbuy.setError("ক্রয়মূল্য ০ এর চেয়ে বেশি হতে হবে");
                        edbuy.requestFocus();
                        return;
                    }

                    float diff = sell - buy;
                    float marginPercent = (diff / buy) * 100;

                    if (diff > 0) {
                        // Profit
                        textdisplay.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.profit_green));
                        String result = String.format(Locale.getDefault(),
                                "🎉 লাভ (Profit): ৳ %.2f\nমার্জিন: +%.2f%%", diff, marginPercent);
                        textdisplay.setText(result);
                    } else if (diff < 0) {
                        // Loss
                        textdisplay.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.loss_red));
                        String result = String.format(Locale.getDefault(),
                                "⚠️ ক্ষতি (Loss): ৳ %.2f\nমার্জিন: %.2f%%", Math.abs(diff), marginPercent);
                        textdisplay.setText(result);
                    } else {
                        // Break even
                        textdisplay.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_primary));
                        textdisplay.setText("সমান সমান (No Profit, No Loss)\nলাভ বা ক্ষতি ০.০০%");
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "সঠিক সংখ্যা ইনপুট দিন", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}