package com.example.ivor_hu.meizhi;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.webkit.WebView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ivor_hu.meizhi.ui.SearchSuggestionProvider;
import com.example.ivor_hu.meizhi.ui.fragment.BaseFragment;
import com.example.ivor_hu.meizhi.ui.fragment.BaseStuffFragment;
import com.example.ivor_hu.meizhi.ui.fragment.CollectionFragment;
import com.example.ivor_hu.meizhi.ui.fragment.GirlsFragment;
import com.example.ivor_hu.meizhi.ui.fragment.SearchFragment;
import com.example.ivor_hu.meizhi.ui.fragment.StuffFragment;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.utils.Constants;

import java.util.List;
import java.util.Map;

import static com.example.ivor_hu.meizhi.utils.Constants.TYPE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final String CURR_TYPE = "curr_fragment_type";

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

    private DrawerLayout mDrawer;
    private SearchView mSearchView;
    private boolean mIsSearching;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchView = findViewById(R.id.searchview);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);

        mToolbar = findViewById(R.id.toolbar);
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

        mCoordinatorLayout = findViewById(R.id.main_coor_layout);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewss) {
//                ((BaseFragment) mCurrFragment).smoothScrollToTop();
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = getLayoutInflater().inflate(R.layout.webview_layout, null);
                WebView mWebView = (WebView) view.findViewById(R.id.webviewzzz);
//                mWebView.setWebViewClient(new WebViewClient());
                mWebView.loadUrl("file:///android_asset/1.html");
//                mWebView.loadData("<span>请问</span><span style='color:#a5060d;'>黄兴</span>< img src='http://106.15.50.103:4000/images/667.png' width='30' width='30'><span>是哪一个省人？</span>", "text/html; charset=UTF-8", null);
//                builder.setView(view);
                builder.setCustomTitle(view);
                builder.setSingleChoiceItems(R.array.socialNetworks, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, which + "", Toast.LENGTH_LONG)
                                .show();
                    }
                });
                builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, which + "", Toast.LENGTH_LONG)
                                .show();
                    }
                });
                builder.setNegativeButton("投票", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, which + "", Toast.LENGTH_LONG)
                                .show();
                    }
                });
                builder.setNeutralButton("放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);
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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            if (((BaseFragment) mCurrFragment).isFetching()) {
                CommonUtil.makeSnackBar(mCoordinatorLayout, getString(R.string.frag_is_fetching), Snackbar.LENGTH_SHORT);
                return;
            }

            String query = intent.getStringExtra(SearchManager.QUERY);
            final String safeText = CommonUtil.stringFilterStrict(query);
            if (safeText == null || safeText.length() == 0 || safeText.length() != query.length()) {
                CommonUtil.makeSnackBar(mCoordinatorLayout, getString(R.string.search_tips), Snackbar.LENGTH_LONG);
            } else {
                new SearchRecentSuggestions(this, SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE)
                        .saveRecentQuery(safeText, null);
                TYPE type = getCurrSearchType();
                String searchCat;
                if (type == null) {
                    searchCat = getString(R.string.api_all);
                } else {
                    searchCat = type.getApiName();
                }
                switchToSearchResult(safeText, searchCat);
                hideSearchView();
            }
        }
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mIsSearching) {
            mIsSearching = false;
            hideSearchView();
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
            new AsyncTask<Context, Void, Void>() {
                @Override
                protected Void doInBackground(Context... contexts) {
                    CommonUtil.clearCache(contexts[0]);
                    Glide.get(contexts[0]).clearDiskCache();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    CommonUtil.makeSnackBar(mCoordinatorLayout, getString(R.string.clear_done), Snackbar.LENGTH_SHORT);
                }
            }.execute(MainActivity.this);
            return true;
        } else if (id == R.id.action_search) {
            mIsSearching = true;
            showSearchView();
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
        } else if (id == TYPE.COLLECTIONS.getResId()) {
            swithTo(manager, TYPE.COLLECTIONS.getId(), CollectionFragment.newInstance(TYPE.COLLECTIONS.getApiName()));
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
            reenterState = new Bundle(data.getExtras());
            final int index = reenterState.getInt(ViewerActivity.INDEX, 0);
            ((GirlsFragment) mCurrFragment).smoothScrollTo(index);
            supportPostponeEnterTransition();
            ((GirlsFragment) mCurrFragment).onActivityReenter(index);
        }
    }

    private void closeDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void swithTo(FragmentManager manager, String type, Fragment addedFragment) {
        Fragment fragment = manager.findFragmentByTag(type);
        if (null != fragment) {
            hideAndShow(manager, fragment, type);
        } else {
            hideAndAdd(manager, addedFragment, type);
        }
        if (mIsSearching) {
            hideSearchView();
        }
    }

    private void switchToSearchResult(String keyword, String category) {
        FragmentManager manager = getSupportFragmentManager();
        String searchTag = Constants.TYPE.SEARCH_RESULTS.getId();
        Fragment searchFragment = manager.findFragmentByTag(searchTag);
        if (searchFragment == null) {
            hideAndAdd(manager, SearchFragment.newInstance(keyword, category), searchTag);
        } else {
            hideAndShow(manager, searchFragment, searchTag);
            ((SearchFragment) searchFragment).search(keyword, category);
        }
    }

    private void hideAllExcept(String mCurrFragmentType) {
        FragmentManager manager = getSupportFragmentManager();
        for (TYPE type : TYPE.values()) {
            Fragment fragment = manager.findFragmentByTag(type.getId());
            if (fragment == null) {
                continue;
            }

            if (type.getId().equals(mCurrFragmentType)) {
                manager.beginTransaction().show(fragment).commit();
                mCurrFragment = fragment;
            } else {
                manager.beginTransaction().hide(fragment).commit();
            }
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

    private void showSearchView() {
        if (mSearchView != null) {
            mSearchView.setVisibility(View.VISIBLE);
            if (mSearchView.getWidth() == 0) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showSearchViewAnimation();
                    }
                });
            } else {
                showSearchViewAnimation();
            }
        }

        if (mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
        updateSearchHint();
    }

    private void showSearchViewAnimation() {
        int cx = mSearchView.getWidth() - (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24, mSearchView.getResources().getDisplayMetrics());
        int cy = mSearchView.getHeight() / 2;
        int finalRadius = Math.max(mSearchView.getWidth(), mSearchView.getHeight());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewAnimationUtils.createCircularReveal(mSearchView, cx, cy, 0, finalRadius).start();
        }
    }

    private void hideSearchView() {
        if (mSearchView != null) {
            mSearchView.setVisibility(View.GONE);
        }
        if (mToolbar != null) {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    private void updateSearchHint() {
        int navResId;
        TYPE type = getCurrSearchType();
        if (type == null) {
            navResId = R.string.search_all;
        } else {
            navResId = type.getStrId();
        }

        if (mSearchView != null) {
            mSearchView.setQueryHint(String.format(getString(R.string.search), getString(navResId)));
        }
    }

    private TYPE getCurrSearchType() {
        if (TYPE.GIRLS.getId().equals(mCurrFragmentType)
                || TYPE.COLLECTIONS.getId().equals(mCurrFragmentType)
                || TYPE.SEARCH_RESULTS.getId().equals(mCurrFragmentType)) {
            return null;
        } else {
            return TYPE.valueOf(mCurrFragmentType);
        }
    }

    private void updateLikedData(Fragment newFragment, String fragmentIdx) {
        if (fragmentIdx.equals(TYPE.GIRLS.getId()) || fragmentIdx.equals(TYPE.SEARCH_RESULTS.getId())) {
            return;
        }
        ((BaseStuffFragment) newFragment).updateData();
    }

}
