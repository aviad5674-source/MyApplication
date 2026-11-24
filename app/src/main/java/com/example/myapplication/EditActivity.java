
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {

    private EditText editFirstName;
    private EditText editLastName;
    private EditText editEmail;

    private RadioGroup classGroup;
    private Spinner majorSpinner;

    private CheckBox notifications;
    private CheckBox terms;

    private TextView classLabel;
    private TextView majorLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initializeViews();
        setUpSpinner();

        Student student = Student.fromIntent(getIntent());

        if (student == null) {
            Toast.makeText(this,
                    getString(R.string.student_not_found),
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fillFields(student);

        Button saveButton = findViewById(R.id.saveReturnButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> saveChanges());

        cancelButton.setOnClickListener(v -> finish());
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

    private void fillFields(Student student) {

        editFirstName.setText(student.getFirstName());
        editLastName.setText(student.getLastName());
        editEmail.setText(student.getEmail());

        notifications.setChecked(student.isNotifications());
        terms.setChecked(student.isTermsAccepted());

        for (int i = 0; i < classGroup.getChildCount(); i++) {

            RadioButton radioButton =
                    (RadioButton) classGroup.getChildAt(i);

            if (radioButton.getText().toString()
                    .equals(student.getStudentClass())) {

                radioButton.setChecked(true);
                break;
            }
        }

        ArrayAdapter adapter = (ArrayAdapter) majorSpinner.getAdapter();

        int position = adapter.getPosition(student.getMajor());

        if (position >= 0) {
            majorSpinner.setSelection(position);
        }
    }

    private boolean validateInput() {

        classLabel.setText(getString(R.string.class_label));
        majorLabel.setText(getString(R.string.major_label));

        editFirstName.setError(null);
        editLastName.setError(null);
        editEmail.setError(null);
        terms.setError(null);

        boolean valid = true;

        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

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

    private void saveChanges() {

        if (!validateInput()) {
            return;
        }

        int selectedId = classGroup.getCheckedRadioButtonId();

        RadioButton selectedClass = findViewById(selectedId);

        Student updatedStudent = new Student(
                editFirstName.getText().toString().trim(),
                editLastName.getText().toString().trim(),
                editEmail.getText().toString().trim(),
                selectedClass.getText().toString(),
                majorSpinner.getSelectedItem().toString(),
                notifications.isChecked(),
                terms.isChecked()
        );

        updatedStudent.save(this);

        Intent resultIntent = new Intent();
        updatedStudent.putInIntent(resultIntent);

        setResult(RESULT_OK, resultIntent);

        finish();
    }
}
