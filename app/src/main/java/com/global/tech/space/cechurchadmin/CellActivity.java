package com.global.tech.space.cechurchadmin;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.global.tech.space.cechurchadmin.cell_tabs.CellDetailFragment;
import com.global.tech.space.cechurchadmin.cell_tabs.MeetingFragment;
import com.global.tech.space.cechurchadmin.cell_tabs.MemberFragment;
import com.global.tech.space.cechurchadmin.main_tabs.CellMeetingsFragments;
import com.global.tech.space.cechurchadmin.main_tabs.CellsFragments;
import com.global.tech.space.cechurchadmin.main_tabs.MembersFragments;
import com.global.tech.space.cechurchadmin.main_tabs.ServiceFragments;
import com.global.tech.space.cechurchadmin.models.Cell;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CellActivity extends AppCompatActivity
{
    public static final String NEW_CELL = "new_cell";

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private Cell cell;
    private CEService.CEApi ceApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cell_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        getInfo();
    }

    private void init() {
        ceApi = CEService.getInstance().getCeApi();
    }

    private void getInfo() {
        Call<Cell> memberCall = ceApi.getCell(getIntent().getIntExtra("id", -1 ));
        memberCall.enqueue(new Callback<Cell>() {
            @Override
            public void onResponse(Call<Cell> call, Response<Cell> response) {
                if ( response.isSuccessful() )
                {
                    cell = response.body();
                    initToolbar();
                    initControls();
                    ifNew();
                }
            }

            @Override
            public void onFailure(Call<Cell> call, Throwable t) {

            }
        });
    }

    private void ifNew() {
        if ( getIntent().getAction() != null && getIntent().getAction().equals(NEW_CELL) )
        {
            pager.setCurrentItem( 1, true );
        }
    }

    private void initControls()
    {
        TabLayout tabs = findViewById(R.id.cell_tab);
        pager = findViewById(R.id.cell_pager);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        pager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager( pager );

        initPager();
    }

    private void initPager() {
        pager.addOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                pagerAdapter.setupPage( position );
            }
        } );
    }

    private void initToolbar()
    {
        TextView name = findViewById(R.id.cell_name);
        CollapsingToolbarLayout toolbar = findViewById(R.id.collapse_cell_title);
        name.setText( cell.name );

        toolbar.setCollapsedTitleTextColor(ResourcesCompat.getColor( getResources(), R.color.white, null ));
        toolbar.setTitle( cell.name );
    }

    public void pagerAction(View view) {
        pagerAdapter.pageAction( pager.getCurrentItem() );
    }

    public void cellAction(View view) {
        int index = pager.getCurrentItem();
        pagerAdapter.pageAction( index );
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private final PageProvider[] pagers;
        private final String[] titles;

        public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);

            Bundle args = new Bundle();
            args.putInt( "id", cell.id );
            args.putString( "cell", cell.toGson() );

            CellDetailFragment cell = new CellDetailFragment();
            cell.setArguments(args);

            MemberFragment members = new MemberFragment();
            members.setArguments(args);

            MeetingFragment meetings = new MeetingFragment();
            meetings.setArguments(args);

            pagers = new PageProvider[]{cell, members, meetings};
            titles = new String[]{ "Details", "Members", "Meetings"};
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

        public void setupPage( int pos )
        {
            pagers[ pos ].setupPage( CellActivity.this );
        }

        public void pageAction( int position )
        {
            pagers[ position ].performAction();
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
        void setupPage( AppCompatActivity activity );
    }
}
