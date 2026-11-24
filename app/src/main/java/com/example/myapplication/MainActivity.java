package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editFirstName;
    private EditText editLastName;
    private EditText editEmail;

    private RadioGroup classGroup;
    private Spinner majorSpinner;

    private CheckBox notifications;
    private CheckBox terms;

    private TextView classLabel;
    private TextView majorLabel;

    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setUpSpinner();

        registerButton.setOnClickListener(v -> saveRegistration());

        Student savedStudent = Student.load(this);

        if (savedStudent != null) {
            Intent intent = new Intent(this, DetailsActivity.class);
            savedStudent.putInIntent(intent);
            startActivity(intent);
        }
    }

    private void initializeViews() {

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editEmail = findViewById(R.id.editEmail);

        classGroup = findViewById(R.id.classGroup);
        majorSpinner = findViewById(R.id.majorSpinner);

        notifications = findViewById(R.id.notifications);
        terms = findViewById(R.id.terms);

        classLabel = findViewById(R.id.classLabel);
        majorLabel = findViewById(R.id.majorLabel);

        registerButton = findViewById(R.id.button);
    }

    private void setUpSpinner() {

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.majors,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        majorSpinner.setAdapter(adapter);
    }

    private boolean validateInput() {

        classLabel.setText(getString(R.string.class_label));
        majorLabel.setText(getString(R.string.major_label));
        terms.setError(null);

        editFirstName.setError(null);
        editLastName.setError(null);
        editEmail.setError(null);

        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        boolean valid = true;

        if (firstName.isEmpty()) {
            editFirstName.setError(getString(R.string.error_first_name));
            valid = false;
        }

        if (lastName.isEmpty()) {
            editLastName.setError(getString(R.string.error_last_name));
            valid = false;
        }

        if (email.isEmpty() ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError(getString(R.string.error_email));
            valid = false;
        }

        if (classGroup.getCheckedRadioButtonId() == -1) {
            classLabel.setText(getString(R.string.error_class));
            valid = false;
        }

        if (majorSpinner.getSelectedItemPosition() == 0) {
            majorLabel.setText(getString(R.string.error_major));
            valid = false;
        }

        if (!terms.isChecked()) {
            terms.setError(getString(R.string.error_terms));
            valid = false;
        }

        return valid;
    }

    private void saveRegistration() {

        if (!validateInput()) {
            return;
        }

        int selectedId = classGroup.getCheckedRadioButtonId();
        RadioButton selectedClass = findViewById(selectedId);

        Student student = new Student(
                editFirstName.getText().toString().trim(),
                editLastName.getText().toString().trim(),
                editEmail.getText().toString().trim(),
                selectedClass.getText().toString(),
                majorSpinner.getSelectedItem().toString(),
                notifications.isChecked(),
                terms.isChecked()
        );

        student.save(this);

        Intent intent = new Intent(this, DetailsActivity.class);
        student.putInIntent(intent);

        startActivity(intent);
    }
}
