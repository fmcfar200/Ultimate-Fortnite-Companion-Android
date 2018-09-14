package com.example.fraser.fndb;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SeasonSelectActivity extends AppCompatActivity implements View.OnClickListener
{


    private Button s1Button, s2Button, s3Button, s4Button, s5Button, battlePassButton,
    itemShopButton,miscButton,uncommonButton, rareButton, epicButton, legendaryButton;

    private ViewFlipper layoutFlipper;
    private Toolbar toolbar;

    private int season = 1;

    public enum SearchType
    {
        BP,UNCOMMON, RARE, EPIC, LEGENDARY
    }
    private SearchType searchType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);

        toolbar = findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Battle Pass Skins");

        layoutFlipper = findViewById(R.id.viewFlipper);
        s1Button = findViewById(R.id.s1Skins);
        s2Button = findViewById(R.id.s2Skins);
        s3Button = findViewById(R.id.s3Skins);
        s4Button = findViewById(R.id.s4Skins);
        s5Button = findViewById(R.id.s5Skins);
        battlePassButton = findViewById(R.id.battlePassButton);
        itemShopButton = findViewById(R.id.shopSkins);
        miscButton = findViewById(R.id.miscSkins);

        uncommonButton = findViewById(R.id.uncommonSkins);
        rareButton = findViewById(R.id.rareSkins);
        epicButton = findViewById(R.id.epicSkins);
        legendaryButton = findViewById(R.id.legendarySkins);

        s1Button.setOnClickListener(this);
        s2Button.setOnClickListener(this);
        s3Button.setOnClickListener(this);
        s4Button.setOnClickListener(this);
        s5Button.setOnClickListener(this);
        battlePassButton.setOnClickListener(this);
        itemShopButton.setOnClickListener(this);
        miscButton.setOnClickListener(this);

        uncommonButton.setOnClickListener(this);
        rareButton.setOnClickListener(this);
        epicButton.setOnClickListener(this);
        legendaryButton.setOnClickListener(this);


        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Skins");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutFlipper.getCurrentView() == layoutFlipper.getChildAt(0)) {
                        finish();
                    }
                    else {
                        layoutFlipper.setDisplayedChild(0);
                    }
                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.toolbarSearch));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot typeSnap: dataSnapshot.getChildren())
                        {
                            for (DataSnapshot objSnap: typeSnap.getChildren())
                            {
                                for (DataSnapshot item: objSnap.getChildren())
                                {
                                    if (item.child("name").getValue().equals(query))
                                    {
                                        String id = String.valueOf(item.child("id").getValue());
                                        String name = String.valueOf(item.child("name").getValue());
                                        String rarity = String.valueOf(item.child("rarity").getValue());
                                        String imageID = String.valueOf(item.child("imageId").getValue());
                                        //int iImageID = Integer.parseInt(imageID);

                                        Skin theSkin = new Skin(id,name,rarity,imageID);

                                        if (typeSnap.getKey().contains("SP"))
                                        {
                                            searchType = SearchType.BP;
                                            String collectionString = objSnap.getKey().toLowerCase();
                                            String[] array1 = collectionString.split("_");
                                            String[] array2 = array1[1].split("s");
                                            season = Integer.parseInt(array2[1]);

                                            StartDetail(theSkin, season, searchType);
                                        }
                                        else if (typeSnap.getKey().contains("IS"))
                                        {
                                            season = 0;
                                            searchType = SearchType.valueOf(rarity.toUpperCase());

                                            StartDetail(theSkin, season, searchType);

                                        }

                                    }

                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public void onClick(View v)
    {
        searchType = SearchType.BP;
        if (v==battlePassButton)
        {
            layoutFlipper.setDisplayedChild(1);
        }
        else if (v == itemShopButton){
            layoutFlipper.setDisplayedChild(2);
        }
        else if(v==s1Button) {
            season = 1;
            StartListActivity(season, searchType);
        }
        else if (v == s2Button) {
            season = 2;
            StartListActivity(season, searchType);
        }
        else if (v == s3Button) {
            season = 3;
            StartListActivity(season, searchType);
        }
        else if (v == s4Button) {
            season = 4;
            StartListActivity(season,searchType);
        }
        else if (v == s5Button) {
            season = 5;
            StartListActivity(season,searchType);
        }

        else if(v == uncommonButton)
        {
            season = 0;
            searchType = SearchType.UNCOMMON;
            StartListActivity(season,searchType);
        }
        else if(v == rareButton)
        {
            season = 0;
            searchType = SearchType.RARE;
            StartListActivity(season,searchType);
        }
        else if(v == epicButton)
        {
            season = 0;
            searchType = SearchType.EPIC;
            StartListActivity(season,searchType);
        }
        else if(v == legendaryButton)
        {
            season = 0;
            searchType = SearchType.LEGENDARY;
            StartListActivity(season,searchType);
        }

    }

    private void StartListActivity(int seasonNo, SearchType type)
    {
        Intent intent = new Intent(getApplicationContext(),SkinActivity.class);
        intent.putExtra("seasonNo", seasonNo);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    private void StartDetail(Skin skin, int seasonNo, SearchType type)
    {
        Intent intent = new Intent(getApplicationContext(),DetailActivity.class);
        intent.putExtra("seasonNo", seasonNo);
        intent.putExtra("type", type);
        intent.putExtra("Skin", skin);
        startActivity(intent);
    }

}
