package pro.network.jvradmin.order;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pro.network.jvradmin.R;
import pro.network.jvradmin.app.AppController;
import pro.network.jvradmin.app.Appconfig;
import pro.network.jvradmin.product.Product;

import static pro.network.jvradmin.app.Appconfig.ORDER_CHANGE_STATUS;
import static pro.network.jvradmin.app.Appconfig.ORDER_GET_ALL;

public class MainActivityOrder extends AppCompatActivity implements OrderAdapter.ContactsAdapterListener, StatusListener {
    private static final String TAG = MainActivityOrder.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Order> orderList;
    private OrderAdapter mAdapter;
    private SearchView searchView;
    ProgressDialog progressDialog;
    private OrderAdapter deliverAdapter;
    private ArrayList<Order> deliveredList;
    private RecyclerView recycler_view_delivered;

    Button loadMore;
    int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainorder);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.order);

        recyclerView = findViewById(R.id.recycler_view);
        orderList = new ArrayList<>();
        mAdapter = new OrderAdapter(MainActivityOrder.this, orderList, this, this);

        recycler_view_delivered = findViewById(R.id.recycler_view_delivered);
        deliveredList = new ArrayList<>();
        deliverAdapter = new OrderAdapter(MainActivityOrder.this, deliveredList, this, this);

        loadMore = findViewById(R.id.loadMore);


        // white background notification bar
        whiteNotificationBar(recyclerView);


        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(addManager1);
        recyclerView.setAdapter(mAdapter);

        recycler_view_delivered.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager deliManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_view_delivered.setLayoutManager(deliManager);
        recycler_view_delivered.setAdapter(deliverAdapter);

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchContacts();
            }
        });


    }


    private void fetchContacts() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                ORDER_GET_ALL + "?offset="+(offset * 10), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        if (offset == 0) {
                            orderList = new ArrayList<>();
                            deliveredList = new ArrayList<>();
                        }
                        offset = offset + 1;
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Order order = new Order();
                            order.setId(jsonObject.getString("id"));
                            order.setPrice(jsonObject.getString("price"));
                            order.setQuantity(jsonObject.getString("quantity"));
                            order.setStatus(jsonObject.getString("status"));
                            order.setItems(jsonObject.getString("items"));
                            order.setName(jsonObject.getString("name"));
                            order.setPhone(jsonObject.getString("phone"));
                            order.setCreatedOn(jsonObject.getString("createdon"));

                            try {
                                String log = jsonObject.getString("items");

                                log = getSpecialRemovedString(log, "description");
                                log = getSpecialRemovedString(log, "model");

                                ObjectMapper mapper = new ObjectMapper();
                                Object listBeans = new Gson().fromJson(log, Object.class);
                                ArrayList<Product> accountList = mapper.convertValue(
                                        listBeans,
                                        new TypeReference<ArrayList<Product>>() {
                                        }
                                );
                                order.setProductBeans(accountList);
                            } catch (Exception e) {
                                order.setProductBeans(new ArrayList<Product>());
                            }

                            if (order.getStatus().equalsIgnoreCase("Delivered")) {
                                deliveredList.add(order);
                            } else {
                                orderList.add(order);
                            }

                        }
                        mAdapter.notifyData(orderList);
                        deliverAdapter.notifyData(deliveredList);
                        getSupportActionBar().setSubtitle(orderList.size()
                                + " Pending / " + (orderList.size() + deliveredList.size()) + "  Orders");

                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }
                hideDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                // Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("offset", offset * 10 + "");
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private String getSpecialRemovedString(String log, String category) {
        String regex = "\"" + category + "\":\"";

        String regexTo = "\",";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(log);

        ArrayList<String> strings = new ArrayList<>();
        while (matcher.find()) {
            Matcher matcherTo = Pattern.compile(regexTo).matcher(log);
            if ((matcherTo.find(matcher.end())) || matcherTo.find()) {
                strings.add(log.substring(matcher.start(), matcherTo.end()));
            }
        }
        for (int k = 0; k < strings.size(); k++) {
            String target = strings.get(k);
            String subtarget = target.substring(target.indexOf("\":\"") + 3, target.indexOf("\","));
            String subtargetNew = subtarget.replaceAll("[^a-zA-Z0-9 ]", "");
            target = target.replace(subtarget, subtargetNew);
            log = log.replace(strings.get(k), target);
        }
        return log;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
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

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
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
    public void onContactSelected(Order contact) {
       /* Intent intent = new Intent(MainActivityOrder.this, ProductUpdate.class);
        intent.putExtra("data", contact);
        startActivity(intent);*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchContacts();

    }

    @Override
    public void onDeliveredClick(String id) {
        statusChange(id);
    }

    @Override
    public void onWhatsAppClick(String phone) {
        try {
            Uri uri = Uri.parse("smsto:91" + phone);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            //  i.putExtra("sms_body", "Hello");
            i.setPackage("com.whatsapp.w4b");
            startActivity(i);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=91" + phone
                    + "&text=" + "Hi"));
            intent.setPackage("com.whatsapp.w4b");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=91" + phone
                    + "&text=" + "Hi"));
            intent.setPackage("com.whatsapp");
            startActivity(intent);
        }
    }

    @Override
    public void onCallClick(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }


    private void statusChange(final String id) {
        String tag_string_req = "req_register";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.PUT,
                ORDER_CHANGE_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        fetchContacts();
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

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
