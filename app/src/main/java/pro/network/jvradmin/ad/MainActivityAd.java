package pro.network.jvradmin.ad;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rahul.media.main.MediaFactory;

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
import java.util.List;
import java.util.Map;

import pro.network.jvradmin.R;
import pro.network.jvradmin.app.AndroidMultiPartEntity;
import pro.network.jvradmin.app.AppController;
import pro.network.jvradmin.app.Appconfig;


public class MainActivityAd extends AppCompatActivity implements  AdClick {

    AdimgAdapter shopAdapter;
    List<String> shopList = new ArrayList<>();
    ProgressDialog progressDialog;
    private MediaFactory mediaFactory;
    private ArrayList<String> all_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_img_main);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        final FloatingActionButton addShop = (FloatingActionButton) findViewById(R.id.addShop);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        shopAdapter = new AdimgAdapter(shopList, this, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(shopAdapter);
        addShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePicker();
            }
        });

        //getAllStaff();

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


    private void getAllStaff() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Fetching ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                Appconfig.AD_GET_ALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                try {
                    shopList = new ArrayList<>();
                    JSONObject jObj = new JSONObject(response);
                    String files = jObj.getString("files");
                    String[] filesStrings = files.split(",");
                    for (int i = 0; i < filesStrings.length; i++) {
                        if (filesStrings[i] != null &&
                                (filesStrings[i].contains(".jpg")
                                        || filesStrings[i].contains(".png")
                                        || filesStrings[i].contains(".PNG")
                                        || filesStrings[i].contains(".JPEG"))) {
                            shopList.add(filesStrings[i]);
                        }
                    }
                    shopAdapter.notifyData(shopList);

                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityAd.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityAd.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void deleteFile(final int position) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Fetching ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.DELETE,
                Appconfig.AD_DELETE+"?name="+shopList.get(position), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success") == 1) {
                        shopList.remove(position);
                        shopAdapter.notifyData(shopList);
                    }
                    Toast.makeText(MainActivityAd.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityAd.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityAd.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("name", shopList.get(position));
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    protected void onStart() {
        super.onStart();
        getAllStaff();
    }




    @Override
    public void onDeleteClick(int position) {
        deleteFile(position);
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
            progressDialog.setMessage("Uploading..." + (String.valueOf(progress[0])));
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
            HttpPost httppost = new HttpPost(Appconfig.AD_CREATE);
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
                getAllStaff();
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

    public void imagePicker() {
        final CharSequence[] items;
        if (Appconfig.isDeviceSupportCamera(MainActivityAd.this)) {
            items = new CharSequence[2];
            items[0] = "Camera";
            items[1] = "Gallery";
        } else {
            items = new CharSequence[1];
            items[0] = "Gallery";
        }

        android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(MainActivityAd.this);
        alertdialog.setTitle("Add Image");
        alertdialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
                    MediaFactory.MediaBuilder mediaBuilder = new MediaFactory.MediaBuilder(MainActivityAd.this)
                            .fromCamera()
                            .setPickCount(1)
                            .isSquareCrop(false)
                            .withAspectRatio(9, 16)
                            .doCropping();
                    mediaFactory = MediaFactory.create().start(mediaBuilder);
                } else if (items[item].equals("Gallery")) {
                    MediaFactory.MediaBuilder mediaBuilder = new MediaFactory.MediaBuilder(MainActivityAd.this)
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
    private void performUpload() {
        if (all_path.size() > 0) {
            String path = all_path.get(0);
            all_path.remove(0);
            showDialog();
            new MainActivityAd.UploadFileToServer().execute(Appconfig.compressImage(path));
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
}
