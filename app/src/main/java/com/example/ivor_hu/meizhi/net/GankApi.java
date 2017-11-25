package com.example.ivor_hu.meizhi.net;

import com.example.ivor_hu.meizhi.db.entity.Image;
import com.example.ivor_hu.meizhi.db.entity.SearchEntity;
import com.example.ivor_hu.meizhi.db.entity.Stuff;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Ivor on 2016/4/4.
 */
public interface GankApi {
    int DEFAULT_BATCH_NUM = 20;
    String HTTP_CODE_PREFIX_4 = "4";
    String HTTP_CODE_PREFIX_5 = "5";
    String BASE_URL = "http://gank.io/api/";
    String TYPE = "type";
    String PAGE = "page";
    String QUERY = "query";
    String COUNT = "count";
    String CATEGOTY = "category";
    String STUFF_URL = "data/{" + TYPE + "}/{" + COUNT + "}/{" + PAGE + "}";
    String GIRL_URL = "data/%E7%A6%8F%E5%88%A9/{" + COUNT + "}/{" + PAGE + "}";
    String SEARCH_URL = "search/query/{" + QUERY + "}/category/{" + CATEGOTY + "}/count/{" + COUNT + "}/page/{" + PAGE + "}";

    @GET(STUFF_URL)
    Call<Result<List<Stuff>>> fetchStuffs(@Path(TYPE) String type,
                                          @Path(COUNT) int count,
                                          @Path(PAGE) int page);

    @GET(GIRL_URL)
    Call<Result<List<Image>>> fetchGirls(@Path(COUNT) int count,
                                         @Path(PAGE) int page);

    @GET(SEARCH_URL)
    Call<Result<List<SearchEntity>>> search(
            @Path(QUERY) String keyword,
            @Path(CATEGOTY) String category,
            @Path(COUNT) int count,
            @Path(PAGE) int page);

    class Result<T> {
        public boolean error;
        public T results;
    }
}
