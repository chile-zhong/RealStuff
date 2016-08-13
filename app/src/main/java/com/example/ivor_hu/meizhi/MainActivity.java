package com.example.ivor_hu.meizhi;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
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

import com.example.ivor_hu.meizhi.base.BaseFragment;
import com.example.ivor_hu.meizhi.db.Image;
import com.example.ivor_hu.meizhi.db.Stuff;
import com.example.ivor_hu.meizhi.db.TypeBean;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.widget.CollectionFragment;
import com.example.ivor_hu.meizhi.widget.GirlsFragment;
import com.example.ivor_hu.meizhi.widget.SearchFragment;
import com.example.ivor_hu.meizhi.widget.SearchSuggestionProvider;
import com.example.ivor_hu.meizhi.widget.SlidingTabLayout;
import com.example.ivor_hu.meizhi.widget.StuffFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String CURR_IDX = "curr_fragment_idx";
    private static final int CLEAR_DONE = 0x36;
    private static final int CLEAR_ALL = 0x33;

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
    private BaseFragment mCurrFragment;
    private Bundle reenterState;

    private Handler mClearCacheHandler;
    private Realm mRealm;
    private SearchView mSearchView;
    private boolean mIsSearching;
    private List<TypeBean> mTypes;
    private ViewPager mViewPager;
    private MainFragPagerAdapter mAdapter;
    private String mSearchCat = "all";
    private String mSearchKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        mSearchView = (SearchView) findViewById(R.id.searchview);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.nav_girls);
        setSupportActionBar(mToolbar);
        mToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

        initTypes();
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mAdapter = new MainFragPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrFragment = getCurrFragment(position);
                updateLikedData();
                mToolbar.setTitle(mTypes.get(position).getStrId());
                hideSearchView();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setOffscreenPageLimit(7);
        mViewPager.setCurrentItem(0);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingtab);
        slidingTabLayout.setViewPager(mViewPager);
        mCurrFragment = getCurrFragment(0);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coor_layout);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrFragment.smoothScrollToTop();
            }
        });

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (reenterState != null && getCurrentItem() == 0) {
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
                        mCurrFragment.updateData();
                        break;
                    case CLEAR_ALL:
                        BaseFragment fragment = (BaseFragment) mAdapter.instantiateItem(mViewPager, 0);
                        if (fragment != null)
                            fragment.updateData();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void initTypes() {
        mTypes = new ArrayList<>();
        mTypes.add(new TypeBean(R.string.nav_girls, R.string.api_girls));
        mTypes.add(new TypeBean(R.string.nav_android, R.string.api_android));
        mTypes.add(new TypeBean(R.string.nav_ios, R.string.api_ios));
        mTypes.add(new TypeBean(R.string.nav_web, R.string.api_web));
        mTypes.add(new TypeBean(R.string.nav_app, R.string.api_app));
        mTypes.add(new TypeBean(R.string.nav_fun, R.string.api_fun));
        mTypes.add(new TypeBean(R.string.nav_others, R.string.api_others));
        mTypes.add(new TypeBean(R.string.nav_collections, R.string.nav_collections));
        mTypes.add(new TypeBean(R.string.nav_search, 0));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            if (mCurrFragment.isFetching()) {
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
                mSearchKey = safeText;
                mSearchCat = getCurrSearchType();
                mViewPager.setCurrentItem(mTypes.size() - 1);
                ((SearchFragment) mCurrFragment).search(mSearchKey, mSearchCat, 10);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURR_IDX, getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int currIdx = savedInstanceState.getInt(CURR_IDX);
        mViewPager.setCurrentItem(currIdx);
        mCurrFragment = getCurrFragment(currIdx);
        mToolbar.setTitle(getString(mTypes.get(currIdx).getStrId()));
    }

    private BaseFragment getCurrFragment(int currIdx) {
        return (BaseFragment) mAdapter.instantiateItem(mViewPager, currIdx);
    }

    @Override
    public void onBackPressed() {
        if (mIsSearching) {
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
            if (mCurrFragment.isFetching())
                CommonUtil.makeSnackBar(mCoordinatorLayout, getString(R.string.frag_is_fetching), Snackbar.LENGTH_SHORT);
            else
                clearRealmType(getCurrentItem());
            return true;
        } else if (id == R.id.action_search) {
            mIsSearching = true;
            showSearchView();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        if (getCurrentItem() == 0) {
            supportPostponeEnterTransition();

            reenterState = new Bundle(data.getExtras());

            final int index = reenterState.getInt(ViewerActivity.INDEX, 0);
            ((GirlsFragment) mCurrFragment).onActivityReenter(index);
        }
    }

    private void clearRealmType(final int currIdx) {
        if (currIdx == mTypes.size() - 2) {
            clearCacheSnackBar(R.string.clear_cache_all, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Image.clearImage(MainActivity.this, mRealm);
                    Stuff.clearAll(mRealm);
                    mClearCacheHandler.sendEmptyMessage(CLEAR_ALL);
                }
            });
        } else if (currIdx == mTypes.size() - 1) {
            CommonUtil.makeSnackBar(mCoordinatorLayout, getString(R.string.no_search_cache), Snackbar.LENGTH_SHORT);
        } else {
            final int strId = mTypes.get(currIdx).getStrId();
            final String apiName = getString(mTypes.get(currIdx).getApiId());
            if (strId != -1) {
                clearCacheSnackBar(strId, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currIdx == 0)
                            Image.clearImage(MainActivity.this, mRealm);
                        else
                            Stuff.clearType(mRealm, apiName);
                        mClearCacheHandler.sendEmptyMessage(CLEAR_DONE);
                    }
                });
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

    private void showSearchView() {
        if (mSearchView != null) {
            mSearchView.setVisibility(View.VISIBLE);
            int cx = mSearchView.getWidth() - (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 24, mSearchView.getResources().getDisplayMetrics());
            int cy = mSearchView.getHeight() / 2;
            int finalRadius = Math.max(mSearchView.getWidth(), mSearchView.getHeight());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                ViewAnimationUtils.createCircularReveal(mSearchView, cx, cy, 0, finalRadius).start();
        }

        if (mToolbar != null)
            mToolbar.setVisibility(View.GONE);
        updateSearchHint();
    }

    private void hideSearchView() {
        if (mSearchView != null)
            mSearchView.setVisibility(View.GONE);
        if (mToolbar != null)
            mToolbar.setVisibility(View.VISIBLE);
    }

    private void updateSearchHint() {
        String hint = getCurrSearchType();
        if (hint.equals(getString(R.string.api_all)))
            hint = getString(R.string.search_all);
        if (mSearchView != null)
            mSearchView.setQueryHint(String.format(getString(R.string.search), hint));
    }

    private String getCurrSearchType() {
        int currIdx = getCurrentItem();
        if (currIdx == 0
                || currIdx == mTypes.size() - 1
                || currIdx == mTypes.size() - 2)
            return getString(R.string.api_all);
        else
            return getString(mTypes.get(currIdx).getApiId());
    }

    private int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    private void updateLikedData() {
        if (getCurrentItem() == 0 || getCurrentItem() == mTypes.size() - 1) {
            return;
        }
        mCurrFragment.updateData();
    }

    class MainFragPagerAdapter extends FragmentStatePagerAdapter {
        public MainFragPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            BaseFragment fragment;
            if (position == 0) {
                fragment = GirlsFragment.newInstance(getString(mTypes.get(position).getApiId()));
            } else if (position == getCount() - 2) {
                fragment = CollectionFragment.newInstance(getString(mTypes.get(position).getApiId()));
            } else if (position == getCount() - 1) {
                fragment = SearchFragment.newInstance(mSearchKey, mSearchCat);
            } else {
                fragment = StuffFragment.newInstance(getString(mTypes.get(position).getApiId()));
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mTypes.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(mTypes.get(position).getStrId());
        }
    }


}
