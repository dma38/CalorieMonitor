package com.example.caloriemonitor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RSSActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private String sfeed_url;
    private URL feed_URL;
    private BufferedReader bufferedReader;
    private ArrayList<String> title;
    private ArrayList<String> pubDate;
    private ArrayList<String> description;
    private ArrayList<String> link;
    StringBuilder stringBuilderTitle;
    StringBuilder stringBuilderPubDate;
    StringBuilder stringBuilderDescription;
    StringBuilder stringBuilderLink;
    TextView txtFeed;

    private final int STANDARD_REQUEST_CODE = 0;

    private ArrayList<Post> post;
    private ListView listView;
    private ProgressDialog progressDialog = null;
    private PostAdapter postAdapter;
    private int fontSize = 20;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);
        sharedPreferences = this.getSharedPreferences("generalPrefs", this.MODE_PRIVATE);

    }

    private void makePost() {
        Log.d("Di", "-----------------------Make post-----------------------");
        post = new ArrayList<Post>();

        for (int i =1;i<title.size()-1;i++)
        {
            post.add(new Post(title.get(i), pubDate.get(i-1), description.get(i), link.get(i)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.sciencedaily)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("feed_URL","https://rss.sciencedaily.com/health_medicine/fitness.xml");
            editor.putString("feed_from","Science Daily");
            editor.commit();

            this.onResume();
        }
        else if(id == R.id.outside_feed)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("feed_URL","https://www.outsideonline.com/rss/fitness/rss.xml");
            editor.putString("feed_from","Outside Magazine: Fitness");
            editor.commit();
            this.onResume();
        }
        else if(id == R.id.refresh)
        {
            this.onResume();
        }

        return super.onOptionsItemSelected(item);
    }

    private class PostAdapter extends ArrayAdapter<Post> {

        private ArrayList<Post> items;

        public PostAdapter(Context context, int textViewResourceId, ArrayList<Post> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        //This method is called once for every item in the ArrayList as the list is loaded.
        //It returns a View -- a list item in the ListView -- for each item in the ArrayList
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            fontSize = sharedPreferences.getInt("fontSize", -1);

            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.rss_list_item, null);
            }
            Post o = items.get(position);
            String field = sharedPreferences.getString("field","PubDate");

            if (o != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                if (tt != null) {
                    tt.setText(o.getTitle());
                    if(fontSize != -1)
                    {
                        tt.setTextSize(fontSize);
                    }
                    else
                    {
                        tt.setTextSize(20);
                    }


                }
                if(bt != null){
                    switch (field)
                    {
                        case "PubDate":
                            bt.setText(o.getPubDate());
                            break;
                        case "Description":
                            bt.setText(Html.fromHtml(o.getDescription()));
                            break;
                        case "Link":
                            bt.setText(o.getLink());
                            break;

                    }


                }

            }

            //to be changed  #FFF1F1 #ECF9FF #A8FFC2  defL #FAFAFA
            //listView.setBackgroundColor(Color.parseColor("#FFF1F1"));
            String color = sharedPreferences.getString("bgColor", "Default");
            switch (color)
            {
                case "Pink":
                    listView.setBackgroundColor(Color.parseColor("#FFF1F1"));
                    break;
                case "Green":
                    listView.setBackgroundColor(Color.parseColor("#A8FFC2"));
                    break;
                case "Blue":
                    listView.setBackgroundColor(Color.parseColor("#ECF9FF"));
                    break;
                case "Default":
                    listView.setBackgroundColor(Color.parseColor("#FAFAFA"));
                    break;
            }
            return v;
        }
    }


    class Post {
        private String title;
        private String pubDate;
        private String link;
        private String description;
        public Post(String title, String pubDate, String description, String link) {
            this.title = title;
            this.pubDate = pubDate;
            this.description = description;
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public String getPubDate() {
            return pubDate;
        }

        public String getLink() {
            return link;
        }

        public String getDescription() {
            return description;
        }
    }

    class MyHandler extends DefaultHandler {
        private boolean inTitle, inPubDate, inLink, inDescription;


        public MyHandler(){
            title = new ArrayList<String>();
            pubDate = new ArrayList<String>();
            description = new ArrayList<String>();
            link = new ArrayList<String>();
        }
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
//            Log.d("Di", "Start element" + qName);

            //keep track of what element we are in
            if(qName.equals("title"))
            {
                stringBuilderTitle = new StringBuilder(50);
                inTitle = true;
            }
            if(qName.equals("pubDate"))
            {
                stringBuilderPubDate = new StringBuilder();
                inPubDate = true;
            }
            if(qName.equals("link"))
            {
                stringBuilderLink = new StringBuilder();
                inLink = true;
            }
            if(qName.equals("description"))
            {
                stringBuilderDescription = new StringBuilder();
                inDescription = true;
            }
        }
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
//            Log.d("Di", "End element" + qName);
            if(qName.equals("title"))
            {
                inTitle = false;

                title.add(stringBuilderTitle.toString());
            }
            if(qName.equals("pubDate"))
            {
                inPubDate = false;

                pubDate.add(stringBuilderPubDate.toString());
            }
            if(qName.equals("description"))
            {
                inDescription = false;

                description.add(stringBuilderDescription.toString());
            }
            if(qName.equals("link"))
            {
                inLink = false;

                link.add(stringBuilderLink.toString());
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if(inTitle){
                stringBuilderTitle.append(ch, start, length);

            }
            if(inPubDate)
            {
                stringBuilderPubDate.append(ch, start, length);
            }
            if(inDescription)
            {
                stringBuilderDescription.append(ch, start, length);
            }
            if(inLink)
            {
                stringBuilderLink.append(ch, start, length);
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            Log.d("Di", "endDocument");
            Log.d("Di", "Contents: ");
//            for (int i =1;i<title.size();i++)
//            {
//                Log.d("Di", i + " " + title.get(i) + pubDate.get(i-1));
//            }
        }
    }

    class AsyncTest extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            Log.d("Di","doInBackground");

            SAXParserFactory spf = SAXParserFactory.newInstance();
            try {
                feed_URL = new URL(sfeed_url);

                SAXParser saxParser = spf.newSAXParser();

                MyHandler myHandler = new MyHandler();

                bufferedReader = new BufferedReader(
                        new InputStreamReader(feed_URL.openStream()));

                saxParser.parse(new InputSource(bufferedReader),myHandler);



            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            makePost();
                postAdapter = new PostAdapter(RSSActivity.this, R.layout.list_item, post);

                listView = (ListView) findViewById(R.id.nice_listview);
                listView.setAdapter(postAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(RSSActivity.this, RSSDetailsActivity.class);
                        intent.putExtra("selectedItem", i);
                        intent.putExtra("title", title.get(i + 1));
                        intent.putExtra("pubDate", pubDate.get(i));
                        intent.putExtra("link", link.get(i + 1));
                        intent.putExtra("description", description.get(i + 1));
                        startActivity(intent);
//              Toast.makeText(MainActivity.this, "Index: " + i, Toast.LENGTH_SHORT).show();
                    }
                });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        txtFeed = (TextView)findViewById(R.id.txtFeed);
        sfeed_url = sharedPreferences.getString("feed_URL","https://rss.sciencedaily.com/health_medicine/fitness.xml");
        txtFeed.setText(sharedPreferences.getString("feed_from","Science Daily"));
        txtFeed.setTextColor(Color.parseColor("#000033"));
        txtFeed.setBackgroundColor(Color.parseColor("#99CCFF"));
        AsyncTest asyncTest = new AsyncTest();
        asyncTest.execute();




    }





    public void startSettingsActivity() {
     //   Intent intent = new Intent(this, SettingsActivity.class);

//        intent.putExtra("name","Di");
//        intent.putExtra("age",26);

     //   startActivityForResult(intent,STANDARD_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            int preFontSize = data.getIntExtra("fontSize",-1);
            if(preFontSize == -1)
            {
                onResume();
            }
            else
            {
                fontSize = preFontSize;
                onResume();
            }
        }
        else
        {
            Toast.makeText(RSSActivity.this,"Result Cancelled", Toast.LENGTH_LONG).show();
        }
    }
}
