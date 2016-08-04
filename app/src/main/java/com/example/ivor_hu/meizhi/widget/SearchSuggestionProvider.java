package com.example.ivor_hu.meizhi.widget;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by ivor on 16-8-4.
 */
public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.example.ivor_hu.widget.SearchRecentSuggestionsProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
