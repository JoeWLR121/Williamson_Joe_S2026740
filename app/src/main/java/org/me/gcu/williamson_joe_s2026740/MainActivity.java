package org.me.gcu.williamson_joe_s2026740;

/*Joe Williamson, S2026740*/
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.BufferedReader;
import android.content.DialogInterface;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private EditText searchInput;
    private ListView listView;
    private TextView listItems;
    private DatePicker datePick;
    private Button startButtonPlan, startButtonCurrent, startButtonRoad, searchButton, dateButton, clearButton;
    private String result1 = "";
    private ArrayList<IncidentClass> incidentArray = new ArrayList<>();


    private String urlSource2 = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String urlSource3 = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private int itemTagFound = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("MyTag", "in onCreate");
        Log.e("MyTag", "working here");

        // Set up the raw links to the graphical components
        startButtonPlan = (Button) findViewById(R.id.startButtonPlan);
        startButtonCurrent = (Button) findViewById(R.id.startButtonCurrent);
        startButtonRoad = (Button) findViewById(R.id.startButtonRoad);
        clearButton = (Button) findViewById(R.id.clearButton);
        searchButton = (Button) findViewById(R.id.searchButton);
        dateButton = (Button) findViewById(R.id.dateButton);
        searchInput = (EditText) findViewById(R.id.searchInput);
        datePick = (DatePicker) findViewById(R.id.datePick);
        listView = (ListView) findViewById(R.id.listView);
        listItems = (TextView) findViewById(R.id.listItems);
        startButtonPlan.setOnClickListener(this);
        startButtonCurrent.setOnClickListener(this);
        startButtonRoad.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        dateButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        datePick.setSpinnersShown(true);
        datePick.setCalendarViewShown(false);


    }

    // ArrayList to store values and find correct entries to push to listview.
    private ArrayList<IncidentClass> parseData(String dataToParse) {

        incidentArray = null;
        IncidentClass item = null;
        ArrayList<IncidentClass> ilist = null;

        try {
            Log.e("MyTag", "array here");
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(dataToParse));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("channel")) {
                        ilist = new ArrayList<IncidentClass>();

                    } else if (xpp.getName().equalsIgnoreCase("item")) {

                        Log.e("MyTag", "Item Start Tag found");
                        item = new IncidentClass();
                        itemTagFound = 1;
                    }
                    if (itemTagFound == 1) {
                        if (xpp.getName().equalsIgnoreCase("title")) {
                            String tempT = "";
                            tempT = xpp.nextText();
                            if (tempT == null) {
                                Log.d("MyTag", "no title available");
                                tempT = "No Title Available";
                            } else if (tempT != null) {
                                Log.e("MyTag", "Road: " + tempT);
                            }
                            item.setTitle(tempT);

                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            String tempDes = xpp.nextText();
                            if (tempDes == null/* || !tempDes.contains("<br />") || !tempDes.contains("Start Date:") || !tempDes.contains("End Date: ")*/) {
                                Log.d("MyTag", "no description available");
                                tempDes = "No Description Available";
                                item.setDesc(tempDes);
                            } else if (tempDes != null && tempDes.contains("<br />")) {

                                String end_sub = tempDes.substring(tempDes.indexOf(" - 00:00"));
                                String startDate = tempDes.substring("Start Date: ".length(), tempDes.indexOf("<br />"));
                                String endDate = end_sub.substring("End Date: ".length(), end_sub.lastIndexOf("- 00:00"));
                                endDate = endDate.replaceFirst("r />End Date:", "");
                                String dates = "Start Date: " + startDate + "\n" + "\n" +
                                        "End Date: " + endDate + "\n" + "\n" + "Full Information: " + tempDes;
                                item.setDesc(dates);
                            } else if (tempDes != null) {
                                Log.d("MyTag", "no description available");
                                item.setDesc(tempDes);
                            }


                        }


                    }

                } else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        ilist.add(item);
                    } else if (xpp.getName().equalsIgnoreCase("channel")) {
                        int size;
                        size = ilist.size();
                    }
                }

                // Get the next event
                eventType = xpp.next();

            } // End of while

            return ilist;

        } catch (XmlPullParserException ae1) {
            Log.e("MyTag", "Parsing error" + ae1.toString());
        } catch (IOException ae1) {
            Log.e("MyTag", "IO error during parsing");
        }

        Log.e("MyTag", "End document");

        return ilist;

    }


    public void startProgressPlan() {
        // Run network access on a separate thread;
        // Traffic Scotland Planned Roadworks XML link
        String urlSource = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
        new Thread(new Task(urlSource)).start();
    }

    public void startProgressCurrent() {
        String urlSource2 = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
        new Thread(new Task2(urlSource2)).start();
    }

    public void startProgressRoad() {
        String urlSource3 = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
        new Thread(new Task3(urlSource3)).start();
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View v) {
        // if statement dedicated to the start button, needed to pull up all roadworks
        if (v == startButtonPlan) {
            Log.e("MyTag", "in onClick");
            startProgressPlan();
            Log.e("MyTag", "after startProgress");
        }
        if (v == startButtonCurrent) {
            Log.e("MyTag", "in onClick");
            startProgressCurrent();
            Log.e("MyTag", "after startProgress");
        }
        if (v == startButtonRoad) {
            Log.e("MyTag", "in onClick");
            startProgressRoad();
            Log.e("MyTag", "after startProgress");
        }


        // if statement dedicated to the search function, needed to pull up all roadworks matching search criteria
        if (v == searchButton) {
            if (searchInput.getText().length() != 0) {
                String search = searchInput.getText().toString();
                Log.d("My Tag", search);
                ArrayList<IncidentClass> foundItems = new ArrayList<>();
                for (IncidentClass incidents : incidentArray) {
                    if (incidents.getTitle().contains(search)) {
                        foundItems.add(incidents);
                        Log.d("tag", foundItems.toString());
                    } else if (foundItems == null) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                        builder1.setMessage("Sorry, the search term entered did not return any entries");
                        builder1.setCancelable(false);
                        builder1.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert1 = builder1.create();
                        alert1.show();
                    }
                }
                // adapter built similarly to "Find All Roadworks" but
                // calling the foundItems array established in this method
                ArrayAdapter<IncidentClass> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.activity_items, foundItems);
                listView.setAdapter(adapter);
            }

            // statement to handle the search box if no data has been entered,
            // displays alert prompt and closes.
            if (searchInput.getText().length() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please enter a valid road to search for");
                builder.setCancelable(false);
                builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        // Search function for date using spinner DatePicker
        if (v == dateButton) {
            // establishing the date string to be converted in format
            String day = "" + datePick.getDayOfMonth();
            String month = "/" + (datePick.getMonth() + 1);
            String year = "/" + datePick.getYear();
            String date = day + month + year;
            Log.d("MyTag", "Date: " + date);

            if (date != null) {

                Date dateShort;
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
                String dateLong = null;
                try {
                    //
                    dateShort = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date);
                    assert dateShort != null;
                    dateLong = sdf.format(dateShort);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String searchDate = dateLong;
                ArrayList<IncidentClass> searchResults = new ArrayList<IncidentClass>();
                for (IncidentClass incidents : incidentArray) {
                    if (incidents.getDesc() != null && (incidents.getDesc().contains(searchDate))) {
                        searchResults.add(incidents);

                    } else if (incidents.getDesc().equalsIgnoreCase(searchDate)) {
                        Log.e("MyTag", "ruh oh no match");
                    }


                }
                ArrayAdapter<IncidentClass> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.activity_items, searchResults);
                listView.setAdapter(adapter);
                Log.d("MyTag", searchResults.toString());
            }


        }

        if (v == clearButton) {
            searchInput = null;
            /*ArrayAdapter<IncidentClass> clearAdapter = new ArrayAdapter(MainActivity.this, R.layout.activity_items);
            clearAdapter.clear();
            listView.setAdapter(clearAdapter);*/
        }

    }


    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable {
        private String url;

        public Task(String aurl) {
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
                Log.e("MyTag", "after ready");

                while ((inputLine = in.readLine()) != null) {
                    result1 = result1 + inputLine;
                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception in run");
            }


            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    // parsing data into completed/final array.
                    incidentArray = parseData(result1);

                    // Adapter established to select array that includes parsed data.
                    ArrayAdapter<IncidentClass> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.activity_items, incidentArray);
                    listView.setAdapter(adapter);
                    Log.d("UI thread", "UI thread");
                }
            });
        }
    }

    private class Task2 implements Runnable {
        private String url2;


        public Task2(String aurl2) {
            url2 = aurl2;
        }


        @Override
        public void run() {

            URL aurl2;
            URLConnection yc2;


            BufferedReader in2 = null;
            String inputLine2 = "";


            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl2 = new URL(url2);
                yc2 = aurl2.openConnection();
                in2 = new BufferedReader(new InputStreamReader(yc2.getInputStream()));
                Log.e("MyTag", "after ready");

                while ((inputLine2 = in2.readLine()) != null) {
                    result1 = result1 + inputLine2;
                }
                in2.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception in run");
            }

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    // parsing data into completed/final array.
                    incidentArray = parseData(result1);

                    // Adapter established to select array that includes parsed data.
                    ArrayAdapter<IncidentClass> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.activity_items, incidentArray);
                    listView.setAdapter(adapter);
                    Log.d("UI thread", "UI thread");
                }
            });
        }

    }

    private class Task3 implements Runnable {
        private String url3;


        public Task3(String aurl3) {
            url3 = aurl3;
        }


        @Override
        public void run() {

            URL aurl3;
            URLConnection yc3;


            BufferedReader in3 = null;
            String inputLine3 = "";


            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl3 = new URL(url3);
                yc3 = aurl3.openConnection();
                in3 = new BufferedReader(new InputStreamReader(yc3.getInputStream()));
                Log.e("MyTag", "after ready");

                while ((inputLine3 = in3.readLine()) != null) {
                    result1 = result1 + inputLine3;
                }
                in3.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception in run");
            }

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    // parsing data into completed/final array.
                    incidentArray = parseData(result1);

                    // Adapter established to select array that includes parsed data.
                    ArrayAdapter<IncidentClass> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.activity_items, incidentArray);
                    listView.setAdapter(adapter);
                    Log.d("UI thread", "UI thread");
                }
            });
        }

    }

}