package dschik.noticeboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity implements DialogProfileActivity.DialogListerner {
    private SharedPreferences sh;
    TextView detail;
    private SharedPreferences.Editor shedit;
    private DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbref = db.getReference();


        sh = getSharedPreferences("shared", Context.MODE_PRIVATE);
        shedit = sh.edit();

        TextView username = findViewById(R.id.user_name);
        TextView useremail = findViewById(R.id.email_address);
        ImageView settings = findViewById(R.id.settings);
        detail = findViewById(R.id.user_detail);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String name = sh.getString("dis_name", "name");
        String email = sh.getString("dis_email", "email");
        String dept = sh.getString("dis_dept", "dept");
        String year = sh.getString("dis_year", "year");
        String details = dept + "-" + getYear(year);

        username.setText(name.toUpperCase());
        useremail.setText(email);
        detail.setText(details);
        //usernumber.setText(number);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();
            }
        });

    }

    private void showdialog() {
        DialogProfileActivity dialog_profile_activity = new DialogProfileActivity();
        dialog_profile_activity.show(getSupportFragmentManager(), "info_dialog");
    }

    @Override
    public void applyData(String department, String year) {

        String s = department + "-" + getYear(year);
        detail.setText(s);
        updateProfile(department, year);
    }

    String getYear(String year) {
        String yr = "";
        switch (year) {
            case "1":
                yr = year + "st";
                break;
            case "2":
                yr = year + "nd";
                break;
            case "3":
                yr = year + "rd";
                break;
            default:
                yr = year + "th";
                break;
        }
        return yr;
    }

    private void updateProfile(String department, String year) {
        String name = sh.getString("dis_name", "name");
        String email = sh.getString("dis_email", "email");
        shedit.putString("dis_dept", department);
        shedit.putString("dis_year", year);
        shedit.apply();

        UserObj user1 = new UserObj(name, email, "", department, year);
        //insert user info in db here
        dbref.child("user").child(getPath(email)).setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(ProfileActivity.this, "Authentication Passed", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @NonNull
    private String getPath(String email) {

        return email.replace(".", "");
    }



        /*String number = getMyPhoneNO();
        Toast.makeText(getApplicationContext(), "My Phone Number is: "
                + number, Toast.LENGTH_SHORT).show();

        TextView textView = (TextView) findViewById(R.id.phone_number);
        Log.d("aa","number"+number);
        textView.setText("My Phone number is: " + number);*/

}
    /*private String getMyPhoneNO() {
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission")
        String mPhoneNumber = tMgr.getLine1Number();
        return mPhoneNumber;
    }*/
