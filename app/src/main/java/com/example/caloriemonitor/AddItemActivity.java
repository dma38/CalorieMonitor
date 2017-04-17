package com.example.caloriemonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Integer.parseInt;

public class AddItemActivity extends AppCompatActivity {

    Button btnCalorieHelper;
    Button btnFoodItemAdd;
    EditText etFoodItemName;
    EditText etFoodItemCalorie;
    EditText etFoodItemQuantity;
    Spinner spinnerMealType;
    EditText etFoodItemNotes;
    int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        etFoodItemName = (EditText)findViewById(R.id.etFoodItemName);
        etFoodItemCalorie = (EditText)findViewById(R.id.etFoodItemCalorie);
        etFoodItemQuantity = (EditText)findViewById(R.id.etFoodItemQuantity);
        btnFoodItemAdd = (Button)findViewById(R.id.btnFoodItemAdd);
        btnCalorieHelper = (Button)findViewById(R.id.btnCalorieHelper);

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();


        rootRef.child("mealType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> mealTypes = new ArrayList<String>();

                for (DataSnapshot mealTypeDatasapshot: dataSnapshot.getChildren()) {
                    String mealTypeName = mealTypeDatasapshot.getValue(String.class);
                    mealTypes.add(mealTypeName);
                }

                spinnerMealType = (Spinner) findViewById(R.id.spinnerMealType);
                ArrayAdapter<String> mealTypeAdapter = new ArrayAdapter<String>(AddItemActivity.this, android.R.layout.simple_spinner_item, mealTypes);
                mealTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMealType.setAdapter(mealTypeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        btnFoodItemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(etFoodItemName.getText().toString().matches("") || etFoodItemCalorie.getText().toString().matches("") || etFoodItemQuantity.getText().toString().matches("")) {
                    Toast.makeText(AddItemActivity.this, "Please fill all fields with *", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String foodItemName = etFoodItemName.getText().toString();
                    Float foodItemCalories = Float.parseFloat(etFoodItemCalorie.getText().toString());
                    int foodItemQuantity = parseInt(etFoodItemQuantity.getText().toString());

                    DatabaseReference mealItemRef = rootRef.child("mealItems");
                    String mealItemKey = mealItemRef.push().getKey();
                    DatabaseReference currentMealItem = mealItemRef.child(mealItemKey);
                    currentMealItem.child("calories").setValue(foodItemCalories);
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String datetime = df.format(Calendar.getInstance().getTime());
                    currentMealItem.child("datetime").setValue(datetime);
                    String dayMealId = df.format(Calendar.getInstance().getTime());
                    currentMealItem.child("dayMealId").setValue(dayMealId);

                    int mealTypeId = spinnerMealType.getSelectedItemPosition() + 1;
                    currentMealItem.child("mealTypeId").setValue(mealTypeId);

                    currentMealItem.child("description").setValue(foodItemName);
                    currentMealItem.child("quantity").setValue(foodItemQuantity);


                    //add day meals
                    rootRef.child("dayMeals").child(dayMealId).child(mealItemKey).setValue(true);


                    rootRef.child("dayMeals").child(dayMealId).child("sDate").setValue(datetime);

                    Toast.makeText(AddItemActivity.this, "The record is added", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(AddItemActivity.this, DetailActivity.class);
                    i.putExtra("dayMealId",dayMealId);
                    startActivity(i);
                }

            }
        });

        btnCalorieHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddItemActivity.this,CalorieHelperActivity.class);
                String keyword = etFoodItemName.getText().toString();
                i.putExtra("keyword",keyword);
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK)
        {
            String name = data.getStringExtra("name");
            String calories = data.getStringExtra("calories");
           // Toast.makeText(AddItemActivity.this, name + ", " + calories,Toast.LENGTH_SHORT).show();
            etFoodItemName.setText(name);
            etFoodItemCalorie.setText(calories);

        }
        else
        {
            Toast.makeText(AddItemActivity.this,"Result Cancelled", Toast.LENGTH_LONG).show();
        }
    }
}
