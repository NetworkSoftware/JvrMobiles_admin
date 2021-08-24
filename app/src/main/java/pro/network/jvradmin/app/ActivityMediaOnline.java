package pro.network.jvradmin.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.PhotoView;

import pro.network.jvradmin.R;


public class ActivityMediaOnline extends AppCompatActivity {
    private String filePath = null;
    private PhotoView imgPreview;
    ProgressDialog pDialog;

    private void previewMedia(boolean paramBoolean) {

        if (paramBoolean) {
            this.imgPreview.setVisibility(View.VISIBLE);

            GlideApp.with(ActivityMediaOnline.this).
                    load(filePath).placeholder(R.drawable.icon).into(imgPreview);

            return;
        }

    }


    protected void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_mediaonline);
        imgPreview = ((PhotoView) findViewById(R.id.previewimg));
        imgPreview.setOnMatrixChangeListener(new MatrixChangeListener());
        imgPreview.setOnPhotoTapListener(new PhotoTapListener());
        imgPreview.setOnSingleFlingListener(new SingleFlingListener());
        Intent localIntent = getIntent();
        this.filePath = localIntent.getStringExtra("filePath");
        boolean bool = localIntent.getBooleanExtra("isImage", true);
        if (this.filePath != null) {
            previewMedia(bool);
            String value = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length());
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            return;
        }

        Toast.makeText(getApplicationContext(), "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
    }


    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            /// showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
        }
    }

    private void showToast(CharSequence text) {
//        if (mCurrentToast != null) {
//            mCurrentToast.cancel();
//        }

        //   mCurrentToast = Toast.makeText(SimpleSampleActivity.this, text, Toast.LENGTH_SHORT);
        // mCurrentToast.show();
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
            // mCurrMatrixTv.setText(rect.toString());
        }
    }

    private class SingleFlingListener implements OnSingleFlingListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // Log.d("PhotoView", String.format(FLING_LOG_STRING, velocityX, velocityY));
            return true;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String changeCabs(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
}
/* Location:           D:\Gopal\downloads\Apk decompile java\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     smart.breed.contest.ActivityMedia
 * JD-Core Version:    0.6.0
 */