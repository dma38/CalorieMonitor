package com.example.caloriemonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemDetailActivity extends AppCompatActivity {

    TextView txtItemDetailName;
    TextView txtItemDetailCalories;
    TextView txtItemDetailQuantity;
    TextView txtItemDetailMealType;
    Button btnItemDetailDelete;
    String dayMealId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mealItemIdRef;

        txtItemDetailName = (TextView)findViewById(R.id.txtItemDetailName);
        txtItemDetailCalories = (TextView)findViewById(R.id.txtItemDetailCalories);
        txtItemDetailQuantity = (TextView)findViewById(R.id.txtItemDetailQuantity);
        txtItemDetailMealType = (TextView)findViewById(R.id.txtItemDetailMealType);
        btnItemDetailDelete = (Button)findViewById(R.id.btnItemDetailDelete);

        Intent intent = getIntent();
     //Toast.makeText(this, intent.getStringExtra("mealItemId"),Toast.LENGTH_LONG).show();

        final String mealItemId = intent.getStringExtra("mealItemId");

        //mealItemIdRef = rootRef.child("mealItems").child(mealItemId);

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("mealItems").child(mealItemId).child("description").getValue()!= null  && dataSnapshot.child("mealItems").child(mealItemId).child("calories").getValue()!= null && dataSnapshot.child("mealItems").child(mealItemId).child("quantity").getValue()!= null&& dataSnapshot.child("mealItems").child(mealItemId).child("mealTypeId").getValue()!= null)
                {
                    //get the student's name and program value
                    txtItemDetailName.setText(dataSnapshot.child("mealItems").child(mealItemId).child("description").getValue().toString());
                    txtItemDetailCalories.setText(String.valueOf(Math.round(Float.parseFloat(dataSnapshot.child("mealItems").child(mealItemId).child("calories").getValue().toString()))));
                    txtItemDetailQuantity.setText(dataSnapshot.child("mealItems").child(mealItemId).child("quantity").getValue().toString());

                    String mealTypeId = dataSnapshot.child("mealItems").child(mealItemId).child("mealTypeId").getValue().toString();
                    String mealType = dataSnapshot.child("mealType").child(mealTypeId).getValue().toString();

                    txtItemDetailMealType.setText(mealType);

                    dayMealId = dataSnapshot.child("mealItems").child(mealItemId).child("dayMealId").getValue().toString();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnItemDetailDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootRef.child("mealItems").child(mealItemId).removeValue();
                Toast.makeText(ItemDetailActivity.this, "Deleted Successfully.",Toast.LENGTH_LONG).show();

                Intent i = new Intent(ItemDetailActivity.this, DetailActivity.class);

                i.putExtra("dayMealId",dayMealId);
                startActivity(i);
            }
        });
    }
}
