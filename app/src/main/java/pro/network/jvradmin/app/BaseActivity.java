package pro.network.jvradmin.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import pro.network.jvradmin.StartActivity;

import static pro.network.jvradmin.app.Appconfig.mypreference;
public abstract class BaseActivity extends AppCompatActivity {

    protected SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        startDemo();
    }



    protected void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    protected void setActionBar(int theme) {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(theme);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(theme));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }
    protected void logout() {
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.commit();
        editor.apply();
        startActivity(new Intent(pro.network.jvradmin.app.BaseActivity.this, StartActivity.class));
        finishAffinity();
    }
    protected abstract void startDemo();

}
