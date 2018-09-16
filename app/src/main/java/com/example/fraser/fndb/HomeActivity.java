package com.example.fraser.fndb;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.util.JsonReader;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    Button skinBtn;
    Button testButton;
    TextView timerTextView;

    GridView itemShopGrid;
    List<ShopItem> shopItems = new ArrayList<>();
    ShopGridAdapter adapter;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.nav_open, R.string.nav_closed)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerView.bringToFront();
            }
        };



        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        skinBtn = findViewById(R.id.skinButton);
        skinBtn.setOnClickListener(this);

        testButton = findViewById(R.id.statsButton);
        testButton.setOnClickListener(this);

        timerTextView = findViewById(R.id.shopTimerText);

        itemShopGrid = findViewById(R.id.itemShopGrid);

        String endTimeString = "01:00:00";
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND, 0);
        Date tomorrow = calendar.getTime();

        long diffInMS = tomorrow.getTime() - currentDate.getTime();

        MyCount counter = new MyCount(diffInMS,1000);
        counter.start();


        ShopFetch fetch = new ShopFetch();
        fetch.execute();

    }


    @Override
    public void onClick(View v)
    {
        if (v ==skinBtn)
        {
            StartActivity(SeasonSelectActivity.class);
        }
    }

    void StartActivity(Class theclass)
    {
        Intent i = new Intent(getApplicationContext(), theclass);
        startActivity(i);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.drHome)
        {

        }
        else if (id == R.id.drSkins)
        {
            StartActivity(SeasonSelectActivity.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class MyCount extends CountDownTimer
    {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            long millis = millisUntilFinished;
            long diffInHours = TimeUnit.MILLISECONDS.toHours(millis);
            millis -= TimeUnit.HOURS.toMillis(diffInHours);

            long diffInMins = TimeUnit.MILLISECONDS.toMinutes(millis);
            millis -= TimeUnit.MINUTES.toMillis(diffInMins);

            long diffInSecs = TimeUnit.MILLISECONDS.toSeconds(millis);
            //millis -= TimeUnit.SECONDS.toMillis(diffInSecs);


            timerTextView.setText(String.valueOf(diffInHours) + ":" +
            String.valueOf(diffInMins) + ":" + String.valueOf(diffInSecs));
        }

        @Override
        public void onFinish()
        {
        }
    }


    class ShopFetch extends AsyncTask<Void, Void, List<ShopItem>>{


        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(HomeActivity.this);
            dialog.setMessage("Getting Ready...");
            dialog.show();
        }

        @Override
        protected List<ShopItem> doInBackground(Void... voids)
        {
            shopItems = new ArrayList<>();
            List<ShopItem> theShopItems = new ArrayList<>();

            try {
                URL endpoint = new URL("https://api.fortnitetracker.com/v1/store");
                HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
                connection.setRequestProperty("TRN-Api-Key", "246a06d4-9ecc-443f-bd96-67e18bb94e4d");

                if (connection.getResponseCode() == 200)
                {

                    InputStream responseBody = connection.getInputStream();
                    BufferedReader r = new BufferedReader(new InputStreamReader(responseBody));
                    StringBuilder jsonString = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        jsonString.append(line).append('\n');
                    }

                    JSONArray array = new JSONArray(jsonString.toString());
                    for (int i = 0; i < array.length(); i ++)
                    {
                        JSONObject object = array.getJSONObject(i);
                        String imageURL = object.get("imageUrl").toString();
                        String name = object.get("name").toString();
                        String rarity = object.get("rarity").toString();
                        String storeCategory = object.get("storeCategory").toString();
                        String vBucks = object.get("vBucks").toString();
                        ShopItem item = new ShopItem(imageURL,name,rarity,storeCategory,vBucks);
                        shopItems.add(item);


                    }
                }
                else
                {
                    Log.e("Response", "Error");

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return shopItems;
        }


        @Override
        protected void onPostExecute(List<ShopItem> shopItems) {
            adapter = new ShopGridAdapter(getApplicationContext(),R.layout.grid_list_item,shopItems);
            itemShopGrid.setAdapter(adapter);
            dialog.dismiss();
        }
    }
}
