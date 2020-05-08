package studios.luxurious.mmustsolution;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ClassAttendance extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);


        String str2 = "The selected option is not offically available.";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClassAttendance.this);
        alertDialog.setTitle("Not Available")
                .setMessage(str2)

                .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent d = new Intent(ClassAttendance.this,HomeActivity.class);
                        d.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        d.putExtra("PlayVideo","No");
                        startActivity(d);
                        finish();
                    }
                })
                .setCancelable(false)
                .show();


    }
}
