package pro.network.jvradmin.product;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.network.jvradmin.R;
import pro.network.jvradmin.app.ActivityMediaOnline;
import pro.network.jvradmin.app.AndroidMultiPartEntity;
import pro.network.jvradmin.app.AppController;
import pro.network.jvradmin.app.Appconfig;

import static pro.network.jvradmin.app.Appconfig.MAX_COUNT;
import static pro.network.jvradmin.app.Appconfig.PRODUCT_DELETE;
import static pro.network.jvradmin.app.Appconfig.PRODUCT_GET_ID;
import static pro.network.jvradmin.app.Appconfig.PRODUCT_UPDATE;

/**
 * Created by user_1 on 11-07-2018.
 */

public class ProductUpdate extends AppCompatActivity implements ImageClick {


    MaterialBetterSpinner brand;
    EditText model;
    EditText price;
    EditText ram;
    EditText rom, description;

    private ProgressDialog pDialog;
    private RecyclerView imagelist;
    private ArrayList<String> samplesList = new ArrayList<>();
    private MediaFactory mediaFactory;
    AddImageAdapter maddImageAdapter;

    MaterialBetterSpinner category;


    MaterialBetterSpinner stock_update;

    private String[] STOCKUPDATE = new String[]{
            "In Stock", "Currently Unavailable",
    };
    String studentId = null;

    TextView submit;
    CardView itemsAdd;
    private String imageUrl = "";
    ArrayList<String> all_path = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_register);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        itemsAdd = (CardView) findViewById(R.id.itemsAdd);
        ImageView image_wallpaper = (ImageView) findViewById(R.id.image_wallpaper);
        image_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


        model = (EditText) findViewById(R.id.model);
        price = (EditText) findViewById(R.id.price);
        ram = (EditText) findViewById(R.id.ram);
        rom = (EditText) findViewById(R.id.rom);
        description = findViewById(R.id.description);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Appconfig.CATEGORY);
        category.setAdapter(categoryAdapter);

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
                ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(ProductUpdate.this,
                        android.R.layout.simple_dropdown_item_1line, Appconfig.stringMap.get(category.getText().toString()));
                brand.setAdapter(brandAdapter);
            }
        });
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
        submit = (TextView) findViewById(R.id.submit);
        submit.setText("UPDATE");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (category.getText().toString().length() <= 0) {
                    category.setError("Select the Category");
                } else if (brand.getText().toString().length() <= 0) {
                    brand.setError("Enter the Brand");
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


        if (getIntent().getBooleanExtra("isServer", false)) {
            getProductById(getIntent().getStringExtra("id"));
        } else {
            Product contact = (Product) getIntent().getSerializableExtra("data");
            doAssignAllValues(contact);
        }


    }


    private void getProductById(final String id) {
        String tag_string_req = "req_register";
        pDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                PRODUCT_GET_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d("Register Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Product product = new Product();
                            product.setId(jsonObject.getString("id"));
                            product.setBrand(jsonObject.getString("brand"));
                            product.setCategory(jsonObject.getString("category"));
                            product.setStock_update(jsonObject.getString("stock_update"));
                            product.setDescription(jsonObject.getString("description"));
                            product.setPrice(jsonObject.getString("price"));
                            product.setRam(jsonObject.getString("ram"));
                            product.setRom(jsonObject.getString("rom"));
                            product.setModel(jsonObject.getString("model"));
                            product.setImage(jsonObject.getString("image"));
                            doAssignAllValues(product);
                        }
                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("id", id);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void doAssignAllValues(Product contact) {
        try {
            category.setText(contact.category);
            brand.setText(contact.brand);
            model.setText(contact.model);
            price.setText(contact.price);
            description.setText(contact.description);
            studentId = contact.id;
            stock_update.setText(contact.stock_update);
            imageUrl = contact.image;
            if (imageUrl == null) {
                imageUrl = "";
            } else {
                samplesList = new Gson().fromJson(imageUrl, (Type) List.class);
            }
            maddImageAdapter.notifyData(samplesList);
        } catch (Exception e) {
            Log.e("xxxxxxxxxxx", e.toString());
        }
    }

    private void registerUser() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Updateing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.PUT,
                PRODUCT_UPDATE, new Response.Listener<String>() {
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
                localHashMap.put("category", category.getText().toString());
                localHashMap.put("brand", brand.getText().toString());
                localHashMap.put("model", model.getText().toString());
                localHashMap.put("price", price.getText().toString());
                localHashMap.put("ram", ram.getText().toString());
                localHashMap.put("rom", rom.getText().toString());
                localHashMap.put("stock_update", stock_update.getText().toString());
                localHashMap.put("id", studentId);
                localHashMap.put("image", new Gson().toJson(samplesList));
                localHashMap.put("description", description.getText().toString().replaceAll("[^a-zA-Z0-9]", " "));
                return localHashMap;
            }
        };

        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void deleteUser() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.DELETE,
                PRODUCT_DELETE+"?id="+studentId, new Response.Listener<String>() {
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
                localHashMap.put("id", studentId);
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
            performUpload();
            // showing the server response in an alert dialog
            //showAlert(result);


            super.onPostExecute(result);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_delete:
                AlertDialog diaBox = AskOption();
                diaBox.show();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.ic_delete_black_24dp)

                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        dialog.dismiss();
                        deleteUser();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myQuittingDialogBox;
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
        if (samplesList.size() <= MAX_COUNT) {
            final CharSequence[] items;
            if (Appconfig.isDeviceSupportCamera(ProductUpdate.this)) {
                items = new CharSequence[2];
                items[0] = "Camera";
                items[1] = "Gallery";
            } else {
                items = new CharSequence[1];
                items[0] = "Gallery";
            }

            android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(ProductUpdate.this);
            alertdialog.setTitle("Add Image");
            alertdialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Camera")) {
                        MediaFactory.MediaBuilder mediaBuilder = new MediaFactory.MediaBuilder(ProductUpdate.this)
                                .fromCamera()
                                .setPickCount(MAX_COUNT - samplesList.size())
                                .isSquareCrop(false)
                                .withAspectRatio(9, 16)
                                .doCropping();
                        mediaFactory = MediaFactory.create().start(mediaBuilder);
                    } else if (items[item].equals("Gallery")) {
                        MediaFactory.MediaBuilder mediaBuilder = new MediaFactory.MediaBuilder(ProductUpdate.this)
                                .fromGallery()
                                .setPickCount(MAX_COUNT - samplesList.size())
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

    @Override
    public void onImageClick(int position) {
        Intent localIntent = new Intent(ProductUpdate.this, ActivityMediaOnline.class);
        localIntent.putExtra("filePath", samplesList.get(position));
        localIntent.putExtra("isImage", true);
        startActivity(localIntent);
    }

    @Override
    public void onDeleteClick(int position) {
        samplesList.remove(position);
        maddImageAdapter.notifyData(samplesList);
    }

}
