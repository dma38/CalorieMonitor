package com.example.caloriemonitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MealHistoryActivity extends AppCompatActivity {


    private DayMealAdapter dayMealAdapter;
    private ArrayList<DayMeal> dayMealArrayList;
    private ArrayList<String> dayMealIdString;
    private ListView listView;
    Button btnBackToMain_History;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_history);


        listView = (ListView)findViewById(R.id.listView);
        btnBackToMain_History = (Button)findViewById(R.id.btnBackToMain_history);

        btnBackToMain_History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MealHistoryActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dayMealArrayList = new ArrayList<DayMeal>();
                dayMealIdString = new ArrayList<String>();

                for (DataSnapshot daymeal : dataSnapshot.child("dayMeals").getChildren()) {
                    float totalCalories =0;
                    for (DataSnapshot mealItemIterator : dataSnapshot.child("mealItems").getChildren()){
                        float calories = Float.parseFloat(mealItemIterator.child("calories").getValue().toString());
                        if(mealItemIterator.child("dayMealId").getValue() != null)
                        {
                            if(mealItemIterator.child("dayMealId").getValue().toString().matches(daymeal.getKey()))
                            {
                                totalCalories += calories;
                            }
                        }


                    }
                        dayMealArrayList.add(new DayMeal(daymeal.child("sDate").getValue().toString(), daymeal.child("notes").getValue().toString(),totalCalories));
                        dayMealIdString.add(daymeal.getKey());

                }

                dayMealAdapter = new DayMealAdapter(MealHistoryActivity.this, R.layout.list_item, dayMealArrayList);

                listView.setAdapter(dayMealAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(MealHistoryActivity.this, DetailActivity.class);

                        intent.putExtra("dayMealId", dayMealIdString.get(i));
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public class DayMeal {
        private String sDate;
        private String notes;
        private float totalCalories;

        public DayMeal(String sDate, String notes, float totalCalories) {
            this.sDate = sDate;
            this.notes = notes;
            this.totalCalories = totalCalories;
        }

        public String getNotes() {
            return notes;
        }

        public String getsDate() {
            return sDate;
        }

        public float getTotalCalories() {
            return totalCalories;
        }

    }
    private class DayMealAdapter extends ArrayAdapter<DayMeal> {

        private ArrayList<DayMeal> items;

        public DayMealAdapter(Context context, int textViewResourceId, ArrayList<DayMeal> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            DayMeal o = items.get(position);

            if (o != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                if (tt != null) {
                    tt.setText(o.getsDate());
                }
                if (bt != null) {
                    bt.setText(String.valueOf(o.getTotalCalories()));
                }
            }
            return v;
        }
    }
}