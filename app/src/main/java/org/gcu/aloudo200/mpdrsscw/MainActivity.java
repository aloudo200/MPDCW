package org.gcu.aloudo200.mpdrsscw;
/* <!-- Created by Andrew Loudon S1426140 -->*/

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.BaseAdapter;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener, OnMapReadyCallback
{
    private String separator = System.getProperty("line.separator");

    private String url1="http://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String url2="http://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String url3="http://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private EditText inputFilter;
    private TextView s2title;
    private TextView s2desc;
    private TextView s2date;
    private ViewSwitcher vwSwitch;
    private Button currentInc;
    private Button plannedRd;
    private GoogleMap mMap;
    private FeedChannelItem currentItem;
    private String result = "";
    private ListView rssOutList;
    private LinkedList<FeedChannelItem> rssFeedTop = new LinkedList<FeedChannelItem>();
    ArrayList<String> listItemTitles = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        BaseAdapter listAdapter = new ArrayAdapter<String> (this, R.layout.custom_textview_item, listItemTitles);
       // Override the getView part of this adapter as we want to customise how items are shown



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vwSwitch = (ViewSwitcher)findViewById(R.id.vwSwitch);
        if (vwSwitch == null)
        {
            Toast.makeText(getApplicationContext(), "Null ViewSwitcher", Toast.LENGTH_LONG);
            Log.e(getPackageName(), "null pointer");
        }


        s2title = (TextView)findViewById(R.id.s2title);
        s2desc = (TextView)findViewById(R.id.s2desc);
        s2date = (TextView)findViewById(R.id.s2date);
        inputFilter = (EditText)findViewById(R.id.inputFilter);
        currentInc = (Button)findViewById(R.id.currentInc);
        plannedRd = (Button)findViewById(R.id.plannedRd);
        plannedRd.setOnClickListener(this);
        currentInc.setOnClickListener(this);

        rssOutList = (ListView)findViewById(R.id.incidentList);

        rssOutList.setOnItemClickListener(this);
        rssOutList.setAdapter(listAdapter);
        rssOutList.forceLayout();



    } // End of onCreate

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ViewGroup vg = findViewById(R.id.mainScreen);
        vg.invalidate();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Double Lat = 0.0;
        Double Lng = 0.0;
        String markerTitle = "Default Marker";

        //break up the left and right sides of the georss String relative to the item
        //selected in the list, setting them to Latitude and Longitude respectively

            Lat = Double.parseDouble(currentItem.getGeorss().substring(0, currentItem.getGeorss().indexOf(" ")));
            Lng = Double.parseDouble(currentItem.getGeorss().substring(currentItem.getGeorss().indexOf(" "), currentItem.getGeorss().length()));
            markerTitle = currentItem.getItemDesc();
            LatLng selected = new LatLng(Lat, Lng);
            mMap.addMarker(new MarkerOptions().position(selected).title(markerTitle));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(selected));
            Log.e("Map", "onMapReady called");

        }



  /*  public View getViewInListByPos(int pos, ListView lv) {

        final int firstItemPos = lv.getFirstVisiblePosition();
        final int lastItemPos = firstItemPos + lv.getChildCount() - 1;

        if (pos < firstItemPos || pos > lastItemPos) {
          // TextView tv = (TextView) lv.getAdapter().getView(pos, lv.getChildAt(pos), lv);
          //  tv.setTextColor(Color.RED);
            return lv.getAdapter().getView(pos, lv.getChildAt(pos), lv); }
        else {

            final int childIndex = pos - firstItemPos;
            //TextView tv = (TextView) lv.getChildAt(childIndex);
           // tv.setTextColor(Color.RED);
            return lv.getChildAt(childIndex);
        }
    } */

    @Override
    //handling items clicked on the ListView
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
    {
        /* all of this method is only possible AFTER the required data is parsed and available
        /* achieved by using the previously created rssFeedTop LinkedList containing all the
        /* FeedChannelItem objects


        /* when a user clicks on a title, i.e an Incident or Planned Roadworks,
        /* find an item matching that title within the list of rss output (rssFeedTop)
        so that we can individually pull in data from them */


        for (FeedChannelItem item : rssFeedTop) {
            Log.e("Info", "Looking for: " + parent.getItemAtPosition(pos).toString());

             //find out the title of the item the user clicked on in the ListView

            String itemCheck = parent.getItemAtPosition(pos).toString();

            if (item.getItemTitle() == itemCheck) {
                // when we find it, set the description to match that of the reported incident
                Log.e("Success", "Found Matching Item");
                // and then set the values of the items to the appropriate TextViews
                currentItem = item;
                s2title.setText(item.getItemTitle() + separator);
                s2desc.setText(separator + item.getItemDesc());
                if (!(item.getItemDesc().contains("Start Date"))) {
                    s2date.setText(separator + "Last updated on: " + item.getPubdate());
                }
                else s2date.setText("");
            }

        }
        /*because we are using another LinearLayout to hold the s2title, s2desc and s2date TextViews,
         switch to that view so that the user can see the output*/
        vwSwitch.showNext();
        Log.e("Map should be ready", "Below this line");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    //The below method improves the search functionality in that the user does not need to
    //concern themselves with the specificity of their search term

    public String capitaliseStart(String caps)
    {
        Character c1 = caps.charAt(0);

        String c2 = c1.toString().toUpperCase();

        Character c3 = c2.charAt(0);

        caps = caps.replace(c1, c3);

        return caps;
    }

    public void onBackPressed()
    {
        if (vwSwitch.getNextView().getId() == R.id.mainScreen) {
            vwSwitch.showPrevious();
            mMap.clear();
        }
        else
        {
            finish();
        }
    }

    public void onClick(View aview)
    {
        startProgress(aview);
    }

    public void startProgress(View aview) {

        if (aview.getId() == (R.id.currentInc)) { // Current Incidents
            new Thread(new Task(url1)).start();
            rssOutList.clearChoices();
        } else if (aview.getId() == R.id.plannedRd) {
            new Thread(new Task(url3)).start();
            rssOutList.clearChoices();
        }
    }//

    private LinkedList<FeedChannelItem> parseXML(String inputData, String filter)
    {
        FeedChannelItem feed = null;
       // LinkedList <FeedChannelItem> rssFeed = null;
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));
            int eventType = xpp.getEventType();

            //if the container for all the RSS feed output is not empty, clear it

            if (!rssFeedTop.isEmpty())
            {
                rssFeedTop.clear();
            }
            while (eventType !=XmlPullParser.END_DOCUMENT)
            {
                //Found the start tag
                if(eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("channel")) {
                        //rssFeedTop is a global variable to allow access to it from within this
                        //class at any time, such as when clicking on an item in the ListView
                        rssFeedTop = new LinkedList<FeedChannelItem>();
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        String temp = xpp.nextText();
                        Log.e("MyTag", "Channel Start Tag Found");
                        feed = new FeedChannelItem();
                        feed.setItemTitle(temp);
                    } else if (xpp.getName().equalsIgnoreCase("description")) {
                        String temp = xpp.nextText();
                        Log.e("MyTag", "Showing: +" + temp);
                        //bit of formatting as needed
                        temp = temp.replaceAll("<br />", " " + separator + separator);
                        temp = temp.replaceAll(":", ": ");
                        temp = temp.replaceAll("00: 00", "00:00");
                        temp = temp.replaceAll("Traffic Management", "\n\nTraffic Management");
                        temp = temp.replaceAll("Diversion Information", "\n\nDiversion Information");
                        feed.setItemDesc(temp);
                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                        String temp = xpp.nextText();
                        Log.e("MyTag", "Link to channel: " + temp);
                        feed.setItemLink(temp);
                    } else if (xpp.getName().equalsIgnoreCase("generator")) {
                        String temp = xpp.nextText();
                        Log.e("MyTag", "Generated by: " + temp);
                        feed.setGenerator(temp);
//
                    } else if (xpp.getName().equalsIgnoreCase("georss:point")) {
                        //use this for geolocation?
                        String temp = xpp.nextText();
                        Log.e("MyTag", "Location info: " + temp);
                        feed.setGeorss(temp);
                    } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                        String temp = xpp.nextText();
                        Log.e("MyTag", "Updated on: " + temp);
                        feed.setPubdate(temp);
                    }

                }
                else
                if(eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        Log.e("MyTag", "Full channel output is" + feed.toString());

                        /*when there are no more incidents or roadworks left to parse, add this
                        newly created and updated FeedChannelItem object to the global variable
                        that is holding all of these */

                        /* first check if the title or description of the incident/planned roadworks matches
                        what the user searches for in the EditText element on the MainView
                         */

                        //handle all potential cases to make it case insensitive for a better user
                        //experience
                        if(feed.getItemTitle().contains(filter.toUpperCase()) || feed.getItemDesc().contains(filter.toUpperCase()) || feed.getItemTitle().contains(filter) ||
                           feed.getItemDesc().contains(filter) || feed.getItemTitle().contains(capitaliseStart(filter)) || feed.getItemDesc().contains(capitaliseStart(filter)))
                        {
                            //if there is a match, then add this as it is one of the items we want
                            rssFeedTop.add(feed);
                        }

                    } else if (xpp.getName().equalsIgnoreCase("channel")) {
                        int size;
                        size = rssFeedTop.size();
                        Log.e("MyTag", "Number of feeds: " + size);
                    }
                }
                eventType = xpp.next();
            } // end of while loop
            //return rssFeed
        }
        catch (XmlPullParserException ael)
        {
            Log.e("MyTag", "Error during parse" + ael.toString());
        }
        catch (IOException ael)
        {
            Log.e("MyTag", "IO Error encountered on parse attempt");
        }

        Log.e("MyTag", "End of feed");

        return rssFeedTop;
    }



    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    class Task implements Runnable
    {
        private String url;


        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run() {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                //
                // Throw away the first 2 header lines before parsing
                //
                //
                //
                if (!result.equals("")) {
                    result = "";
                }
                while ((inputLine = in.readLine()) != null) {

                    result = result + inputLine;
                    Log.e("MyTag", inputLine);

                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }

            //
            //
            // Parse the resulting XML into a linked list

            final LinkedList<FeedChannelItem> rssOutput = parseXML(result, inputFilter.getText().toString());

            // Now prepare the items we will add to the list of titles, obtained with getItemTitle()
            // from the OTHER FeedChannelItem list (currentIncidents) created during parsing through
            // a for loop use this list of titles to enable us to loop over each FeedChannelItem in the LinkedList of them when clicking on one of these titles in the ListView

            MainActivity.this.runOnUiThread(new Runnable() {

                public void run() {
                    Log.d("UI thread", "I am the UI thread");

                    //If there are incidents found by the parser, do something
                    if (rssOutput != null) {
                        Log.e("MyTag", "Incidents reported...");
                        if (!listItemTitles.isEmpty()) {
                            listItemTitles.clear();

                        }

                        //for all incidents found, get the title of that item and add the returned string to the currently empty list of titles
                        for (FeedChannelItem in : rssOutput) {
                            listItemTitles.add(in.getItemTitle());
                            //urlInput.append(in.toString());
                        }
                        // tell the app that the adapter used to handle the list of titles within the ListView has changed
                        // this is what makes it update after you press any of the buttons so that the ListView contents will change when needed
                        ;

                        //((BaseAdapter)rssOutList.getAdapter()).notifyDataSetChanged();

                        ((BaseAdapter) rssOutList.getAdapter()).notifyDataSetChanged();

                        /*********The below code was used to attempt to set the text colour of each
                        item in the list depending on the length of the works. The logic here
                        seems to be at fault as it was making each item green, and also not
                        loading and applying this to each item in the list first time (too
                        much work for one thread to handle perhaps). For the time being, all of this
                        has been commented out but feel free to uncomment
                        it to show what I was trying to achieve \\\\

                        for (int i = 0; i < rssOutList.getCount(); i++) {

                            TextView tv = (TextView) getViewInListByPos(i, rssOutList);

                         //get all textviews from the list. Not to be confused with getting the
                         data held in the text views - we want the actual TextView objects android
                         created for each ListItemTitle loaded into it

                                int workDuration = 0;
                                int start = 0;
                                int end = 0;
                                {

                                    for (FeedChannelItem compare : rssFeedTop) {

                                        if (compare.getItemDesc().contains("Start Date:")) {


                         //probably a very expensive operation below, but given the format of the
                         rss feed provided by Traffic Scotland, this seemed to be the only way to
                         do it but I am sure it probably isn't


                                            String start2 = compare.getItemDesc().substring(compare.getItemDesc().indexOf("day,") + 5, compare.getItemDesc().indexOf("day,") + 7);
                                            String end2 = compare.getItemDesc().substring(compare.getItemDesc().lastIndexOf("day,") + 5, compare.getItemDesc().lastIndexOf("day,") + 7);

                                            if (start2.startsWith("0")) {
                                                start2.replaceAll("0", "");
                                            }
                                            if (end2.startsWith("0")) ;
                                            {
                                                end2.replaceAll("0", "");
                                            }

                                            start = Integer.parseInt(start2);
                                            end = Integer.parseInt(end2);

                                            if (start < end) {
                                                workDuration = end - start;
                                            } else if (start >= end) {
                                                workDuration = start - end;
                                            }

                                            if (workDuration >= 10) {
                         //Red text colour for works taking longer than 10 days to complete
                                                tv.setTextColor(Color.RED);

                                            } else if (workDuration >=5 & workDuration < 10) {
                                                tv.setTextColor(Color.YELLOW);
                         //Yellow text colour for works taking between 5 and 10 days to complete

                                            } else if (workDuration < 5) {
                                                tv.setTextColor(Color.GREEN);
                         //Green text colour for works taking less than 5 days to complete

                                            }
                         //Default text colour
                                            tv.setTextColor(Color.BLACK);
                                        }
                                    }
                                }
                         //Refresh the view to update the styles
                            ViewGroup vg = findViewById(R.id.mainScreen);
                            vg.invalidate();
                            setContentView(R.layout.activity_main);
                            }
                            */


                    } else {
                        Log.e("MyTag", "No incidents reported");
                    }



                }


            });
        }

        }


    }







