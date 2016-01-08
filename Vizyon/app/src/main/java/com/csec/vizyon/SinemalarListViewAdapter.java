package com.csec.vizyon;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by justburcel on 07/01/2016.
 */
public class SinemalarListViewAdapter extends ArrayAdapter<String> {

    private final Activity context;

    ArrayList<String> sinemalarTitle;
    ArrayList<String> sinemalarImg;
    ArrayList<String> sinemalarDesc;

    final String TAG_SINEMALAR_ADAPTER = "ListViewAdapter";

    public SinemalarListViewAdapter(Activity context, ArrayList<String> sinemalarTitle, ArrayList<String> sinemalarImg,  ArrayList<String>sinemalarDesc) {
        super(context, R.layout.sinemalar_listview_item, sinemalarTitle);

        this.context=context;
        this.sinemalarTitle = sinemalarTitle;
        this.sinemalarImg = sinemalarImg;
        this.sinemalarDesc = sinemalarDesc;

        //Log.i(TAG_SINEMALAR_ADAPTER, sinemalarTitle.toString());
    }

    public View getView(int position,View view,ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.sinemalar_listview_item, null,true);

        TextView title = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView desc = (TextView) rowView.findViewById(R.id.description);

        title.setText(sinemalarTitle.get(position));
        desc.setText(sinemalarDesc.get(position));
        UrlImageViewHelper.setUrlDrawable(imageView, sinemalarImg.get(position));

        return rowView;
    };
}
