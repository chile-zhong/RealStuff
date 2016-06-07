package com.example.ivor_hu.meizhi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.widget.BaseFragment;
import com.example.ivor_hu.meizhi.widget.GirlsFragment;
import com.example.ivor_hu.meizhi.widget.StuffFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.ivor_hu.meizhi.utils.Constants.TYPE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final String CURR_TYPE = "curr_fragment_type";
    private static final int CLEAR_SNACKBAR = 0x36;

    private CoordinatorLayout mCoordinatorLayout;
    GestureDetector mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            CommonUtil.makeSnackBar(mCoordinatorLayout, getResources().getString(R.string.main_double_taps), Snackbar.LENGTH_LONG);
            return true;
        }
    });
    private FloatingActionButton mFab;
    private Toolbar mToolbar;
    private Fragment mCurrFragment;
    private String mCurrFragmentType;
    private Bundle reenterState;

    private Handler mClearCacheHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.nav_girls);
        setSupportActionBar(mToolbar);
        mToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

        final FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = GirlsFragment.newInstance(TYPE.GIRLS.getId());
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment, TYPE.GIRLS.getId())
                    .commit();
            mCurrFragment = fragment;
            mCurrFragmentType = TYPE.GIRLS.getId();
        }

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coor_layout);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseFragment) mCurrFragment).smoothScrollToTop();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (reenterState != null && TYPE.GIRLS.getId().equals(mCurrFragmentType)) {
                    GirlsFragment girlsFragment = (GirlsFragment) mCurrFragment;
                    int i = reenterState.getInt(ViewerActivity.INDEX, 0);
//                    Log.d(TAG, "onMapSharedElements: reenter from " + i);

                    sharedElements.clear();
                    sharedElements.put(girlsFragment.getImageUrlAt(i), girlsFragment.getImageViewAt(i));

                    reenterState = null;
                }
            }
        });

        mClearCacheHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case CLEAR_SNACKBAR:
                        CommonUtil.makeSnackBar(mCoordinatorLayout, "Clear " + msg.arg1, Snackbar.LENGTH_LONG);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURR_TYPE, mCurrFragmentType);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrFragmentType = savedInstanceState.getString(CURR_TYPE);
        hideAllExcept(mCurrFragmentType);
        mToolbar.setTitle(TYPE.valueOf(mCurrFragmentType).getStrId());
    }

    private void hideAllExcept(String mCurrFragmentType) {
        FragmentManager manager = getSupportFragmentManager();
        for (TYPE type : TYPE.values()) {
            Fragment fragment = manager.findFragmentByTag(type.getId());
            if (fragment == null)
                continue;

            if (type.getId().equals(mCurrFragmentType)) {
                manager.beginTransaction().show(fragment).commit();
                mCurrFragment = fragment;
            } else {
                manager.beginTransaction().hide(fragment).commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.action_clear_cache) {
            Log.d(TAG, "onOptionsItemSelected: clear");
            ClearCacheDialog.newInstance().show(getSupportFragmentManager(), "clear_cache");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();
        if (((BaseFragment) mCurrFragment).isFetching()) {
            CommonUtil.makeSnackBar(mCoordinatorLayout, getString(R.string.frag_is_fetching), Snackbar.LENGTH_SHORT);
            closeDrawer();
            return false;
        }

        if (id == TYPE.GIRLS.getResId()) {
            swithTo(manager, TYPE.GIRLS.getId(), GirlsFragment.newInstance(TYPE.GIRLS.getApiName()));
        } else {
            for (TYPE type : TYPE.values()) {
                if (type.getResId() == id) {
                    swithTo(manager, type.getId(), StuffFragment.newInstance(type.getApiName()));
                    break;
                }
            }
        }

        closeDrawer();
        return true;
    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void swithTo(FragmentManager manager, String type, Fragment addedFragment) {
        Fragment fragment = manager.findFragmentByTag(type);
        if (null != fragment) {
            hideAndShow(manager, fragment, type);
        } else {
            hideAndAdd(manager, addedFragment, type);
        }
    }

    private void hideAndAdd(FragmentManager manager, Fragment newFragment, String fragmentIdx) {
        manager.beginTransaction().hide(mCurrFragment).add(R.id.fragment_container, newFragment, fragmentIdx).commit();
        mCurrFragment = newFragment;
        mCurrFragmentType = fragmentIdx;
        mToolbar.setTitle(TYPE.valueOf(fragmentIdx).getStrId());
    }

    private void hideAndShow(FragmentManager manager, Fragment newFragment, String fragmentIdx) {
        manager.beginTransaction().hide(mCurrFragment).show(newFragment).commit();
        updateLikedData(newFragment, fragmentIdx);
        mCurrFragment = newFragment;
        mCurrFragmentType = fragmentIdx;
        mToolbar.setTitle(TYPE.valueOf(fragmentIdx).getStrId());
    }

    private void updateLikedData(Fragment newFragment, String fragmentIdx) {
        if (fragmentIdx.equals(TYPE.GIRLS.getId())) {
            return;
        }
        ((StuffFragment) newFragment).updateData();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        if (TYPE.GIRLS.getId().equals(mCurrFragmentType)) {
            supportPostponeEnterTransition();

            reenterState = new Bundle(data.getExtras());

            final int index = reenterState.getInt(ViewerActivity.INDEX, 0);
            ((GirlsFragment) mCurrFragment).onActivityReenter(index);
        }
    }

    public static class ClearCacheDialog extends DialogFragment {
        Map<String, String> mMap;

        public static ClearCacheDialog newInstance() {
            Bundle args = new Bundle();
            ClearCacheDialog fragment = new ClearCacheDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mMap = new HashMap<>();
            for (TYPE type : TYPE.values()) {
                mMap.put(getString(type.getStrId()), type.getApiName());
            }
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            getDialog().setTitle(getString(R.string.action_clear_cache));

            final List<String> items = getListData();
            View view = inflater.inflate(R.layout.dialog_clear_cache, container);
            ListView listView = (ListView) view.findViewById(R.id.clear_cache_lv);

            listView.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                    CommonUtil.makeSnackBarWithAction(
                            view,
                            String.format(getString(R.string.clear_type), items.get(position)),
                            Snackbar.LENGTH_SHORT,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clearCache(position);
                                    CommonUtil.makeSnackBar(view, "Deleted.", Snackbar.LENGTH_SHORT);
                                }
                            },
                            getString(R.string.confirm));
                }
            });
            return view;
        }

        private void clearCache(int position) {
            if (position == 0) {
                Log.d(TAG, "clearCache: all");
            } else {
                Log.d(TAG, "clearCache: " + mMap.get(getListData().get(position)));
            }
        }

        private List<String> getListData() {
            List<String> strings = new ArrayList<>();
            strings.add(getString(R.string.clear_cache_all));

            for (TYPE type : TYPE.values()) {
                strings.add(getString(type.getStrId()));
            }
            strings.remove(strings.size() - 1);
//
//            strings.add(getString(R.string.nav_girls));
//            strings.add(getString(R.string.nav_android));
//            strings.add(getString(R.string.nav_ios));
//            strings.add(getString(R.string.nav_web));
//            strings.add(getString(R.string.nav_app));
//            strings.add(getString(R.string.nav_fun));
//            strings.add(getString(R.string.nav_others));
            return strings;
        }
    }

}
