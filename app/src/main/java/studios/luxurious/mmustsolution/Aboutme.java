package studios.luxurious.mmustsolution;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class Aboutme extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutme);


    }

    public void toTwitter(View view) {

        try
                {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("twitter://user?screen_name=" + "codemasterKenya")));

                }
                catch (Exception paramAnonymousView)
                {
                    Aboutme.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://twitter.com/" + "codemasterKenya")));
                }
    }

    public void toEmail(View view) {


        try
                {
                    Intent toEmail = new Intent("android.intent.action.VIEW", Uri.parse("mailto:martinmbae.codemaster@gmail.com"));
                    startActivity(toEmail);

                }
                catch (ActivityNotFoundException paramAnonymousView) {

                    Toast.makeText(this, "Unable to connect to Email", Toast.LENGTH_LONG).show();

                }
            }


    public void toFacebook(View view) {

        try {
            this.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Intent facebookintent = new Intent("android.intent.action.VIEW", Uri.parse("fb://facewebmodal/f?href=" + "https://www.facebook.com/martin.chege.3998"));
            facebookintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            startActivity(facebookintent);
        } catch (Exception paramContext) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.facebook.com/martin.chege.3998")));
        }
    }

    public void More(View view) {

        String url_more_app ="https://play.google.com/store/apps/developer?id=Martin+Mbae";
        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse(url_more_app ));

        startActivity(viewIntent);
    }

    public void toInstagram(View view) {

        Uri uri = Uri.parse("http://instagram.com/_u/codemasterkenya");
        Intent insta = new Intent(Intent.ACTION_VIEW, uri);
        insta.setPackage("com.instagram.android");

        if (isIntentAvailable(insta)){
            startActivity(insta);
        } else{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/codemasterkenya")));
        }
    }

    private boolean isIntentAvailable(Intent intent) {
        final PackageManager packageManager = this.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}

