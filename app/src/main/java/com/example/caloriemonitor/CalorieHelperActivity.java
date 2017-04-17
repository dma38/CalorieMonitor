package com.example.caloriemonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class CalorieHelperActivity extends AppCompatActivity {



    private SearchAdapter searchAdapter;
    private ArrayList<SearchItem> searchArrayList;
    private ArrayList<String> searchIdString;
    private ListView listView;
    TextView txtSearchLabel;
    Button btnSearchButton;
    Intent intent;
    EditText etKeyword;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_helper);
        btnSearchButton = (Button)findViewById(R.id.btnSearchButton);
        txtSearchLabel = (TextView)findViewById(R.id.txtSearchLabel);
        etKeyword = (EditText)findViewById(R.id.etSeachName);
        listView = (ListView)findViewById(R.id.listview_search);
        Intent i = getIntent();
        if(i.getStringExtra("keyword")!=null) {
            etKeyword.setText(i.getStringExtra("keyword"));
        }
//        btnBackToMain_History = (Button)findViewById(R.id.btnBackToMain_history);
//
//        btnBackToMain_History.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MealHistoryActivity.this, MainActivity.class);
//                startActivity(i);
//          }
//        });

        btnSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etKeyword.getText().toString().matches("")) {
                    Toast.makeText(CalorieHelperActivity.this, "Please enter a keyword", Toast.LENGTH_SHORT).show();
                } else {
                    String keyword = etKeyword.getText().toString();
                    String query="cheese";
                    try {
                        query = URLEncoder.encode(keyword, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String url = "https://api.nutritionix.com/v1_1/search/" + query +"?fields=item_name%2Citem_id%2Cbrand_name%2Cnf_calories%2Cnf_total_fat&appId=6291d52f&appKey=27a02c4a97bdd2c73e84fca371db9418";

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    JSONArray jsonMainNode = response.optJSONArray("hits");
                                    for (int i = 0; i < jsonMainNode.length(); i++) {
                                        JSONObject jsonHitsNode = null;
                                        JSONObject jsonFieldsNode = null;
                                        try {
                                            jsonHitsNode = jsonMainNode.getJSONObject(i);
                                            jsonFieldsNode = jsonHitsNode.optJSONObject("fields");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        String item_name = jsonFieldsNode.optString("item_name");
                                        float item_calories = Float.parseFloat(jsonFieldsNode.optString("nf_calories"));

                                        searchArrayList.add(new SearchItem(item_name, item_calories));

                                        searchAdapter = new SearchAdapter(CalorieHelperActivity.this, R.layout.list_item_search, searchArrayList);

                                        listView.setAdapter(searchAdapter);

                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                intent = new Intent(CalorieHelperActivity.this, AddItemActivity.class);

                                                intent.putExtra("calories", String.valueOf(searchArrayList.get(i).getItemCalories()));
                                                intent.putExtra("name", searchArrayList.get(i).getItemName());
                                                //Toast.makeText(CalorieHelperActivity.this, searchArrayList.get(i).getItemName() + ", " + searchArrayList.get(i).getItemCalories(), Toast.LENGTH_SHORT).show();
                                                //startActivity(intent);
                                                onBackPressed();
                                            }
                                        });
                                    }

                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub

                                }
                            });


// Access the RequestQueue through your singleton class.
                    MySingleton.getInstance(CalorieHelperActivity.this).addToRequestQueue(jsObjRequest);


                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(intent == null)
        {
            intent = new Intent(CalorieHelperActivity.this, AddItemActivity.class);
        }

        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        searchArrayList = new ArrayList<SearchItem>();


            }






    class SearchItem {
        private String itemName;
        private float itemCalories;

        public SearchItem(String itemName, float itemCalories) {
            this.itemName = itemName;
            this.itemCalories = itemCalories;
        }

        public String getItemName() {
            return itemName;
        }

        public float getItemCalories() {
            return itemCalories;
        }

    }

     class SearchAdapter extends ArrayAdapter<SearchItem> {

        private ArrayList<SearchItem> items;

        public SearchAdapter(Context context, int textViewResourceId, ArrayList<SearchItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_search, null);
            }
            SearchItem o = items.get(position);

            if (o != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                if (tt != null) {
                    tt.setText(o.getItemName());
                }
                if (bt != null) {
                    bt.setText("Calories: " + String.valueOf(o.getItemCalories()));
                }
            }
            return v;
        }
    }


}

class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private MySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}



