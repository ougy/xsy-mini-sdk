package com.rkhd.platform.sdk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.platform.sdk.exception.ApiEntityServiceException;
import com.rkhd.platform.sdk.http.CommonData;
import com.rkhd.platform.sdk.http.CommonHttpClient;
import com.rkhd.platform.sdk.http.CommonResponse;
import com.rkhd.platform.sdk.model.QueryResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class XoqlService {
    private static final String XOQL_QUERY_URL = "/rest/data/v2.0/query/xoql";
    private static XoqlService singleton = new XoqlService();

    public static XoqlService instance() {
        return singleton;
    }

    public QueryResult<JSONObject> query(String sql) throws ApiEntityServiceException {
        return query(sql, false);
    }

    public QueryResult<JSONObject> query(String sql, boolean admin) throws ApiEntityServiceException {
        QueryResult<JSONObject> queryResult = new QueryResult();

        try {
            CommonData commonData = (new CommonData.Builder()).callType("POST").callString("/rest/data/v2.0/query/xoql").formData("xoql", sql).header("Content-Type", "application/x-www-form-urlencoded").build();

            CommonHttpClient commonHttpClient = CommonHttpClient.instance();
            CommonResponse<JSONObject> commonResponse = commonHttpClient.execute(commonData, s -> {
                JSONObject resp = JSONObject.parseObject(s);
                return resp;
            });
            JSONObject jsonObject = commonResponse.getData();
            checkResult(jsonObject);
            Long code = jsonObject.getLong("code");
            if (code.longValue() != 200L) {
                String msg = jsonObject.getString("msg");
                queryResult.setSuccess(Boolean.valueOf(false));
                queryResult.setErrorMessage(msg);
                return queryResult;
            }
            JSONObject resultJsonObject = jsonObject.getJSONObject("data");
            queryResult.setSuccess(Boolean.valueOf(true));
            queryResult.setTotalCount(resultJsonObject.getLong("totalSize"));
            queryResult.setCurrentCount(resultJsonObject.getLong("count"));
            JSONArray jsonArray = resultJsonObject.getJSONArray("records");
            List<JSONObject> records = new ArrayList<>();
            for (Object obj : jsonArray) {
                Map map = (Map) obj;
                JSONObject data = new JSONObject(map);
                records.add(data);
            }
            queryResult.setRecords(records);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiEntityServiceException(e.getMessage(), e);
        }
        return queryResult;
    }

    private void checkResult(JSONObject result) throws ApiEntityServiceException {
        if (result == null) {
            throw new ApiEntityServiceException("result is null");
        }
        if (!result.containsKey("code")) {
            if (StringUtils.isNotBlank(result.getString("msg"))) {
                throw new ApiEntityServiceException(result.getString("msg"));
            }
            throw new ApiEntityServiceException(result.toString());
        }
    }
}