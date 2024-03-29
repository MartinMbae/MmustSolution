package studios.luxurious.mmustsolution;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Contact_us extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
    }

    public void PoBox(View view) {
        try
        {
            Intent toEmail = new Intent("android.intent.action.VIEW", Uri.parse("mailto:info@mmust.ac.ke"));
            startActivity(toEmail);
        }
        catch (ActivityNotFoundException paramAnonymousView) {
            Toast.makeText(this, "Unable to connect to Email", Toast.LENGTH_LONG).show();
        }

    }

    public void Phone(View view) {


        startActivity(new Intent("android.intent.action.DIAL", Uri.fromParts("tel", "056-31375", null)));

    }

    public void Email(View view) {

        try
        {
            Intent toEmail = new Intent("android.intent.action.VIEW", Uri.parse("mailto:info@mmust.ac.ke"));
            startActivity(toEmail);

        }
        catch (ActivityNotFoundException paramAnonymousView) {

            Toast.makeText(this, "Unable to connect to Email", Toast.LENGTH_LONG).show();

        }

    }
}