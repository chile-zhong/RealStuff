package com.example.ivor_hu.meizhi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.utils.Constants;

import java.util.List;
import java.util.Map;

import static com.example.ivor_hu.meizhi.utils.Constants.TYPES;
import static com.example.ivor_hu.meizhi.utils.Constants.TYPE_ANDROID;
import static com.example.ivor_hu.meizhi.utils.Constants.TYPE_APP;
import static com.example.ivor_hu.meizhi.utils.Constants.TYPE_COLLECTIONS;
import static com.example.ivor_hu.meizhi.utils.Constants.TYPE_FUN;
import static com.example.ivor_hu.meizhi.utils.Constants.TYPE_GIRLS;
import static com.example.ivor_hu.meizhi.utils.Constants.TYPE_IOS;
import static com.example.ivor_hu.meizhi.utils.Constants.TYPE_OTHERS;
import static com.example.ivor_hu.meizhi.utils.Constants.TYPE_WEB;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final String CURR_TYPE = "curr_fragment_type";

    FloatingActionButton mFab;
    private Toolbar mToolbar;
    private Fragment mCurrFragment;
    private String mCurrFragmentType;
    GestureDetector mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap");
            CommonUtil.makeSnackBar(mFab, getResources().getString(R.string.str_double_taps), Snackbar.LENGTH_LONG);
            return true;
        }
    });
    private Bundle reenterState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator);
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
            fragment = new GirlsFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment, TYPE_GIRLS)
                    .commit();
            mCurrFragment = fragment;
            mCurrFragmentType = TYPE_GIRLS;
        }

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mCurrFragmentType) {
                    case TYPE_GIRLS:
                        ((GirlsFragment) mCurrFragment).smoothScrollToTop();
                        break;
                    default:
                        ((StuffFragment) mCurrFragment).smoothScrollToTop();
                        break;
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (reenterState != null && mCurrFragmentType == TYPE_GIRLS) {
                    GirlsFragment girlsFragment = (GirlsFragment) mCurrFragment;
                    int i = reenterState.getInt("index", 0);
//                    Log.d(TAG, "onMapSharedElements: reenter from " + i);

                    sharedElements.clear();
                    sharedElements.put(girlsFragment.getImageUrlAt(i), girlsFragment.getImageViewAt(i));

                    reenterState = null;
                }
            }
        });
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
        mToolbar.setTitle(Constants.getResIdFromType(mCurrFragmentType));
    }

    private void hideAllExcept(String mCurrFragmentType) {
        FragmentManager manager = getSupportFragmentManager();
        for (String type : TYPES) {
            Fragment fragment = manager.findFragmentByTag(type);
            if (type.equals(mCurrFragmentType)) {
                manager.beginTransaction().show(fragment).commit();
                mCurrFragment = fragment;
            } else {
                if (fragment != null) {
                    manager.beginTransaction().hide(fragment).commit();
                }
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();
        if (id == R.id.nav_girls) {
            swithTo(manager, TYPE_GIRLS, new GirlsFragment());
        } else if (id == R.id.nav_android) {
            swithTo(manager, TYPE_ANDROID, StuffFragment.newInstance(TYPE_ANDROID));
        } else if (id == R.id.nav_ios) {
            swithTo(manager, TYPE_IOS, StuffFragment.newInstance(TYPE_IOS));
        } else if (id == R.id.nav_web) {
            swithTo(manager, TYPE_WEB, StuffFragment.newInstance(TYPE_WEB));
        } else if (id == R.id.nav_fun) {
            swithTo(manager, TYPE_FUN, StuffFragment.newInstance(TYPE_FUN));
        } else if (id == R.id.nav_app) {
            swithTo(manager, TYPE_APP, StuffFragment.newInstance(TYPE_APP));
        } else if (id == R.id.nav_others) {
            swithTo(manager, TYPE_OTHERS, StuffFragment.newInstance(TYPE_OTHERS));
        } else if (id == R.id.nav_collections) {
            swithTo(manager, TYPE_COLLECTIONS, StuffFragment.newInstance(TYPE_COLLECTIONS));
        }
//        else if (id == R.id.nav_test) {
//            Log.d(TAG, "onNavigationItemSelected: test");
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void swithTo(FragmentManager manager, String type, Fragment addedFragment) {
        Fragment fragment = manager.findFragmentByTag(type);
        if (null != fragment) {
            Log.d(TAG, type + " is not null.");
            hideAndShow(manager, fragment, type);
        } else {
            hideAndAdd(manager, addedFragment, type);
        }
    }

    private void hideAndAdd(FragmentManager manager, Fragment newFragment, String fragmentIdx) {
        manager.beginTransaction().hide(mCurrFragment).add(R.id.fragment_container, newFragment, fragmentIdx).commit();
        mCurrFragment = newFragment;
        mCurrFragmentType = fragmentIdx;
        mToolbar.setTitle(Constants.getResIdFromType(fragmentIdx));
    }

    private void hideAndShow(FragmentManager manager, Fragment newFragment, String fragmentIdx) {
        manager.beginTransaction().hide(mCurrFragment).show(newFragment).commit();
        updateLikedData(newFragment, fragmentIdx);
        mCurrFragment = newFragment;
        mCurrFragmentType = fragmentIdx;
        mToolbar.setTitle(Constants.getResIdFromType(fragmentIdx));
    }

    private void updateLikedData(Fragment newFragment, String fragmentIdx) {
        if (fragmentIdx.equals(TYPE_GIRLS)) {
            return;
        } else {
            ((StuffFragment) newFragment).updateData();
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        if (mCurrFragmentType == TYPE_GIRLS) {
            supportPostponeEnterTransition();

            reenterState = new Bundle(data.getExtras());

            final int index = reenterState.getInt("index", 0);
//            Log.d(TAG, "onActivityReenter: " + index);
            ((GirlsFragment) mCurrFragment).onActivityReenter(index);
        }
    }

//    public void hideFab() {
//        if (mFab != null && isFabShown) {
////            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
////            mFab.animate().translationY(mFab.getHeight() + lp.bottomMargin).setInterpolator(new AccelerateInterpolator(2));
//            mFab.animate().translationY(screenHeight - mFab.getY()).setInterpolator(new AccelerateInterpolator(2));
//            isFabShown = false;
//        }
//    }
//
//    public void showFab() {
//        if (mFab != null && !isFabShown) {
//            mFab.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2));
//            isFabShown = true;
//        }
//    }

}
