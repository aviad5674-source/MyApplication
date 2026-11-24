package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    private TextView detailsTextView;
    private Student student;

    private ActivityResultLauncher<Intent> editLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        detailsTextView = findViewById(R.id.detailsTextView);

        Button editButton = findViewById(R.id.editButton);
        Button emailButton = findViewById(R.id.emailButton);
        Button shareButton = findViewById(R.id.shareButton);

        editLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK &&
                            result.getData() != null) {

                        Student updatedStudent =
                                Student.fromIntent(result.getData());

                        if (updatedStudent != null) {
                            student = updatedStudent;
                            student.save(this);
                            displayStudent();
                        }
                    }
                }
        );

        student = Student.fromIntent(getIntent());

        if (student == null) {
            student = Student.load(this);
        }

        if (student == null) {
            Toast.makeText(
                    this,
                    getString(R.string.student_not_found),
                    Toast.LENGTH_LONG
            ).show();
            finish();
            return;
        }

        displayStudent();

        editButton.setOnClickListener(v -> openEditScreen());

        emailButton.setOnClickListener(v -> sendEmail());

        shareButton.setOnClickListener(v -> shareDetails());
    }

    private void displayStudent() {

        String yes = getString(R.string.yes);
        String no = getString(R.string.no);

        String result =
                getString(R.string.details_first_name,
                        student.getFirstName()) + "\n\n" +

                        getString(R.string.details_last_name,
                                student.getLastName()) + "\n\n" +

                        getString(R.string.details_email,
                                student.getEmail()) + "\n\n" +

                        getString(R.string.details_class,
                                student.getStudentClass()) + "\n\n" +

                        getString(R.string.details_major,
                                student.getMajor()) + "\n\n" +

                        getString(R.string.details_notifications,
                                student.isNotifications() ? yes : no) + "\n\n" +

                        getString(R.string.details_terms,
                                student.isTermsAccepted() ? yes : no);

        detailsTextView.setText(result);
    }

    private void openEditScreen() {

        Intent intent = new Intent(this, EditActivity.class);
        student.putInIntent(intent);

        editLauncher.launch(intent);
    }

    private String createStudentMessage() {

        return getString(
                R.string.student_message,
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getStudentClass(),
                student.getMajor(),
                student.isNotifications()
                        ? getString(R.string.yes)
                        : getString(R.string.no),
                student.isTermsAccepted()
                        ? getString(R.string.yes)
                        : getString(R.string.no)
        );
    }


    private void sendEmail() {

        Intent intent = new Intent(Intent.ACTION_SENDTO);

        intent.setData(Uri.fromParts(
                "mailto",
                student.getEmail(),
                null
        ));

        intent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.email_subject)
        );

        intent.putExtra(
                Intent.EXTRA_TEXT,
                createStudentMessage()
        );

        try {
            startActivity(Intent.createChooser(intent, null));
        } catch (android.content.ActivityNotFoundException e) {

            Toast.makeText(
                    this,
                    getString(R.string.email_not_available),
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    private void shareDetails() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, createStudentMessage());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, null));
        } else {
            Toast.makeText(this,
                    getString(R.string.share_not_available),
                    Toast.LENGTH_LONG).show();
        }
    }
}
