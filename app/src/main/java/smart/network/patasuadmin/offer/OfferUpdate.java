package smart.network.patasuadmin.offer;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import smart.network.patasuadmin.R;
import smart.network.patasuadmin.app.AndroidMultiPartEntity;
import smart.network.patasuadmin.app.Appconfig;
import smart.network.patasuadmin.app.GlideApp;
import smart.network.patasuadmin.app.Imageutils;
import smart.network.patasuadmin.attachment.ActivityMediaOnline;
import smart.network.patasuadmin.attachment.AttachmentBaseAdapter;
import smart.network.patasuadmin.attachment.Base;
import smart.network.patasuadmin.attachment.BaseClick;

import static smart.network.patasuadmin.app.Appconfig.mypreference;

/**
 * Created by user_1 on 11-07-2018.
 */

public class OfferUpdate extends AppCompatActivity implements BaseClick, Imageutils.ImageAttachmentListener {

    EditText name;
    EditText startDate;
    EditText endDate;
    private TextView submit;
    private ProgressDialog pDialog;
    SharedPreferences sharedpreferences;
    Imageutils imageutils;
    private ImageView image;
    private String imageUrl = null;

    private Offer shop;
    String shopId = null;
    String oldName;
    private RecyclerView baseList;
    private AttachmentBaseAdapter attachmentBaseAdapter;
    private ArrayList<Base> bases = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_register);

        imageutils = new Imageutils(this);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(1);
            }
        });

        name = (EditText) findViewById(R.id.name);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);


        bases.add(new Base("", "true"));
        baseList = (RecyclerView) findViewById(R.id.attachmentList);
        attachmentBaseAdapter = new AttachmentBaseAdapter(this, bases, this);
        baseList.setLayoutManager(new GridLayoutManager(this, 3));
        baseList.setAdapter(attachmentBaseAdapter);


        submit = (TextView) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().length() > 0 &&
                        startDate.getText().toString().length() > 0 &&
                        endDate.getText().toString().length() > 0
                ) {

                    if (imageUrl == null) {
                        Toast.makeText(getApplicationContext(), "Select a Image", Toast.LENGTH_SHORT).show();
                    } else {
                        Offer shop = new Offer(name.getText().toString(),
                                imageUrl, startDate.getText().toString(),
                                endDate.getText().toString());
                        pDialog.show();
                        new UploadDataToServer().execute();
                    }
                }
            }
        });


        try {
            shop = (Offer) getIntent().getSerializableExtra("object");
            shopId = shop.id;
            name.setText(shop.name);
            startDate.setText(shop.startDate);
            endDate.setText(shop.endDate);
            oldName = shop.name;
            ;
            imageUrl = shop.getImage();
            GlideApp.with(getApplicationContext())
                    .load(imageUrl)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .placeholder(R.drawable.profile)
                    .into(image);
            bases=new ArrayList<>();
            for (int i = 0; i < shop.getImage1().size(); i++) {
                if(i==0 && shop.getImage1().get(i).length()!=0){
                    bases.add(new Base("", "true"));
                }
                bases.add(new Base(shop.getImage1().get(i), "true"));
            }
            if (bases.size() < 0) {
                bases.add(new Base("", "true"));
            }
            attachmentBaseAdapter.notifyData(bases);
            submit.setText("Update");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No Shop found", Toast.LENGTH_SHORT).show();
            finish();
        }

        startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(OfferUpdate.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    int latestmonth = monthOfYear + 1;
                                    String monthConverted = "" +latestmonth;
                                    if (latestmonth < 10) {
                                        monthConverted = "0" + monthConverted;
                                    }


                                    String dateConverted = "" + dayOfMonth;
                                    if (dayOfMonth < 10) {
                                        dateConverted = "0" + dayOfMonth;
                                    }
                                    startDate.setText(year + "-" + monthConverted + "-" + dateConverted);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            }
        });


        endDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(OfferUpdate.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    int latestmonth = monthOfYear + 1;
                                    String monthConverted = "" +latestmonth;
                                    if (latestmonth < 10) {
                                        monthConverted = "0" + monthConverted;
                                    }


                                    String dateConverted = "" + dayOfMonth;
                                    if (dayOfMonth < 10) {
                                        dateConverted = "0" + dayOfMonth;
                                    }
                                    endDate.setText(year + "-" + monthConverted + "-" + dateConverted);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            }
        });
    }


    private class UploadDataToServer extends AsyncTask<String, Integer, String> {
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
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Appconfig.UPDATE_OFFER);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                entity.addPart("id", new StringBody(shopId));
                entity.addPart("name", new StringBody(name.getText().toString()));
                entity.addPart("image", new StringBody(imageUrl));
                entity.addPart("endDate", new StringBody(endDate.getText().toString()));
                entity.addPart("startDate", new StringBody(startDate.getText().toString()));
                for (int i = 0; i < bases.size(); i++) {
                    entity.addPart("image1[]", new StringBody(bases.get(i).getUrl()));
                }

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
            pDialog.hide();
            Log.e("Response from server: ", result);
            try {
                JSONObject jObj = new JSONObject(result.split("0000")[1]);
                int success = jObj.getInt("success");
                String msg = jObj.getString("message");
                if (success == 1) {
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
            }
            hideDialog();
            // showing the server response in an alert dialog
            //showAlert(result);


            super.onPostExecute(result);
        }

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file, filename, path, false);
        pDialog.setMessage("Uploading...");
        showDialog();
        new UploadFileToServer().execute(imageutils.getPath(uri));
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
                            .placeholder(R.drawable.profile)
                            .into(image);
                    imageUrl = Appconfig.ip + "/admin/uploads/" + imageutils.getfilename_from_path(filepath);
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

    @Override
    protected void onPause() {
        super.onPause();
        hideDialog();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageutils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBaseClick(final int position) {

        if (position == 0) {
            imageutils.imagepicker(1);
            imageutils.setImageAttachmentListener(new Imageutils.ImageAttachmentListener() {
                @Override
                public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
                    String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
                    Base base = new Base();
                    base.setUrl(imageutils.getPath(uri));
                    base.setIsImage("false");
                    if (filename != null) {
                        base.setIsImage("true");
                        imageutils.createImage(file, filename, path, false);
                    }
                    pDialog.setMessage("Uploading...");
                    showDialog();
                    new UploadFileToServerArray().execute(imageutils.getPath(uri));

                }
            });
        } else {
            Intent localIntent = new Intent(OfferUpdate.this, ActivityMediaOnline.class);
            localIntent.putExtra("filePath", bases.get(position).getUrl());
            localIntent.putExtra("isImage", Boolean.parseBoolean(bases.get(position).getIsImage()));
            startActivity(localIntent);
        }

    }

    @Override
    public void onDeleteClick(int position) {
        bases.remove(position);
        attachmentBaseAdapter.notifyData(bases);
    }


    private class UploadFileToServerArray extends AsyncTask<String, Integer, String> {
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
            hideDialog();
            try {
                JSONObject jsonObject = new JSONObject(result.toString());
                if (!jsonObject.getBoolean("error")) {
                    String imageUrl = Appconfig.ip + "/admin/uploads/" + imageutils.getfilename_from_path(filepath);
                    Base base = new Base();
                    base.setUrl(imageUrl);
                    base.setIsImage("true");
                    bases.add(base);
                    attachmentBaseAdapter.notifyDataItem(bases, bases.size() + 1);
                }
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
            }

            // showing the server response in an alert dialog
            //showAlert(result);


            super.onPostExecute(result);
        }

    }

}