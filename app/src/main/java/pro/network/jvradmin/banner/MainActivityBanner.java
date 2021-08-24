package pro.network.jvradmin.banner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.network.jvradmin.R;
import pro.network.jvradmin.app.AppController;
import pro.network.jvradmin.app.Appconfig;

import static pro.network.jvradmin.app.Appconfig.BANNERS_GET_ALL;

public class MainActivityBanner extends AppCompatActivity implements BannerClick {
    private static final String TAG = MainActivityBanner.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Banner> bannerList;
    private BannerAdapter mAdapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_banner);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Banners");

        recyclerView = findViewById(R.id.recycler_view);
        bannerList = new ArrayList<>();
        mAdapter = new BannerAdapter(this, bannerList,this);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new (this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        //fetchContacts();


        FloatingActionButton addStock = (FloatingActionButton) findViewById(R.id.addbanner);
        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityBanner.this, BannerRegister.class);
                startActivity(intent);
            }
        });
    }

    /**
     * fetches json by making http calls
     */

    private void fetchContacts() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                BANNERS_GET_ALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        bannerList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Banner banner = new Banner();
                            banner.setId(jsonObject.getString("id"));
                            banner.setBanner(jsonObject.getString("banner"));
                            banner.setDescription(jsonObject.getString("description"));
                            bannerList.add(banner);
                        }
                        mAdapter.notifyData(bannerList);
                        getSupportActionBar().setSubtitle(String.valueOf(bannerList.size()) + "  Nos");

                    } else {
                        Toast.makeText(MainActivityBanner.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityBanner.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityBanner.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
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
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        fetchContacts();

    }

    @Override
    public void onDeleteClick(int position) {
        deleteFile(position);

    }
    private void deleteFile(final int position) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Fetching ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.DELETE,
                Appconfig.BANNERS_DELETE+"?id="+bannerList.get(position).getId(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getBoolean("success")) {
                        bannerList.remove(position);
                        mAdapter.notifyData(bannerList);
                    }
                    Toast.makeText(MainActivityBanner.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityBanner.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
              Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityBanner.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("id", bannerList.get(position).id);
                return localHashMap;
            }

        };
        strReq.setRetryPolicy(Appconfig.getPolicy());

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
