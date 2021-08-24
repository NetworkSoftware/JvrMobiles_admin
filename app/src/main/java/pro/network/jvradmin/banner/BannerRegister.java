package pro.network.jvradmin.banner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rahul.media.main.MediaFactory;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pro.network.jvradmin.R;
import pro.network.jvradmin.app.AndroidMultiPartEntity;
import pro.network.jvradmin.app.AppController;
import pro.network.jvradmin.app.Appconfig;
import pro.network.jvradmin.app.GlideApp;

import static pro.network.jvradmin.app.Appconfig.BANNERS_CREATE;

/**
 * Created by user_1 on 11-07-2018.
 */

public class BannerRegister extends AppCompatActivity {


    private ProgressDialog pDialog;

    EditText description;

    String studentId = null;

    TextView submit;
    private ImageView profiletImage;
    private String imageUrl = "";

    MaterialBetterSpinner type;

    private String[] TYPE = new String[]{
            "Banner", "Slider",
    };

    private MediaFactory mediaFactory;
    private ArrayList<String> all_path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.banner_register);


        getSupportActionBar().setTitle("Banner Register");

        profiletImage = (ImageView) findViewById(R.id.profiletImage);
        profiletImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePicker();
            }
        });


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        description = (EditText) findViewById(R.id.description);
        submit = (TextView) findViewById(R.id.submit);
        type = (MaterialBetterSpinner) findViewById(R.id.type);

        ArrayAdapter<String> titleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TYPE);
        type.setAdapter(titleAdapter);
        type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (description.getText().toString().length() <= 0) {
                    description.setError("Enter the Descripton");
                }else {
                    registerUser();
                }
            }
        });


    }

    private void registerUser() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Uploading ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                BANNERS_CREATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String msg = jsonObject.getString("message");
                    if (success) {
                        finish();
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();

                localHashMap.put("banner", imageUrl);
                localHashMap.put("description", description.getText().toString());
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());

        AppController.getInstance().addToRequestQueue(strReq);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    protected void onPause() {
        super.onPause();
        hideDialog();
    }

    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
        String filepath;
        public long totalSize = 0;

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pDialog.setMessage("Uploading..." + (String.valueOf(progress[0])));
        }

        @Override
        protected String doInBackground(String... params) {
            filepath = params[0];
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Appconfig.URL_IMAGE_UPLOAD);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filepath);
                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);

                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;

                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Response from server: ", result);
            try {
                JSONObject jsonObject = new JSONObject(result.toString());
                if (!jsonObject.getBoolean("error")) {
                    GlideApp.with(getApplicationContext())
                            .load(filepath)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false)
                            .placeholder(R.drawable.mobile_phone)
                            .into(profiletImage);
                    imageUrl = Appconfig.ip + "/images/" + filepath.substring(filepath.lastIndexOf('/') + 1, filepath.length());
                } else {
                    imageUrl = null;
                }
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
            }
            hideDialog();
            // showing the server response in an alert dialog
            //showAlert(result);


            super.onPostExecute(result);
        }

    }

    private void performUpload() {
        if (all_path.size() > 0) {
            String path = all_path.get(0);
            all_path.remove(0);
            showDialog();
            new UploadFileToServer().execute(Appconfig.compressImage(path));
        } else {
            hideDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            all_path = new ArrayList<>();
            all_path = mediaFactory.onActivityResult(requestCode, resultCode, data);
            performUpload();
        } catch (Exception e) {
            Log.e("xxxxxxxx", e.toString());
        }

    }

    public void imagePicker() {
        final CharSequence[] items;
        if (Appconfig.isDeviceSupportCamera(BannerRegister.this)) {
            items = new CharSequence[2];
            items[0] = "Camera";
            items[1] = "Gallery";
        } else {
            items = new CharSequence[1];
            items[0] = "Gallery";
        }

        android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(BannerRegister.this);
        alertdialog.setTitle("Add Image");
        alertdialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
                    MediaFactory.MediaBuilder mediaBuilder = new MediaFactory.MediaBuilder(BannerRegister.this)
                            .fromCamera()
                            .setPickCount(1)
                            .isSquareCrop(false)
                            .withAspectRatio(9, 16)
                            .doCropping();
                    mediaFactory = MediaFactory.create().start(mediaBuilder);
                } else if (items[item].equals("Gallery")) {
                    MediaFactory.MediaBuilder mediaBuilder = new MediaFactory.MediaBuilder(BannerRegister.this)
                            .fromGallery()
                            .setPickCount(1)
                            .isSquareCrop(false)
                            .withAspectRatio(9, 16)
                            .doCropping();
                    mediaFactory = MediaFactory.create().start(mediaBuilder);
                }
            }
        });
        alertdialog.show();
    }

}
