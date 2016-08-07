package com.example.ivor_hu.meizhi.utils;

import com.example.ivor_hu.meizhi.R;

/**
 * Created by ivor on 16-6-3.
 */
public interface Constants {

    enum NETWORK_EXCEPTION {
        DEFAULT(0),
        UNKNOWN_HOST(R.string.network_not_avaiable),
        TIMEOUT(R.string.network_timeout),
        IOEXCEPTION(R.string.network_io),
        HTTP4XX(R.string.network_request_error),
        HTTP5XX(R.string.network_server_error);

        private int tipsResId;

        NETWORK_EXCEPTION(int tipsResId) {
            this.tipsResId = tipsResId;
        }

        public int getTipsResId() {
            return tipsResId;
        }
    }
}
