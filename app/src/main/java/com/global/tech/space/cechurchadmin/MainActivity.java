package com.global.tech.space.cechurchadmin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.global.tech.space.cechurchadmin.main_tabs.CellMeetingsFragments;
import com.global.tech.space.cechurchadmin.main_tabs.CellsFragments;
import com.global.tech.space.cechurchadmin.main_tabs.MembersFragments;
import com.global.tech.space.cechurchadmin.main_tabs.ServiceFragments;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity
{
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private boolean searchToogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initControls();
    }

    private void initSearch()
    {
        ImageButton search = findViewById(R.id.main_search_toogle);
        search.setOnClickListener( v -> {
            Intent intent = new Intent( this, MainSearchActivity.class );
            intent.setAction( pagerAdapter.getSearchAction( pager.getCurrentItem() ) );
            startActivity( intent );
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel( getString( R.string.channel_id ), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initControls()
    {
        initPages();
        initSearch();
    }

    private void initPages() {
        TabLayout tabs = findViewById(R.id.main_tab);
        pager = findViewById(R.id.main_pager);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        pager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager( pager );
    }


    public void pagerAction(View view) {
        pagerAdapter.pageAction( pager.getCurrentItem() );
    }

    public void startLoadAnimation() {
        ProgressBar loader = findViewById( R.id.main_loader );
        loader.setIndeterminate( true );
        loader.setVisibility( View.VISIBLE );
        FloatingActionButton add = findViewById( R.id.add_members );
        add.hide();
    }

    public void endLoadAnimation() {
        ProgressBar loader = findViewById( R.id.main_loader );
        loader.setIndeterminate( false );
        loader.setVisibility( View.GONE );
        FloatingActionButton add = findViewById( R.id.add_members );
        add.show();
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private final PageProvider[] pagers;
        private final String[] titles;

        public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            pagers = new PageProvider[]{ new ServiceFragments(), new CellMeetingsFragments(), new MembersFragments(), new CellsFragments()};
            titles = new String[]{ "Services", "Cell Meetings", "Members", "Cells" };
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return pagers[ position].getFragment();
        }

        @Override
        public int getCount() {
            return pagers.length;
        }

        public void pageAction( int position )
        {
            pagers[ position ].performAction();
        }

        public String getSearchAction( int position )
        {
            return pagers[ position ].getSearchAction();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[ position ];
        }
    }

    public interface PageProvider
    {
        Fragment getFragment();
        void performAction();
        String getSearchAction();
    }
}
