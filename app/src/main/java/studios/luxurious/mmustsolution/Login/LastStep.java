package studios.luxurious.mmustsolution.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import studios.luxurious.mmustsolution.HomeActivity;
import studios.luxurious.mmustsolution.R;

public class LastStep extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_step);
    }
    public void Start(View view) {

        startActivity(new Intent(LastStep.this, HomeActivity.class));
        finish();

    }

    @Override
    public void onBackPressed() {
    }
}
