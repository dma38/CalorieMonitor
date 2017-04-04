package com.example.caloriemonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BMIReportActivity extends AppCompatActivity {

    int age;
    String gender;
    double weight;
    double height;
    String exercise_level;
    TextView txtSummaryBMI;
    TextView txtReportBMI;
    double bmi;
    TextView txtSummaryBMR;
    TextView txtReportBMR;
    TextView txtSummaryExpenditure;
    TextView txtReportExpenditure;

    TextView txtMantainanceCalorie;
    TextView txtFatLossCalorie;
    TextView txtExtremeCalorie;
    TextView txtWeightGainCalorie;
    TextView etCustomizedCalorie;

    Button btnMaintenancePlan;
    Button btnFatLossPlan;
    Button btnExtremePlan;
    Button btnWeightGainPlan;
    Button btnCustomizedPlan;
    Button btnRetakeTest;
    Button btnBackToMain;
    Button btnRetakeTest2;
    Button btnBackToMain2;
    SharedPreferences sharedPreferences;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(sharedPreferences.getLong("plan", -1) == -1) {
            Toast.makeText(BMIReportActivity.this, "You haven't selected any plan.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmireport);

        sharedPreferences = this.getSharedPreferences("userInfoPrefs", this.MODE_PRIVATE);

        Intent i = getIntent();
        age = i.getIntExtra("age", -1);
        gender = i.getStringExtra("gender");
        weight = i.getFloatExtra("weight",-1);
        height = i.getFloatExtra("height",-1);
        exercise_level = i.getStringExtra("exercise_level");
        double bmr;

        txtReportBMI = (TextView)findViewById(R.id.txtReportBMI);
        //Toast.makeText(BMIReportActivity.this, age + ", " + gender+ ", " +weight + ", "+ height + ", "+exercise_level,Toast.LENGTH_LONG).show();

        txtSummaryBMI = (TextView)findViewById(R.id.txtSummaryBMI);
        bmi = (weight * 0.45)/((height * 0.025) * (height * 0.025));

        txtReportBMI.setText(String.format("%.2f", bmi));

        String sBMI = "Body Mass Index (BMI) is used as a screening tool to indicate whether a person is underweight, overweight, obese or a healthy weight for their height.\n\n";

        if(bmi <18.5)
        {
            sBMI += "Your BMI is under 18.5. You are underweight. Please adjust your calorie intake to gain some weight.";
        }
        else if(bmi >= 18.5 && bmi<25)
        {
            sBMI += "Your BMI is between 18.5 and 25. You are healthy. Please maintain the current calorie intake.";
        }
        else if(bmi >=25 && bmi <= 30)
        {
            sBMI += "Your BMI is between 25 and 30. You are overweight. Please adjust your calorie intake to lose some fat.";
        }
        else if(bmi > 30)
        {
            sBMI += "Your BMI is higher than 30. You are considered obese. Please adjust your calorie intake to lose some fat.";
        }
        else
        {
            sBMI = "";
        }

        txtSummaryBMI.setText(sBMI);

        txtSummaryBMR = (TextView)findViewById(R.id.txtSummaryBMR);

        txtReportBMR = (TextView)findViewById(R.id.txtReportBMR);
        String sBMR = "Your BMR (Basal Metabolic rate) is the amount of calories your body requires just to perform daily, life sustaining functions. It's the rate of your metabolism or the amount of calories your body burns at rest.\n\n";

        if(gender.matches("Male"))
        {
            bmr = (12.7 * height) + (6.23 * weight) - (6.8 * age) + 66;
        }
        else if (gender.matches("Female"))
        {
            bmr = (4.7 * height) + (4.35 * weight) - (4.7 * age) + 655;
        }
        else
        {
            bmr = 0;
        }

        sBMR += "Your BMR is " + String.format("%.2f", bmr) + ". This means your body need " + String.format("%.2f", bmr) + " kcal to function normally. ";

        txtSummaryBMR.setText(sBMR);

        txtReportBMR.setText(String.format("%.2f", bmr));


        txtSummaryExpenditure = (TextView)findViewById(R.id.txtSummaryExpenditure);

        txtReportExpenditure = (TextView)findViewById(R.id.txtReportExpenditure);

        double sedentaryExp = 1.2 * bmr;

        double lightlyExp = 1.375 * bmr;

        double moderatelyExp = 1.55 * bmr;

        double veryActiveExp = 1.725 * bmr;

        double extraActiveExp = 1.9 * bmr;



        double baseCalorie;

        String sExpenditure = "The calculation according to Harris Benedict Equation can give you an estimate of how many calories you burn each day. \n\n";
        if(exercise_level.matches("Sedentary"))
        {
            txtReportExpenditure.setText(String.format("%.2f", sedentaryExp));
            baseCalorie = sedentaryExp;
            sExpenditure += "Your activity level is Sedentary. You take little to no exercise. Your daily total caloric intake is "+ String.format("%.2f", sedentaryExp);
        }
        else if(exercise_level.matches("Lightly active"))
        {
            txtReportExpenditure.setText(String.format("%.2f", lightlyExp));
            baseCalorie = lightlyExp;
            sExpenditure += "Your activity level is Lightly active. You exercise about 1 to 3 days each week. Your daily total caloric intake is "+ String.format("%.2f", lightlyExp);

        }
        else if(exercise_level.matches("Moderately active"))
        {
            txtReportExpenditure.setText(String.format("%.2f", moderatelyExp));
            baseCalorie = moderatelyExp;
            sExpenditure += "Your activity level is Moderately active. You exercise moderately and/or play sports 3 to 5 days a week. Your daily total caloric intake is "+ String.format("%.2f", moderatelyExp);

        }
        else if(exercise_level.matches("Very active"))
        {
            txtReportExpenditure.setText(String.format("%.2f", veryActiveExp));
            baseCalorie = veryActiveExp;
            sExpenditure += "Your activity level is Very active. You are engaged in strenuous sports or hard exercise 6 to 7 days a week. Your daily total caloric intake is "+ String.format("%.2f", veryActiveExp);

        }
        else if(exercise_level.matches("Extra active"))
        {
            txtReportExpenditure.setText(String.format("%.2f", extraActiveExp));
            baseCalorie = extraActiveExp;
            sExpenditure += "Your activity level is Extra active. You are engaged in very physically challenging jobs or exercise, such as 2-a-day workouts. Your daily total caloric intake is "+ String.format("%.2f", extraActiveExp);

        }
        else
        {
            baseCalorie = 0;
        }

        txtSummaryExpenditure.setText(sExpenditure);


        txtMantainanceCalorie = (TextView)findViewById(R.id.txtMantenanceCalories);
        txtFatLossCalorie = (TextView)findViewById(R.id.txtFatLossCalories);
        txtExtremeCalorie = (TextView)findViewById(R.id.txtExtremeCalories);
        txtWeightGainCalorie = (TextView)findViewById(R.id.txtWeightGainCalories);


        final double maintenanceCalorie;
        final double fatLossCalorie;
        final double extremeCalorie;
        final double weightGainCalorie;

        maintenanceCalorie = baseCalorie;
        fatLossCalorie = baseCalorie - 438;
        extremeCalorie = baseCalorie - 703;
        weightGainCalorie = baseCalorie + 445;

        txtMantainanceCalorie.setText(String.valueOf(Math.round(maintenanceCalorie)));
        txtFatLossCalorie.setText(String.valueOf(Math.round(fatLossCalorie)));
        txtExtremeCalorie.setText(String.valueOf(Math.round(extremeCalorie)));
        txtWeightGainCalorie.setText(String.valueOf(Math.round(weightGainCalorie)));


        btnMaintenancePlan = (Button)findViewById(R.id.btnMantenancePlan);
        btnFatLossPlan = (Button)findViewById(R.id.btnFatLossPlan);
        btnExtremePlan = (Button)findViewById(R.id.btnExtremePlan);
        btnWeightGainPlan = (Button)findViewById(R.id.btnWeightGainPlan);
        btnCustomizedPlan = (Button)findViewById(R.id.btnCustomizedPlan);



        btnRetakeTest = (Button)findViewById(R.id.btnRetakeTest);
        btnRetakeTest2 = (Button)findViewById(R.id.btnRetakeTest2);


        View.OnClickListener e = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getLong("plan", -1) == -1) {
                    Toast.makeText(BMIReportActivity.this, "You haven't selected any plan.", Toast.LENGTH_LONG).show();
                }
                Intent i = new Intent(BMIReportActivity.this, BMIActivity.class);
                startActivity(i);
            }
        };
        btnRetakeTest.setOnClickListener(e);
        btnRetakeTest2.setOnClickListener(e);


        btnMaintenancePlan.setText("Set " + String.valueOf(Math.round(maintenanceCalorie)) +" kcal as my plan!");
        btnFatLossPlan.setText("Set " + String.valueOf(Math.round(fatLossCalorie)) +" kcal as my plan!");
        btnExtremePlan.setText("Set " + String.valueOf(Math.round(extremeCalorie)) +" kcal as my plan!");
        btnWeightGainPlan.setText("Set " + String.valueOf(Math.round(weightGainCalorie)) +" kcal as my plan!");

        btnMaintenancePlan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("age",age);
                editor.putString("gender",gender);
                editor.putFloat("weight", (float)weight);
                editor.putFloat("height", (float)height);
                editor.putString("exercise_level", exercise_level);
                editor.putLong("plan", Math.round(maintenanceCalorie));
                if(editor.commit())
                {
                    Toast.makeText(BMIReportActivity.this, "Maintenance Plan of " + String.valueOf(Math.round(maintenanceCalorie)) + " saved successfully!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(BMIReportActivity.this, "Now you can start recording calories for meals!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(BMIReportActivity.this, "Save Failed! Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnFatLossPlan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("age",age);
                editor.putString("gender",gender);
                editor.putFloat("weight", (float)weight);
                editor.putFloat("height", (float)height);
                editor.putString("exercise_level", exercise_level);
                editor.putLong("plan", Math.round(fatLossCalorie));
                if(editor.commit())
                {
                    Toast.makeText(BMIReportActivity.this, "Fat Loss Plan of " + String.valueOf(Math.round(fatLossCalorie)) + " saved successfully!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(BMIReportActivity.this, "Now you can start recording calories for meals!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(BMIReportActivity.this, "Save Failed! Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnExtremePlan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("age",age);
                editor.putString("gender",gender);
                editor.putFloat("weight", (float)weight);
                editor.putFloat("height", (float)height);
                editor.putString("exercise_level", exercise_level);
                editor.putLong("plan", Math.round(extremeCalorie));
                if(editor.commit())
                {
                    Toast.makeText(BMIReportActivity.this, "Extreme Fat Loss Plan of " + String.valueOf(Math.round(extremeCalorie)) + " saved successfully!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(BMIReportActivity.this, "Now you can start recording calories for meals!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(BMIReportActivity.this, "Save Failed! Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnWeightGainPlan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("age",age);
                editor.putString("gender",gender);
                editor.putFloat("weight", (float)weight);
                editor.putFloat("height", (float)height);
                editor.putString("exercise_level", exercise_level);
                editor.putLong("plan", Math.round(weightGainCalorie));
                if(editor.commit())
                {
                    Toast.makeText(BMIReportActivity.this, "Weight Gain Plan of " + String.valueOf(Math.round(weightGainCalorie)) + " kcal saved successfully!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(BMIReportActivity.this, "Now you can start recording calories for meals!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(BMIReportActivity.this, "Save Failed! Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBackToMain = (Button)findViewById(R.id.btnBackToMainReport);
        btnBackToMain2 = (Button)findViewById(R.id.btnBackToMain2);

        View.OnClickListener e2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getLong("plan", -1) == -1) {
                    Toast.makeText(BMIReportActivity.this, "You haven't selected any plan.", Toast.LENGTH_LONG).show();
                }
                Intent i = new Intent(BMIReportActivity.this, MainActivity.class);
                startActivity(i);
            }
        };

        btnBackToMain.setOnClickListener(e2);
        btnBackToMain2.setOnClickListener(e2);

        etCustomizedCalorie = (TextView)findViewById(R.id.etCustomizedCalorie);

        btnCustomizedPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(etCustomizedCalorie.getText().toString().matches("")))
                {
                    long customizedCalorie = Long.parseLong(etCustomizedCalorie.getText().toString());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("age",age);
                    editor.putString("gender",gender);
                    editor.putFloat("weight", (float)weight);
                    editor.putFloat("height", (float)height);
                    editor.putString("exercise_level", exercise_level);
                    editor.putLong("plan", customizedCalorie);
                    if(editor.commit())
                    {
                        Toast.makeText(BMIReportActivity.this, "Customized Plan of " + String.valueOf(customizedCalorie) + " kcal saved successfully!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(BMIReportActivity.this, "Now you can start recording calories for meals!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(BMIReportActivity.this, "Save Failed! Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(BMIReportActivity.this, "Please enter a value.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(sharedPreferences.getLong("plan", -1) == -1) {
            Toast.makeText(BMIReportActivity.this, "Please scroll down and select the plan.", Toast.LENGTH_LONG).show();
        }


    }
}
