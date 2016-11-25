package com.example.jim.wallpaperchanger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.io.IOException;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WallpaperChanger extends AppCompatActivity {
    private String URLAddress = "";
    //private Bitmap[] Images = new Bitmap[3];
    private final RedditFetcher redditManager = new RedditFetcher();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_changer);
        //make a button
        Button btnSetWallpaper = (Button)findViewById(R.id.btnSetWallpaper);
        btnSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Wallpaper Set", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //Check if device is connected to the internet
        Context ctx = this;
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(ctx.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            redditManager.execute();
        } else {
            //TextView URLDisplay = (TextView) findViewById(R.id.URLText);
            //URLDisplay.setText("No internet connection detected!");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wallpaper_changer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        public static Bitmap[] Images = new Bitmap[3];

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_wallpaper_changer, container, false);
            ImageView imageView = (ImageView)rootView.findViewById(R.id.imageView);
            imageView.setImageBitmap(Images[getArguments().getInt(ARG_SECTION_NUMBER)-1]);
            TextView textView = (TextView) rootView.findViewById(R.id.ImageInformation);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            textView.setText("Displaying the number " + getArguments().getInt(ARG_SECTION_NUMBER) + "image from /r/earthporn");
            return rootView;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    //Class responsible for communicating with reddit API
    private class RedditFetcher extends AsyncTask<Void, String, Bitmap[]> {
        private final String REDDIT_URL =
                "https://www.reddit.com/r/earthporn/top.json?t=all";
        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Do something like display a progress bar
        }

        // This is run in a background thread
        @Override
        protected Bitmap[] doInBackground(Void... params) {
            // get the string from params, which is an array
            try {
                URL url = new URL(REDDIT_URL);
                publishProgress("Attempting to establish connection...");
                HttpURLConnection connection =
                        (HttpURLConnection)url.openConnection();
                publishProgress("Reading from reddit...");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuffer json = new StringBuffer(1024);
                String tmp="";
                while((tmp=reader.readLine())!=null) {
                    json.append(tmp).append("\n");
                }
                reader.close();

                JSONObject data = new JSONObject(json.toString());
                JSONArray redditPost = data.getJSONObject("data").getJSONArray("children");
                Bitmap[] ImageArray = new Bitmap[3];
                //get the top 3 posts
                for (int i = 0; i < 3; i++)
                {
                    String imageURL = redditPost.getJSONObject(i).getJSONObject("data").getJSONObject("preview").getJSONArray("images").getJSONObject(0).getJSONObject("source").getString("url");
                    InputStream input = new URL(imageURL).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    ImageArray[i] = bitmap;
                }
                return ImageArray;
            }catch(Exception e){
                return null;
            }
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            WallpaperChanger.this.displayProgress(values[0]);
            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(Bitmap[] result) {
            super.onPostExecute(result);
            WallpaperChanger.this.storeImages(result);
            WallpaperManager myWallpaperChanger = WallpaperManager.getInstance(getBaseContext());
            try {
                myWallpaperChanger.setBitmap(result[0]);

            }catch (IOException ex) {
                ex.printStackTrace();
            }
        }
            // Do things like hide the progress bar or change a TextView
        }


    //Display current progress message while fetching from Reddit
    public void displayProgress(String message) {

        //TextView URLDisplay = (TextView) findViewById(R.id.URLText);
        //URLDisplay.setText(message);
    }

    //Display the image url
    public void storeImages(Bitmap[] result) {
        for (int i = 0; i < result.length; i++)
        {
            PlaceholderFragment.Images[i] = result[i];
        }
        //mViewPager.get
        //ImageView view1 = (ImageView)findViewById(R.id.image1);
        //ImageView view2 = (ImageView)findViewById(R.id.image2);
        //ImageView view3 = (ImageView)findViewById(R.id.image3);
        //view1.setImageBitmap(image[0]);
        //view2.setImageBitmap(image[1]);
        //view3.setImageBitmap(image[2]);
    }
}
