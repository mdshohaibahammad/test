package com.shofiqul.test_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

   EditText edbuy,edsell;

   Button hisabbutton;

   TextView textdisplay;



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
                String s1,s2,s3;

                s1 = edbuy.getText().toString();
                s2 = edsell.getText().toString();

                float buy,sell,resule,result2;

                buy = Float.parseFloat(s1);
                sell = Float.parseFloat(s2);

                resule = sell- buy;
                result2 = resule/buy*100;

                s3 =  "yore profit margin is " + result2 + "%";
                textdisplay.setText(s3 + "%"+ "s");
            }
        });






    }
}