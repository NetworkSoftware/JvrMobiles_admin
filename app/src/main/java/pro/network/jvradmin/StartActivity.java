package pro.network.jvradmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Log.e("TOken ", "" + FirebaseInstanceId.getInstance().getToken());
        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");


        final EditText editText = (EditText) findViewById(R.id.password);
        editText.setText("12344321");
        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("12344321")) {
                    Intent io = new Intent(StartActivity.this, NaviActivity.class);
                    startActivity(io);
                    finish();
                }
            }
        });
    }
}
