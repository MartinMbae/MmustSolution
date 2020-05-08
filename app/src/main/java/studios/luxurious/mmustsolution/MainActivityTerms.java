package studios.luxurious.mmustsolution;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

import net.khirr.android.privacypolicy.PrivacyPolicyDialog;

import studios.luxurious.mmustsolution.Login.RegistrationNumber;

public class MainActivityTerms extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_terms);

        mAuth = FirebaseAuth.getInstance();

        //  Params: context, termsOfService url, privacyPolicyUrl
        PrivacyPolicyDialog dialog = new PrivacyPolicyDialog(this,
                "http://classattendancemmust.000webhostapp.com/mmustapp/terms.html",
                "http://classattendancemmust.000webhostapp.com/mmustapp/policy.html");


        dialog.setOnClickListener(new PrivacyPolicyDialog.OnClickListener() {
            @Override
            public void onAccept(boolean isFirstTime) {
                Intent intent;
                if (mAuth.getCurrentUser() == null)
                    intent = new Intent(MainActivityTerms.this, RegistrationNumber.class);
                else
                    intent = new Intent(MainActivityTerms.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }
        });

        dialog.addPoliceLine("By using this Mobile Application and by agreeing to this Agreement you warrant and represent that you are a MMUST student or staff member.");
        dialog.addPoliceLine("Although this Mobile Application may link to other mobile applications, we are not, directly or indirectly, implying any approval, association, sponsorship, endorsement, or affiliation with any linked mobile application, unless specifically stated herein. We are not responsible for examining or evaluating, and we do not warrant the offerings of, any businesses or individuals or the content of their mobile applications.");
        dialog.addPoliceLine("This application requires internet access and must collect the following information: Token to send notifications and information about the language of the device.");
        dialog.addPoliceLine("We do not own any data, information or material that you submit in the Chatroom section of this Mobile Application in the course of using the Service. You shall have sole responsibility for the accuracy, quality, integrity, legality, reliability, appropriateness, and intellectual property ownership or right to use of all submitted Content.");
        dialog.addPoliceLine("You acknowledge that you have read this Agreement and agree to all its terms and conditions. By using the Mobile Application or its Services you agree to be bound by this Agreement. If you do not agree to abide by the terms of this Agreement, you are not authorized to use or access the Mobile Application and its Services.");
        dialog.addPoliceLine("All details about the use of data are available in our Privacy Policies, as well as all Terms of Service links below.");

        //  Customizing (Optional)
        dialog.setTitleTextColor(Color.parseColor("#222222"));
        dialog.setAcceptButtonColor(ContextCompat.getColor(this, R.color.colorAccent));

        //  Title
        dialog.setTitle("Terms of Service");

        //  {terms}Terms of Service{/terms} is replaced by a link to your terms
        //  {privacy}Privacy Policy{/privacy} is replaced by a link to your privacy policy
        dialog.setTermsOfServiceSubtitle("If you click on {accept}, you acknowledge that it makes the content present and all the content of our {terms}Terms of Service{/terms} and implies that you have read our {privacy}Privacy Policy{privacy}.");

        //  Set Europe only
//        dialog.setEuropeOnly(true);

        dialog.show();
    }
}
