package com.example.caloriemonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import static java.lang.Integer.parseInt;

public class BMIActivity extends AppCompatActivity {

    EditText etAge;
    EditText etWeight;
    TextView txtExplain;
    Button btnCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        txtExplain = (TextView)findViewById(R.id.txtExplain);
        btnCalculate = (Button)findViewById(R.id.btnCalculate);
        etAge = (EditText)findViewById(R.id.etAge);
        etWeight = (EditText)findViewById(R.id.etWeight);

        final Spinner spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        final Spinner spinnerHeight = (Spinner) findViewById(R.id.spinnerHeight);
        ArrayAdapter<CharSequence> heightAdapter = ArrayAdapter.createFromResource(this, R.array.height_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHeight.setAdapter(heightAdapter);

        final Spinner spinnerWeight = (Spinner) findViewById(R.id.spinnerWeight);
        ArrayAdapter<CharSequence> weightAdapter = ArrayAdapter.createFromResource(this, R.array.weight_array, android.R.layout.simple_spinner_item);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeight.setAdapter(weightAdapter);

        final Spinner spinnerExercise = (Spinner) findViewById(R.id.spinnerExercise);
        ArrayAdapter<CharSequence> exerciseAdapter = ArrayAdapter.createFromResource(this, R.array.exercise_array, android.R.layout.simple_spinner_item);
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExercise.setAdapter(exerciseAdapter);

        spinnerExercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                {
                    txtExplain.setText(getString(R.string.Sedentary_explain));
                }
                else if (position == 1)
                {
                    txtExplain.setText(getString(R.string.Lightly_active_explain));
                }
                else if (position == 2)
                {
                    txtExplain.setText(getString(R.string.Moderately_active_explain));
                }
                else if (position == 3)
                {
                    txtExplain.setText(getString(R.string.Very_active_explain));
                }
                else if (position == 4)
                {
                    txtExplain.setText(getString(R.string.Extra_active_explain));
                }
                else
                {
                    txtExplain.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float weight;
                int age = parseInt(etAge.getText().toString());
                String gender = spinnerGender.getSelectedItem().toString();
                int weight_raw = Integer.parseInt(etWeight.getText().toString());
                if(spinnerWeight.getSelectedItemPosition() == 0)
                {
                    weight = weight_raw;
                }
                else
                {
                    weight = weight_raw * 2.20462f;
                }

                int height;
                String[] height_strings = spinnerHeight.getSelectedItem().toString().split("/");
                String sHeight = height_strings[1].trim().substring(0,3);

                height = Integer.parseInt(sHeight);
                float inchHeight = height * 0.393701f;
                String exercise = spinnerExercise.getSelectedItem().toString();

                //Toast.makeText(BMIActivity.this,sHeight +" "+ exercise,Toast.LENGTH_LONG).show();

                Intent intent = new Intent(BMIActivity.this, BMIReportActivity.class);
                intent.putExtra("age",age);
                intent.putExtra("gender", gender);
                intent.putExtra("weight", weight);
                intent.putExtra("height", inchHeight);
                intent.putExtra("exercise_level", exercise);

                startActivity(intent);
            }
        });
    }
}
