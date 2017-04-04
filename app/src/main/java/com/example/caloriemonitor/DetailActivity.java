package com.example.caloriemonitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    TextView txtDetailName;
    TextView txtDetailProgram;
    Button btnDetailBack;
    ListView listViewDetailItem;
    Button btnMoreDetails;
    ProgressBar progress;
    TextView txtLeft;
    SharedPreferences sharedPreferences;

    float maxCalorie;
    private MealItemAdapter mealItemAdapter;
    private ArrayList<MealItem> mealItems;
    private ArrayList<String> mealItemId;

    TextView txtTotalCalories;
    String dayMealId;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();



    DatabaseReference dayMealIdRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        sharedPreferences = this.getSharedPreferences("userInfoPrefs", this.MODE_PRIVATE);
        maxCalorie = sharedPreferences.getLong("plan",-1);
        txtDetailName = (TextView)findViewById(R.id.txtDetailTitle);
        txtDetailProgram = (TextView)findViewById(R.id.txtDetailNotes);
        btnDetailBack = (Button)findViewById(R.id.btnDetailBack);
        btnMoreDetails = (Button)findViewById(R.id.btnMoreDetails);



        txtLeft = (TextView)findViewById(R.id.txtLeft);

        listViewDetailItem = (ListView)findViewById(R.id.listViewDetailItem);

        txtTotalCalories = (TextView)findViewById(R.id.txtTotalCalories);
        progress = (ProgressBar)findViewById(R.id.progressBar2);

        Intent intent = getIntent();
//      Toast.makeText(this, intent.getStringExtra("studentId"),Toast.LENGTH_LONG).show();

         dayMealId = intent.getStringExtra("dayMealId");


        dayMealIdRef = rootRef.child("dayMeals").child(dayMealId);


        dayMealIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("sDate").getValue()!= null  && dataSnapshot.child("notes").getValue()!= null)
                {

                    txtDetailName.setText("What You Ate on  " + dataSnapshot.child("sDate").getValue().toString() + "\n               & Total Calories Summary");
                    txtDetailProgram.setText(dataSnapshot.child("notes").getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        btnDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, MealHistoryActivity.class);
                startActivity(i);
            }
        });
    }

    public class MealItem {
        private String datetime;
        private String description;
        private int quantity;
        private float calories;
        private String mealType;

        public MealItem(String datetime, String description, int quantity, float calories, String mealType) {
            this.datetime = datetime;
            this.description = description;
            this.quantity = quantity;
            this.calories = calories;
            this.mealType = mealType;
        }

        public String getDatetime() {
            return datetime;
        }

        public String getDescription() {
            return description;
        }

        public int getQuantity() {
            return quantity;
        }

        public float getCalories() {
            return calories;
        }

        public String getMealType() {

            return mealType;
        }

    }
    private class MealItemAdapter extends ArrayAdapter<MealItem> {

        private ArrayList<MealItem> items;

        public MealItemAdapter(Context context, int textViewResourceId, ArrayList<MealItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_item_detail, null);
            }
            MealItem o = items.get(position);

            if (o != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                TextView btr = (TextView) v.findViewById(R.id.bottonRightText);
                if (tt != null) {
                    tt.setText(o.getDescription());
                }
                if (bt != null) {
                    bt.setText(String.valueOf(o.getCalories()));
                }
                if (btr != null) {
                    btr.setText(String.valueOf(o.getMealType()));
                }
            }
            return v;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mealItems = new ArrayList<MealItem>();
                mealItemId = new ArrayList<String>();

                float totalCalories = 0;



                    for (DataSnapshot mealItemIterator : dataSnapshot.child("mealItems").getChildren()) {
                        if(mealItemIterator.child("datetime").getValue()!= null  && mealItemIterator.child("description").getValue()!= null && mealItemIterator.child("quantity").getValue()!= null && mealItemIterator.child("calories").getValue()!= null && mealItemIterator.child("mealTypeId").getValue()!= null && mealItemIterator.child("dayMealId").getValue()!= null) {
                            String datetime = mealItemIterator.child("datetime").getValue().toString();
                            String description = mealItemIterator.child("description").getValue().toString();
                            int quantity = Integer.parseInt(mealItemIterator.child("quantity").getValue().toString());
                            float calories = Float.parseFloat(mealItemIterator.child("calories").getValue().toString());
                            String mealTypeId = mealItemIterator.child("mealTypeId").getValue().toString();
                            String mealType = dataSnapshot.child("mealType").child(mealTypeId).getValue().toString();
                            String dayMealIdFromFirebase = mealItemIterator.child("dayMealId").getValue().toString();
                            //                      Toast.makeText(DetailActivity.this,("Key: "+ mealItemIterator.getKey() + "; Day meal Id " + dayMealId),Toast.LENGTH_LONG).show();
                            if (dayMealIdFromFirebase.matches(dayMealId)) {
                                mealItems.add(new MealItem(datetime, description, quantity, calories * quantity, mealType));
                                mealItemId.add(mealItemIterator.getKey());

                                totalCalories += calories * quantity;
                            }
                        }




                }

                mealItemAdapter = new MealItemAdapter(DetailActivity.this, R.layout.list_item, mealItems);

                listViewDetailItem.setAdapter(mealItemAdapter);

                listViewDetailItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(DetailActivity.this, ItemDetailActivity.class);
                        startActivity(intent);
                    }
                });



                txtTotalCalories.setText("Total calories: "+ String.valueOf(totalCalories) + " out of: " + Math.round(maxCalorie));
                progress.setMax(Math.round(maxCalorie));
                progress.setProgress(Math.round(totalCalories));

                if(totalCalories <= maxCalorie)
                {
                    txtLeft.setText("You can still eat " + (maxCalorie-totalCalories) +" kcals today.");
                }
                else
                {
                    txtLeft.setText("You have eaten " + (totalCalories - maxCalorie) +" kcals more than the limit today.");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
