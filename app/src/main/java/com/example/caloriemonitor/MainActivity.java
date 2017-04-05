package com.example.caloriemonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button btnBMI;
    Button btnAddItem;
    Button btnHistory;
    Button btnReviewReport;
    Button btnRSS;
    TextView txtMain1;
    TextView txtMain2;
    SharedPreferences sharedPreferences;

    @Override
    protected void onResume() {
        super.onResume();
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("userInfoPrefs", this.MODE_PRIVATE);
        btnBMI = (Button) findViewById(R.id.btnBMI);
        btnAddItem = (Button) findViewById(R.id.btnAddItem);
        btnHistory = (Button) findViewById(R.id.btnHistory);
        btnReviewReport = (Button) findViewById(R.id.btnReviewReport);
        btnRSS = (Button) findViewById(R.id.btnRSS);

        txtMain1 = (TextView) findViewById(R.id.txtMain1);
        txtMain2 = (TextView) findViewById(R.id.txtMain2);

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


        btnReviewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, BMIReportActivity.class);
                int age = sharedPreferences.getInt("age", -1);
                String gender = sharedPreferences.getString("gender", "");
                float weight = sharedPreferences.getFloat("weight", -1);
                float height = sharedPreferences.getFloat("height", -1);
                String exercise = sharedPreferences.getString("exercise_level", "");

                //Toast.makeText(MainActivity.this, age + " " + gender + " " + weight + " " + height+ " "+ exercise, Toast.LENGTH_LONG).show();
                intent.putExtra("age", age);
                intent.putExtra("gender", gender);
                intent.putExtra("weight", weight);
                intent.putExtra("height", height);
                intent.putExtra("exercise_level", exercise);

                startActivity(intent);
            }
        });

        btnRSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RSSActivity.class);
                startActivity(i);
            }
        });

        DateFormat dayMealIdFormat = new SimpleDateFormat("EEE, d MMM yyyy");
        final String dayMealId = dayMealIdFormat.format(Calendar.getInstance().getTime());

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        final float maxCalorie = sharedPreferences.getLong("plan", -1);

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float totalCalories = 0;
                for (DataSnapshot mealItemIterator : dataSnapshot.child("mealItems").getChildren()) {
                    if(mealItemIterator.child("datetime").getValue()!= null  && mealItemIterator.child("description").getValue()!= null && mealItemIterator.child("quantity").getValue()!= null && mealItemIterator.child("calories").getValue()!= null && mealItemIterator.child("mealTypeId").getValue()!= null && mealItemIterator.child("dayMealId").getValue()!= null) {

                        float calories = Float.parseFloat(mealItemIterator.child("calories").getValue().toString());
                        int quantity = Integer.parseInt(mealItemIterator.child("quantity").getValue().toString());
                        String dayMealIdFromFirebase = mealItemIterator.child("dayMealId").getValue().toString();
                        if (dayMealIdFromFirebase.matches(dayMealId)) {

                            totalCalories += calories * quantity;
                        }
                    }

                }

                txtMain1.setText("Your plan is: "+ String.valueOf(Math.round(maxCalorie)) + " per day. ");

                if(totalCalories <= maxCalorie)
                {
                    txtMain2.setText("You can still eat " + (maxCalorie-totalCalories) +" kcals today.");
                }
                else
                {
                    txtMain2.setText("You have eaten " + (totalCalories - maxCalorie) +" kcals more than the limit today.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
