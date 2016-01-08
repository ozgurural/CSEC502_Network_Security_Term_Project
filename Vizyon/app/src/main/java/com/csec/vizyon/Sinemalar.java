package com.csec.vizyon;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by burcel on 31/12/15.
 */
public class Sinemalar {

    String url;
    JSONArray sinemalarContent;

    Sinemalar(String url){
        this.url = url;
        sinemalarContent = new JSONArray();
    }

    public JSONArray getSinemalarAsJson(){

        new SinemalarRequest().execute(url);

        return sinemalarContent;
    }


    public class SinemalarRequest extends  AsyncTask<String, Void, String> {
        final String TAGSWARM = "Sinemalar";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            String url = params[0];
            try {
                Document doc = Jsoup.connect(url).get();

                Elements elements = doc.getElementsByClass("img_side_content");

                Log.i(TAGSWARM, String.valueOf(elements.size()));
                for(Element elementRowContent : elements){

                    JSONObject tmpJson = new JSONObject();


                    String pictureUrl = elementRowContent.child(0).child(0).attr("abs:src");
                    String title = elementRowContent.child(1).child(0).child(1).text();
                    String description = elementRowContent.child(1).child(2).text();

                    tmpJson.put("picture", pictureUrl);
                    tmpJson.put("title", title);
                    tmpJson.put("description", description);

                    sinemalarContent.put(tmpJson);

                }
                return sinemalarContent.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.i(TAGSWARM, "onPostExecute:" + result);

        }
    }


}
