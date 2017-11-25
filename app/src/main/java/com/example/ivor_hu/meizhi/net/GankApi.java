package com.example.ivor_hu.meizhi.net;

import com.example.ivor_hu.meizhi.db.Image;
import com.example.ivor_hu.meizhi.db.SearchBean;
import com.example.ivor_hu.meizhi.db.Stuff;
import com.google.gson.annotations.SerializedName;

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
    Call<Result<List<SearchBean>>> search(
            @Path(QUERY) String keyword,
            @Path(CATEGOTY) String category,
            @Path(COUNT) int count,
            @Path(PAGE) int page);

    @GET("data/%E7%A6%8F%E5%88%A9/{count}/1")
    Call<Result<List<Image>>> latestGirls(@Path("count") int count);

    @GET("day/{date}")
    Call<Result<Girls>> dayGirls(@Path("date") String date);

    @GET("data/{type}/{count}/1")
    Call<Result<List<Stuff>>> latestStuff(@Path("type") String type, @Path("count") int count);

    @GET("day/{date}")
    Call<Result<Androids>> dayAndroids(@Path("date") String date);

    @GET("day/{date}")
    Call<Result<IOSs>> dayIOSs(@Path("date") String date);

    @GET("day/{date}")
    Call<Result<Webs>> dayWebs(@Path("date") String date);

    @GET("day/{date}")
    Call<Result<Funs>> dayFuns(@Path("date") String date);

    @GET("day/{date}")
    Call<Result<Apps>> dayApps(@Path("date") String date);

    @GET("day/{date}")
    Call<Result<Others>> dayOthers(@Path("date") String date);

    class Result<T> {
        public boolean error;
        public T results;
    }

    class Girls {
        @SerializedName("福利")
        public List<Image> images;
    }

    class Androids {
        @SerializedName("Android")
        public List<Stuff> stuffs;
    }

    class IOSs {
        @SerializedName("iOS")
        public List<Stuff> stuffs;
    }

    class Funs {
        @SerializedName("瞎推荐")
        public List<Stuff> stuffs;
    }

    class Others {
        @SerializedName("扩展资源")
        public List<Stuff> stuffs;
    }

    class Webs {
        @SerializedName("前端")
        public List<Stuff> stuffs;
    }

    class Apps {
        @SerializedName("App")
        public List<Stuff> stuffs;
    }
}
