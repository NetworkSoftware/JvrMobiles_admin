package pro.network.jvradmin.product;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
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
import pro.network.jvradmin.app.ActivityMediaOnline;
import pro.network.jvradmin.app.AndroidMultiPartEntity;
import pro.network.jvradmin.app.AppController;
import pro.network.jvradmin.app.Appconfig;

import static pro.network.jvradmin.app.Appconfig.PRODUCT_CREATE;

/**
 * Created by user_1 on 11-07-2018.
 */

public class ProductRegister extends AppCompatActivity implements ImageClick {


    private RecyclerView imagelist;
    private ArrayList<String> samplesList = new ArrayList<>();
    AddImageAdapter maddImageAdapter;

    MaterialBetterSpinner brand;
    EditText model;
    EditText price;
    EditText ram;
    EditText rom, description;

    private ProgressDialog pDialog;
    MaterialBetterSpinner category;

    MaterialBetterSpinner stock_update;

    private String[] STOCKUPDATE = new String[]{
            "In Stock", "Currently Unavailable",
    };

    private String imageUrl = "";
    CardView itemsAdd;

    TextView submit;

    private MediaFactory mediaFactory;
    ArrayList<String> all_path = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_register);


        getSupportActionBar().setTitle("Stock Register");

        itemsAdd = (CardView) findViewById(R.id.itemsAdd);
        ImageView image_wallpaper = (ImageView) findViewById(R.id.image_wallpaper);


        image_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePicker();
            }
        });

        samplesList = new ArrayList<>();
        imagelist = (RecyclerView) findViewById(R.id.imagelist);
        maddImageAdapter = new AddImageAdapter(this, samplesList, this);
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imagelist.setLayoutManager(addManager1);
        imagelist.setAdapter(maddImageAdapter);
        category = (MaterialBetterSpinner) findViewById(R.id.category);

        ArrayAdapter<String> titleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Appconfig.CATEGORY);
        category.setAdapter(titleAdapter);

        stock_update = (MaterialBetterSpinner) findViewById(R.id.stock_update);

        ArrayAdapter<String> stockAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, STOCKUPDATE);
        stock_update.setAdapter(stockAdapter);
        stock_update.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        brand = findViewById(R.id.brand);

        ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Appconfig.BRAND);
        brand.setAdapter(brandAdapter);
        brand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        model = (EditText) findViewById(R.id.model);
        price = (EditText) findViewById(R.id.price);
        ram = (EditText) findViewById(R.id.ram);
        rom = (EditText) findViewById(R.id.rom);
        description = findViewById(R.id.description);

        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (category.getText().toString().equalsIgnoreCase(("Mobiles"))) {
                    ram.setVisibility(View.VISIBLE);
                    rom.setVisibility(View.VISIBLE);
                } else {
                    ram.setVisibility(View.GONE);
                    rom.setVisibility(View.GONE);
                }

                ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(ProductRegister.this,
                        android.R.layout.simple_dropdown_item_1line, Appconfig.stringMap.get(category.getText().toString()));
                brand.setAdapter(brandAdapter);
            }
        });

        submit = (TextView) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (category.getText().toString().length() <= 0) {
                    category.setError("Select the Category");
                } else if (brand.getText().toString().length() <= 0) {
                    brand.setError("Select the Brand");
                } else if (model.getText().toString().length() <= 0) {
                    model.setError("Enter the Model");
                } else if (price.getText().toString().length() <= 0) {
                    price.setError("Enter the Price");
                } else if (stock_update.getText().toString().length() <= 0) {
                    stock_update.setError("Select the Sold or Not");
                } else if (samplesList.size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Upload the Images!", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser();
                }

            }
        });


    }

    private void registerUser() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Createing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                PRODUCT_CREATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response.split("0000")[1]);
                    boolean success = jsonObject.getBoolean("success");
                    String msg = jsonObject.getString("message");
                    if (success) {
                        final String descrip = description.getText().toString();
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
                localHashMap.put("category", category.getText().toString());
                localHashMap.put("brand", brand.getText().toString());
                localHashMap.put("model", model.getText().toString());
                localHashMap.put("price", price.getText().toString());
                localHashMap.put("ram", ram.getText().toString());
                localHashMap.put("rom", rom.getText().toString());
                localHashMap.put("image", new Gson().toJson(samplesList));
                localHashMap.put("description", description.getText().toString());
                localHashMap.put("stock_update", stock_update.getText().toString());
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

    @Override
    public void onImageClick(int position) {
        Intent localIntent = new Intent(ProductRegister.this, ActivityMediaOnline.class);
        localIntent.putExtra("filePath", samplesList.get(position));
        localIntent.putExtra("isImage", true);
        startActivity(localIntent);
    }

    @Override
    public void onDeleteClick(int position) {
        samplesList.remove(position);
        maddImageAdapter.notifyData(samplesList);
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
                JSONObject jsonObject = new JSONObject(result);
                if (!jsonObject.getBoolean("error")) {
                    imageUrl = Appconfig.ip + "/images/" + filepath.substring(filepath.lastIndexOf('/') + 1, filepath.length());
                    samplesList.add(imageUrl);
                    maddImageAdapter.notifyData(samplesList);
                } else {
                    imageUrl = null;
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (Error | Exception e) {
                Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
            }
            // showing the server response in an alert dialog
            //showAlert(result);

            performUpload();

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
            Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
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
        if (samplesList.size() <= Appconfig.MAX_COUNT) {
            final CharSequence[] items;
            if (Appconfig.isDeviceSupportCamera(ProductRegister.this)) {
                items = new CharSequence[2];
                items[0] = "Camera";
                items[1] = "Gallery";
            } else {
                items = new CharSequence[1];
                items[0] = "Gallery";
            }

            android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(ProductRegister.this);
            alertdialog.setTitle("Add Image");
            alertdialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Camera")) {
                        MediaFactory.MediaBuilder mediaBuilder = new MediaFactory.MediaBuilder(ProductRegister.this)
                                .fromCamera()
                                .setPickCount(Appconfig.MAX_COUNT - samplesList.size())
                                .isSquareCrop(false)
                                .doCropping();
                        mediaFactory = MediaFactory.create().start(mediaBuilder);
                    } else if (items[item].equals("Gallery")) {
                        MediaFactory.MediaBuilder mediaBuilder = new MediaFactory.MediaBuilder(ProductRegister.this)
                                .fromGallery()
                                .setPickCount(Appconfig.MAX_COUNT - samplesList.size())
                                .isSquareCrop(false)
                                .doCropping();
                        mediaFactory = MediaFactory.create().start(mediaBuilder);
                    }
                }
            });
            alertdialog.show();
        }
    }

    private void sendNotification(String title, String description) {
        String tag_string_req = "req_register";
        showDialog();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to", "/topics/allDevices");
            jsonObject.put("priority", "high");
            JSONObject dataObject = new JSONObject();
            dataObject.put("title", title);
            dataObject.put("message", description);
            jsonObject.put("data", dataObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                finish();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap hashMap = new HashMap();
                hashMap.put("Content-Type", "application/json");
                hashMap.put("Authorization", "key=AAAAMMylh7Y:APA91bERhWc8scjxohE0XlY-ra1ZMYXgGCEvr3zA5rkiEM6A7De4qYURE4hORBmUzPLx5PUraKNbM0ScMy_qbyGc3uVBrKNJxhDlDpvJa5U_7ZR0_XZuTTj7ApBcKClYGnZB54UzVhm_");
                return hashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
