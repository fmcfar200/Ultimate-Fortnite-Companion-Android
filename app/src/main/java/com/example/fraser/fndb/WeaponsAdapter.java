package com.example.fraser.fndb;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class WeaponsAdapter extends BaseAdapter
{
    private Context mContext;
    private int layoutId;
    private List<Weapon> data;

    public WeaponsAdapter(Context c, int theLayoutID, List<Weapon> theData)
    {

        this.mContext = c;
        this.layoutId = theLayoutID;
        this.data = theData;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Weapon getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = View.inflate(mContext,layoutId,null);
        ImageView imageView = view.findViewById(R.id.weaponImage);
        Picasso.with(mContext).load(data.get(position).getImageURL()).into(imageView);



        view.setTag(data.get(position));
        return view;
    }

}