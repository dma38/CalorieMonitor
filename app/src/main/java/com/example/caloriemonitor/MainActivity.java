package com.example.caloriemonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnBMI;
    Button btnAddItem;
    Button btnHistory;
    Button btnReviewReport;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("userInfoPrefs", this.MODE_PRIVATE);
        btnBMI = (Button)findViewById(R.id.btnBMI);
        btnAddItem = (Button)findViewById(R.id.btnAddItem);
        btnHistory = (Button)findViewById(R.id.btnHistory);
        btnReviewReport = (Button)findViewById(R.id.btnReviewReport);

        btnBMI.setVisibility(View.INVISIBLE);
        btnBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BMIActivity.class);
                startActivity(i);
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(i);
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MealHistoryActivity.class);
                startActivity(i);
            }
        });

        if(sharedPreferences.getLong("plan", -1) == -1)
        {
            //Toast.makeText(MainActivity.this, String.valueOf(sharedPreferences.getLong("plan", -1)) ,Toast.LENGTH_SHORT).show();
            btnBMI.setVisibility(View.VISIBLE);
            btnReviewReport.setVisibility(View.INVISIBLE);
            btnAddItem.setVisibility(View.INVISIBLE);
            btnHistory.setVisibility(View.INVISIBLE);
        }
        else
        {
            btnReviewReport.setVisibility(View.VISIBLE);
            btnBMI.setVisibility(View.INVISIBLE);
            btnAddItem.setVisibility(View.VISIBLE);
            btnHistory.setVisibility(View.VISIBLE);
        }

        btnReviewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, BMIReportActivity.class);
                int age = sharedPreferences.getInt("age",-1);
                String gender = sharedPreferences.getString("gender","");
                float weight = sharedPreferences.getFloat("weight", -1);
                float height = sharedPreferences.getFloat("height", -1);
                String exercise = sharedPreferences.getString("exercise_level","");

                //Toast.makeText(MainActivity.this, age + " " + gender + " " + weight + " " + height+ " "+ exercise, Toast.LENGTH_LONG).show();
                intent.putExtra("age",age);
                intent.putExtra("gender", gender);
                intent.putExtra("weight", weight);
                intent.putExtra("height", height);
                intent.putExtra("exercise_level", exercise);

                startActivity(intent);
            }
        });
    }
}
