package com.wondertek.mam.util.network;

import org.apache.commons.httpclient.methods.EntityEnclosingMethod;

/**
 * Created by wangdongxu on 2017-09-26 下午 14:46:09.
 */
public class DeleteMethodWithBody extends EntityEnclosingMethod {
    public DeleteMethodWithBody() {
        super();
    }
    public DeleteMethodWithBody(String uri) {
        super(uri);
    }
    @Override
    public String getName() {
        return "DELETE";
    }
}
