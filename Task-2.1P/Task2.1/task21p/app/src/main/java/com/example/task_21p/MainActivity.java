package com.example.task_21p;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private Spinner unitTypeSpinner;
    private Spinner sourceUnitSpinner;
    private Spinner targetUnitSpinner;
    private EditText inputValue;
    private Button convertButton;
    private TextView conversionResult;
    private FloatingActionButton switchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unitTypeSpinner = findViewById(R.id.UnitSpinner);
        sourceUnitSpinner = findViewById(R.id.SourceValueSpinner);
        targetUnitSpinner = findViewById(R.id.TargetValueSpinner);
        switchButton = findViewById(R.id.SwitchButton);
        inputValue = findViewById(R.id.InputValue);
        convertButton = findViewById(R.id.ConvertButton);
        conversionResult = findViewById(R.id.ConversionResult);
        setupUnitTypeSpinner();
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSpinnerValues();
            }
        });
        convertButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onClick(View view) {
                try {
                    String unitType = unitTypeSpinner.getSelectedItem().toString();
                    String sourceUnit = sourceUnitSpinner.getSelectedItem().toString();
                    String targetUnit = targetUnitSpinner.getSelectedItem().toString();

                    double input = Double.parseDouble(inputValue.getText().toString());
                    double result = convertUnits(unitType, sourceUnit, targetUnit, input);
                    conversionResult.setText(String.format("Result of conversion: %.2f %s", result, targetUnit));
                } catch (NumberFormatException e) {
                    conversionResult.setText("Please enter a valid number");
                }
            }
        });
    }

    private void setupUnitTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.unit_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitTypeSpinner.setAdapter(adapter);

        unitTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                int arrayId;
                switch (position){
                    case 0:
                        arrayId = R.array.length_units;
                        break;
                    case 1:
                        arrayId = R.array.weight_units;
                        break;
                    case 2:
                        arrayId = R.array.temperature_units;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid unit type");
                }
                ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(MainActivity.this, arrayId, android.R.layout.simple_spinner_item);
                unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sourceUnitSpinner.setAdapter(unitAdapter);
                targetUnitSpinner.setAdapter(unitAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void switchSpinnerValues() {
        int sourcePosition = sourceUnitSpinner.getSelectedItemPosition();
        int targetPosition = targetUnitSpinner.getSelectedItemPosition();
        sourceUnitSpinner.setSelection(targetPosition);
        targetUnitSpinner.setSelection(sourcePosition);
    }

    public static double convertUnits(String unitType, String sourceUnit, String targetUnit, double inputValue){
        double result =0;
        switch (unitType){
            case "Length":
                result = convertLengthUnits(sourceUnit, targetUnit, inputValue);
                break;
            case "Weight":
                result = convertWeightUnits(sourceUnit, targetUnit, inputValue);
                break;
            case "Temperature":
                result = convertTemperatureUnits(sourceUnit, targetUnit, inputValue);
                break;
            default:
                System.out.println("Invalid unit type");
                break;
        }
        return result;
    }
    public static double convertLengthUnits(String sourceUnit, String targetUnit, double inputValue) {
        double cmValue;
        switch (sourceUnit) {
            case "inch":
                cmValue = inputValue * 2.54;
                break;
            case "foot":
                cmValue = inputValue * 30.48;
                break;
            case "yard":
                cmValue = inputValue * 91.44;
                break;
            case "mile":
                cmValue = inputValue * 160934;
                break;
            case "cm":
                cmValue = inputValue;
                break;
            case "m":
                cmValue = inputValue * 100;
                break;
            case "km":
                cmValue = inputValue * 100000;
                break;
            default:
                throw new IllegalArgumentException("Invalid length unit");
        }
        switch (targetUnit) {
            case "inch":
                return cmValue / 2.54;
            case "foot":
                return cmValue / 30.48;
            case "yard":
                return cmValue / 91.44;
            case "mile":
                return cmValue / 160934;
            case "cm":
                return cmValue;
            case "m":
                return cmValue / 100;
            case "km":
                return cmValue / 100000;
            default:
                throw new IllegalArgumentException("Invalid length unit");
        }
    }
    public static double convertWeightUnits(String sourceUnit, String targetUnit, double inputValue) {
        double gramValue;
        switch (sourceUnit) {
            case "pound":
                gramValue = inputValue * 453.592;
                break;
            case "ounce":
                gramValue = inputValue * 28.3495;
                break;
            case "ton":
                gramValue = inputValue * 907185;
                break;
            case "g":
                gramValue = inputValue;
                break;
            case "kg":
                gramValue = inputValue * 1000;
                break;
            default:
                throw new IllegalArgumentException("Invalid weight unit");
        }
        switch (targetUnit) {
            case "pound":
                return gramValue / 453.592;
            case "ounce":
                return gramValue / 28.3495;
            case "ton":
                return gramValue / 907185;
            case "g":
                return gramValue;
            case "kg":
                return gramValue / 1000;
            default:
                throw new IllegalArgumentException("Invalid weight unit");
        }
    }
    public static double convertTemperatureUnits(String sourceUnit, String targetUnit, double inputValue) {
        double celsiusValue;
        switch (sourceUnit) {
            case "C":
                celsiusValue = inputValue;
                break;
            case "F":
                celsiusValue = (inputValue - 32) / 1.8;
                break;
            case "K":
                celsiusValue = inputValue - 273.15;
                break;
            default:
                throw new IllegalArgumentException("Invalid temperature unit");
        }
        switch (targetUnit) {
            case "C":
                return celsiusValue;
            case "F":
                return (celsiusValue * 1.8) + 32;
            case "K":
                return celsiusValue + 273.15;
            default:
                throw new IllegalArgumentException("Invalid temperature unit");
        }
    }
}