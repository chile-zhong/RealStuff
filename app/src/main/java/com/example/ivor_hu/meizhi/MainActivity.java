package com.example.ivor_hu.meizhi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ivor_hu.meizhi.db.Image;
import com.example.ivor_hu.meizhi.db.Stuff;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.utils.Constants;
import com.example.ivor_hu.meizhi.widget.BaseFragment;
import com.example.ivor_hu.meizhi.widget.GirlsFragment;
import com.example.ivor_hu.meizhi.widget.SearchFragment;
import com.example.ivor_hu.meizhi.widget.StuffFragment;

import java.util.List;
import java.util.Map;

import io.realm.Realm;

import static com.example.ivor_hu.meizhi.utils.Constants.TYPE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final String CURR_TYPE = "curr_fragment_type";
    private static final int CLEAR_DONE = 0x36;
    private static final int CLEAR_ALL = 0x33;
    private static final String Search_ALL = "All";
    private static final int PASS_SEARCH_CAT = 0x34;

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
    private Realm mRealm;
    private DrawerLayout mDrawer;
    private String mSearchCat = "all";

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
            fragment = GirlsFragment.newInstance(TYPE.GIRLS.getApiName());
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

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        final Spinner searchCatSp = (Spinner) headerView.findViewById(R.id.search_cat_sp);
        ArrayAdapter<String> searchCatAdapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_item,
                getResources().getStringArray(R.array.search_cat));
        searchCatAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        searchCatSp.setPopupBackgroundResource(R.color.colorAccent);
        searchCatSp.setAdapter(searchCatAdapter);
        searchCatSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSearchCat = getResources().getStringArray(R.array.search_cat_api)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final EditText searchEt = (EditText) headerView.findViewById(R.id.query_et);
        final ImageButton searchBtn = (ImageButton) headerView.findViewById(R.id.search_btn);
        final ImageButton clearSearchBtn = (ImageButton) headerView.findViewById(R.id.search_clear_btn);

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (clearSearchBtn == null)
                    return;

                if (s.toString().length() > 0 && clearSearchBtn.getVisibility() == View.GONE)
                    clearSearchBtn.setVisibility(View.VISIBLE);
                else if (s.toString().length() == 0 && clearSearchBtn.getVisibility() == View.VISIBLE)
                    clearSearchBtn.setVisibility(View.GONE);
            }
        });
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    searchBtn.callOnClick();
                    return true;
                }
                return false;
            }
        });

        clearSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.getText().clear();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String searchText = searchEt.getText().toString().trim();
                final String safeText = CommonUtil.stringFilterStrict(searchText);
                if (safeText == null || safeText.length() == 0 || safeText.length() != searchText.length()) {
                    CommonUtil.makeSnackBar(navigationView, getString(R.string.search_tips), Snackbar.LENGTH_LONG);
                } else {
                    searchEt.getText().clear();
                    switchToSearchResult(safeText, mSearchCat, 10);
                    hideSoftKeyboard(searchEt);
                }
            }
        });

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

        mRealm = Realm.getDefaultInstance();
        mClearCacheHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case CLEAR_DONE:
                        ((BaseFragment) mCurrFragment).updateData();
                        break;
                    case CLEAR_ALL:
//                        for (TYPE type : TYPE.values()) {
//                            Fragment fragment = getSupportFragmentManager().findFragmentByTag(type.getId());
//                            if (fragment == null)
//                                continue;
//
//                            ((BaseFragment) fragment).updateData();
//                        }
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TYPE.GIRLS.getId());
                        if (fragment != null)
                            ((BaseFragment) fragment).updateData();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
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
            if (((BaseFragment) mCurrFragment).isFetching())
                CommonUtil.makeSnackBar(mCoordinatorLayout, getString(R.string.frag_is_fetching), Snackbar.LENGTH_SHORT);
            else
                clearRealmType(mCurrFragmentType);
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

    private void clearRealmType(final String typeId) {
        if (TYPE.COLLECTIONS.getId().equals(typeId)) {
            clearCacheSnackBar(R.string.clear_cache_all, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Image.clearImage(mRealm);
                    Stuff.clearAll(mRealm);
                    mClearCacheHandler.sendEmptyMessage(CLEAR_ALL);
                }
            });
        } else if (TYPE.SEARCH_RESULTS.getId().equals(typeId)) {
            CommonUtil.makeSnackBar(mCoordinatorLayout, getString(R.string.no_search_cache), Snackbar.LENGTH_SHORT);
        } else {
            final int strId = TYPE.valueOf(typeId).getStrId();
            final String apiName = TYPE.valueOf(typeId).getApiName();
            if (strId != -1) {
                clearCacheSnackBar(strId, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TYPE.GIRLS.getApiName().equals(apiName))
                            Image.clearImage(mRealm);
                        else
                            Stuff.clearType(mRealm, apiName);
                        mClearCacheHandler.sendEmptyMessage(CLEAR_DONE);
                    }
                });
            }
        }
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

    private void clearCacheSnackBar(int clearTipStrId, View.OnClickListener onClickListener) {
        CommonUtil.makeSnackBarWithAction(
                mCoordinatorLayout,
                String.format(getString(R.string.clear_type), getString(clearTipStrId)),
                Snackbar.LENGTH_SHORT,
                onClickListener,
                getString(R.string.confirm));
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

    private void switchToSearchResult(String keyword, String category, int count) {
        // close Drawer
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            mDrawer.closeDrawer(GravityCompat.END);
        }

        FragmentManager manager = getSupportFragmentManager();
        String searchTag = Constants.TYPE.SEARCH_RESULTS.getId();
        Fragment searchFragment = manager.findFragmentByTag(searchTag);
        if (searchFragment == null) {
            hideAndAdd(manager, SearchFragment.newInstance(keyword, category, count), searchTag);
        } else {
            hideAndShow(manager, searchFragment, searchTag);
            ((SearchFragment) searchFragment).search(keyword, category, count);
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

    private void hideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void updateLikedData(Fragment newFragment, String fragmentIdx) {
        if (fragmentIdx.equals(TYPE.GIRLS.getId()) || fragmentIdx.equals(TYPE.SEARCH_RESULTS.getId())) {
            return;
        }
        ((StuffFragment) newFragment).updateData();
    }

}
