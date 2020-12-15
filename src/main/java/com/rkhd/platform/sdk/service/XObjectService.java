package com.rkhd.platform.sdk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.rkhd.platform.sdk.exception.ApiEntityServiceException;
import com.rkhd.platform.sdk.http.RkhdHttpClient;
import com.rkhd.platform.sdk.http.RkhdHttpData;
import com.rkhd.platform.sdk.model.BatchOperateResult;
import com.rkhd.platform.sdk.model.OperateResult;
import com.rkhd.platform.sdk.model.QueryResult;
import com.rkhd.platform.sdk.model.XObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class XObjectService {
    private static final String API_V20_URL = "/rest/data/v2.0/xobjects/";
    private static final String QUERY_API_URI = "/rest/data/v2/query";
    private static final String DEFAULT_ENCODE = "UTF-8";
    private static final String MODEL_PACKAGE = "com.rkhd.platform.sdk.data.model";
    private static final String HEADER_XSY_EXECMODE = "xsy-execmode";
    private static final String HEADER_XSY_CRITERIA = "xsy-criteria";
    private static final String METHOD_GET_UPDATE_PROPERTIES = "_getUpdateProperties";
    private static final String METHOD_CLEAR_UPDATE_PROPERTIES = "_clearUpdateProperties";
    private static final String METHOD_GET_PROPERTIES = "_getProperties";
    private static final String CALL_TYPE_GET = "GET";
    private static final String CALL_TYPE_POST = "POST";
    private static final String CALL_TYPE_PATCH = "PATCH";
    private static final String CALL_TYPE_DELETE = "DELETE";
    private static final String CALL_TYPE_PUT = "PUT";
    private static final ValueFilter VALUE_FILTER = new DecimalValueFilter();
    private static XObjectService singleton = new XObjectService();

    public static XObjectService instance() {
        return singleton;
    }

    public <T extends XObject> QueryResult<T> query(String sql) throws ApiEntityServiceException {
        return query(sql, false);
    }

    public <T extends XObject> QueryResult<T> query(String sql, boolean admin) throws ApiEntityServiceException {
        QueryResult<T> queryResult = new QueryResult();
        try {
            JSONObject jsonObject = (JSONObject) RkhdHttpClient.instance().execute(
                    RkhdHttpData.newBuilder().callString("/rest/data/v2/query?q=" + URLEncoder.encode(sql, "UTF-8"))
                            .callType("GET").header("xsy-criteria", admin ? "10" : "").build(), JSON::parseObject);
            if (jsonObject == null) {
                queryResult.setCode(Long.valueOf(-10000L));
                queryResult.setSuccess(Boolean.valueOf(false));
                queryResult.setErrorMessage("");
                return queryResult;
            }
            log.warn("[xobject-service-query] sql: " + sql + ", response: " + jsonObject.toString());
            Long code = jsonObject.getLong("code");
            queryResult.setCode(code);
            if (code.longValue() != 200L) {
                String msg = jsonObject.getString("msg");
                queryResult.setSuccess(Boolean.valueOf(false));
                queryResult.setErrorMessage(msg);
                return queryResult;
            }
            String apiKey = extractTableName(sql);
            String modelClassName = getModelClass(apiKey);
            String fullClassName = "com.rkhd.platform.sdk.data.model." + modelClassName;

            Class<?> modelClass = Class.forName(fullClassName);
            log.warn("[xobject-service-query] apiKey: " + apiKey + ", fullClassName: " + fullClassName);
            JSONObject resultJsonObject = jsonObject.getJSONObject("result");
            queryResult.setSuccess(Boolean.valueOf(true));
            queryResult.setTotalCount(resultJsonObject.getLong("totalSize"));
            queryResult.setCurrentCount(resultJsonObject.getLong("count"));
            JSONArray recordsJsonArray = resultJsonObject.getJSONArray("records");
            List<T> records = queryResult.getRecords();

            for (int i = 0; i < recordsJsonArray.size(); i++) {
                JSONObject modelJsonObject = recordsJsonArray.getJSONObject(i);
                XObject xObject = (XObject) JSON.parseObject(modelJsonObject.toJSONString(), modelClass);
                clearUpdateProperties(xObject);
                records.add((T) xObject);
            }
        } catch (Exception e) {
            log.error("[xobject-service-query] error: " + e.getMessage(), e);
            throw new ApiEntityServiceException(e.getMessage(), e);
        }
        return queryResult;
    }

    public <T extends XObject> T get(T xObject) throws ApiEntityServiceException {
        return get(xObject, false);
    }

    public <T extends XObject> T get(T xObject, boolean admin) throws ApiEntityServiceException {
        try {
            checkXObject((XObject) xObject);
            String apiKey = xObject.getApiKey();
            Long id = xObject.getId();
            if (id == null) {
                throw new ApiEntityServiceException("id can not be null");
            }

            RkhdHttpData data = RkhdHttpData.newBuilder().callString("/rest/data/v2.0/xobjects/" + apiKey + "/" + id).callType("GET").header("xsy-criteria", admin ? "10" : "").build();
            JSONObject jsonObject = (JSONObject) RkhdHttpClient.instance().execute(data, JSON::parseObject);
            createLog(data, jsonObject.toString());
            checkResult(jsonObject);
            JSONObject jsonModle = jsonObject.getJSONObject("data");
            if (jsonModle == null) {
                throw new ApiEntityServiceException("result is null");
            }
            T resultXobject = convertJsonToXobject(jsonModle, apiKey);
            return resultXobject;
        } catch (ApiEntityServiceException e) {
            log.error(e.getMessage(), (Throwable) e);
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiEntityServiceException(e);
        }
    }

    public <T extends XObject> OperateResult insert(T xObject) throws ApiEntityServiceException {
        return insert(xObject, false);
    }

    public <T extends XObject> OperateResult insert(T xObject, boolean admin) throws ApiEntityServiceException {
        try {
            checkXObject((XObject) xObject);
            String apiKey = xObject.getApiKey();
            RkhdHttpData data = new RkhdHttpData();
            data.setCall_type("POST");
            data.setCallString("/rest/data/v2.0/xobjects/" + apiKey);
            Map<String, Object> properties = getProperties((XObject) xObject);
            String jsonBody = JSON.toJSONString(properties, (SerializeFilter) VALUE_FILTER, new SerializerFeature[0]);
            data.setBody("{\"data\":" + jsonBody + "}");

            setAdminPrivileges(admin, data);
            RkhdHttpClient rkhdHttpClient = new RkhdHttpClient();
            String result = rkhdHttpClient.performRequest(data);
            createLog(data, result);
            JSONObject jsonResult = JSON.parseObject(result);
            return createResultByJSONResult(jsonResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiEntityServiceException(e);
        }
    }

    public <T extends XObject> BatchOperateResult insert(List<T> xObjects) throws ApiEntityServiceException {
        return insert(xObjects, false, false);
    }

    public <T extends XObject> BatchOperateResult insert(List<T> xObjects, boolean partialSuccess) throws ApiEntityServiceException {
        return insert(xObjects, partialSuccess, false);
    }

    public <T extends XObject> BatchOperateResult insert(List<T> xObjects, boolean partialSuccess, boolean admin) throws ApiEntityServiceException {
        BatchOperateResult batchOperateResult = new BatchOperateResult();
        try {
            checkList(xObjects);
            String apikey = ((XObject) xObjects.get(0)).getApiKey();
            RkhdHttpData data = new RkhdHttpData();
            data.setCall_type("POST");
            data.setCallString("/rest/data/v2.0/xobjects/" + apikey + "/batch");
            JSONArray jsonArray = convertListToJSONArray(xObjects);
            StringBuilder sb = new StringBuilder();
            sb.append("{\"batchData\":").append(JSON.toJSONString(jsonArray, (SerializeFilter) VALUE_FILTER, new SerializerFeature[0])).append("}");
            data.setBody(sb.toString());

            setBatchExecmode(partialSuccess, data);

            setAdminPrivileges(admin, data);
            RkhdHttpClient rkhdHttpClient = new RkhdHttpClient();
            String result = rkhdHttpClient.performRequest(data);
            createLog(data, result);
            JSONObject json = JSON.parseObject(result);
            batchOperateResult.setCode(json.getLong("code"));
            if (json.getLong("code").longValue() != 200L) {
                batchOperateResult.setSuccess(Boolean.valueOf(false));
                batchOperateResult.setErrorMessage(json.getString("msg"));
                JSONArray jSONArray = json.getJSONArray("batchData");
                if (jSONArray != null) {
                    List<OperateResult> list = dealBatchResult(jSONArray);
                    batchOperateResult.setOperateResults(list);
                }
                return batchOperateResult;
            }
            JSONArray resultData = json.getJSONArray("batchData");
            List<OperateResult> operateResults = dealBatchResult(resultData);
            batchOperateResult.setSuccess(Boolean.valueOf(true));
            batchOperateResult.setOperateResults(operateResults);
            return batchOperateResult;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiEntityServiceException(e);
        }
    }

    public <T extends XObject> OperateResult update(T xobject) throws ApiEntityServiceException {
        return update(xobject, false);
    }

    public <T extends XObject> OperateResult update(T xobject, boolean admin) throws ApiEntityServiceException {
        try {
            checkXObject((XObject) xobject);
            String apikey = xobject.getApiKey();
            Long id = xobject.getId();
            if (id == null) {
                throw new ApiEntityServiceException("id can not be null");
            }
            RkhdHttpData data = new RkhdHttpData();
            data.setCall_type("PATCH");
            StringBuilder sb = new StringBuilder();
            sb.append("/rest/data/v2.0/xobjects/").append(apikey).append("/").append(id);
            data.setCallString(sb.toString());
            sb.delete(0, sb.length());
            String updateData = createUpdateData((XObject) xobject);
            sb.append("{\"data\":").append(updateData).append("}");
            data.setBody(sb.toString());

            setAdminPrivileges(admin, data);
            RkhdHttpClient rkhdHttpClient = new RkhdHttpClient();
            String resultString = rkhdHttpClient.performRequest(data);
            createLog(data, resultString);
            JSONObject resultJson = JSON.parseObject(resultString);
            return createResultByJSONResult(resultJson);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiEntityServiceException(e);
        }
    }

    public <T extends XObject> BatchOperateResult update(List<T> xObjects) throws ApiEntityServiceException {
        return update(xObjects, false, false);
    }

    public <T extends XObject> BatchOperateResult update(List<T> xObjects, boolean partialSuccess) throws ApiEntityServiceException {
        return update(xObjects, partialSuccess, false);
    }

    public <T extends XObject> BatchOperateResult update(List<T> xObjects, boolean partialSuccess, boolean admin) throws ApiEntityServiceException {
        BatchOperateResult batchOperateResult = new BatchOperateResult();
        try {
            checkList(xObjects);
            String apiKey = ((XObject) xObjects.get(0)).getApiKey();
            RkhdHttpData data = new RkhdHttpData();
            data.setCall_type("PATCH");
            StringBuilder sb = new StringBuilder();
            sb.append("/rest/data/v2.0/xobjects/").append(apiKey).append("/batch");
            data.setCallString(sb.toString());
            JSONArray jsonData = convertUpdateListToJSONArray(xObjects);
            sb.delete(0, sb.length());
            sb.append("{\"batchData\":").append(JSON.toJSONString(jsonData, (SerializeFilter) VALUE_FILTER, new SerializerFeature[0])).append("}");
            data.setBody(sb.toString());

            setBatchExecmode(partialSuccess, data);

            setAdminPrivileges(admin, data);
            RkhdHttpClient rkhdHttpClient = new RkhdHttpClient();
            String stringResult = rkhdHttpClient.performRequest(data);
            createLog(data, stringResult);
            JSONObject jsonResult = JSON.parseObject(stringResult);
            batchOperateResult.setCode(jsonResult.getLong("code"));
            if (jsonResult.getLong("code").longValue() != 200L) {
                batchOperateResult.setSuccess(Boolean.valueOf(false));
                batchOperateResult.setErrorMessage(jsonResult.getString("msg"));
                JSONArray resultData = jsonResult.getJSONArray("batchData");
                if (resultData != null) {
                    List<OperateResult> list = dealBatchResult(resultData);
                    batchOperateResult.setOperateResults(list);
                }
                return batchOperateResult;
            }
            List<OperateResult> operateResults = dealBatchResult(jsonResult.getJSONArray("batchData"));
            batchOperateResult.setSuccess(Boolean.valueOf(true));
            batchOperateResult.setOperateResults(operateResults);
            return batchOperateResult;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiEntityServiceException(e);
        }
    }

    public <T extends XObject> OperateResult delete(T xObject) throws ApiEntityServiceException {
        return delete(xObject, false);
    }

    public <T extends XObject> OperateResult delete(T xObject, boolean admin) throws ApiEntityServiceException {
        try {
            checkXObject((XObject) xObject);
            String apiKey = xObject.getApiKey();
            Long id = xObject.getId();
            if (id == null) {
                throw new ApiEntityServiceException("id can not be null");
            }
            RkhdHttpData data = new RkhdHttpData();
            data.setCall_type("DELETE");
            StringBuilder sb = new StringBuilder();
            sb.append("/rest/data/v2.0/xobjects/").append(apiKey).append("/").append(id);
            data.setCallString(sb.toString());

            setAdminPrivileges(admin, data);
            RkhdHttpClient rkhdHttpClient = new RkhdHttpClient();
            String stringResult = rkhdHttpClient.performRequest(data);
            createLog(data, stringResult);
            JSONObject jsonResult = JSON.parseObject(stringResult);
            return createResultByJSONResult(jsonResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiEntityServiceException(e);
        }
    }

    public <T extends XObject> BatchOperateResult delete(List<T> xobjects) throws ApiEntityServiceException {
        return delete(xobjects, false);
    }

    public <T extends XObject> BatchOperateResult delete(List<T> xobjects, boolean admin) throws ApiEntityServiceException {
        BatchOperateResult batchOperateResult = new BatchOperateResult();
        try {
            checkList(xobjects);
            String apiKey = ((XObject) xobjects.get(0)).getApiKey();
            RkhdHttpData data = new RkhdHttpData();
            data.setCall_type("DELETE");
            StringBuilder sb = new StringBuilder();
            sb.append("/rest/data/v2.0/xobjects/").append(apiKey).append("/batch");
            data.setCallString(sb.toString());
            sb.delete(0, sb.length());
            sb.append("{\"batchData\":").append(convertDeleteListToJSONArray(xobjects)).append("}");
            data.setBody(sb.toString());

            setAdminPrivileges(admin, data);
            RkhdHttpClient rkhdHttpClient = new RkhdHttpClient();
            String stringResult = rkhdHttpClient.performRequest(data);
            createLog(data, stringResult);
            JSONObject jsonResult = JSON.parseObject(stringResult);
            batchOperateResult.setCode(jsonResult.getLong("code"));
            if (jsonResult.getLong("code").longValue() != 200L) {
                batchOperateResult.setSuccess(Boolean.valueOf(false));
                batchOperateResult.setErrorMessage(jsonResult.getString("msg"));
                JSONArray resultData = jsonResult.getJSONArray("batchData");
                if (resultData != null) {
                    List<OperateResult> list = dealBatchResult(resultData);
                    batchOperateResult.setOperateResults(list);
                }
                return batchOperateResult;
            }
            List<OperateResult> operateResults = dealBatchResult(jsonResult.getJSONArray("batchData"));
            batchOperateResult.setSuccess(Boolean.valueOf(true));
            batchOperateResult.setOperateResults(operateResults);
            return batchOperateResult;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiEntityServiceException(e);
        }
    }

    public <T extends XObject> OperateResult lock(T xObject) throws ApiEntityServiceException {
        return lock(xObject, false);
    }

    public <T extends XObject> OperateResult lock(T xObject, boolean admin) throws ApiEntityServiceException {
        try {
            checkXObject((XObject) xObject);
            String apiKey = xObject.getApiKey();
            Long id = xObject.getId();
            if (id == null) {
                throw new ApiEntityServiceException("id can not be null");
            }
            RkhdHttpData data = new RkhdHttpData();
            data.setCall_type("PUT");
            StringBuilder sb = new StringBuilder();
            sb.append("/rest/data/v2.0/xobjects/").append(apiKey).append("/actions/locks/").append(id).append("/lock");
            data.setCallString(sb.toString());

            setAdminPrivileges(admin, data);
            RkhdHttpClient rkhdHttpClient = new RkhdHttpClient();
            String stringResult = rkhdHttpClient.performRequest(data);
            JSONObject jsonResult = JSON.parseObject(stringResult);
            return createResultByJSONResult(jsonResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiEntityServiceException(e);
        }
    }

    public <T extends XObject> OperateResult unlock(T xObject) throws ApiEntityServiceException {
        return unlock(xObject, false);
    }

    public <T extends XObject> OperateResult unlock(T xObject, boolean admin) throws ApiEntityServiceException {
        try {
            checkXObject((XObject) xObject);
            String apiKey = xObject.getApiKey();
            Long id = xObject.getId();
            if (id == null) {
                throw new ApiEntityServiceException("id can not be null");
            }
            RkhdHttpData data = new RkhdHttpData();
            data.setCall_type("DELETE");
            StringBuilder sb = new StringBuilder();
            sb.append("/rest/data/v2.0/xobjects/").append(apiKey).append("/actions/locks/").append(id).append("/lock");
            data.setCallString(sb.toString());

            setAdminPrivileges(admin, data);
            RkhdHttpClient rkhdHttpClient = new RkhdHttpClient();
            String stringResult = rkhdHttpClient.performRequest(data);
            JSONObject jsonResult = JSON.parseObject(stringResult);
            return createResultByJSONResult(jsonResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiEntityServiceException(e);
        }
    }

    public <T extends XObject> OperateResult transfer(T xObject, Long targetUserId) throws ApiEntityServiceException {
        return transfer(xObject, targetUserId, false);
    }

    public <T extends XObject> OperateResult transfer(T xObject, Long targetUserId, boolean admin) throws ApiEntityServiceException {
        try {
            checkXObject((XObject) xObject);
            String apiKey = xObject.getApiKey();
            Long id = xObject.getId();
            if (id == null) {
                throw new ApiEntityServiceException("id can not be null");
            }
            RkhdHttpData data = new RkhdHttpData();
            data.setCall_type("POST");
            StringBuilder sb = new StringBuilder();
            sb.append("/rest/data/v2.0/xobjects/").append(apiKey).append("/actions/transfers?recordId=")
                    .append(id).append("&targetUserId=").append(targetUserId);
            data.setCallString(sb.toString());

            setAdminPrivileges(admin, data);
            RkhdHttpClient rkhdHttpClient = new RkhdHttpClient();
            String stringResult = rkhdHttpClient.performRequest(data);
            JSONObject jsonResult = JSON.parseObject(stringResult);
            return createResultByJSONResult(jsonResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiEntityServiceException(e);
        }
    }

    private static String extractTableName(String sql) {
        String table, lowerSql = StringUtils.lowerCase(sql);
        int beginIndex = StringUtils.indexOf(lowerSql, " from ");
        String fromAfterSql = StringUtils.substring(sql, beginIndex + 5).trim();
        int endIndex = StringUtils.indexOf(fromAfterSql, " ");
        if (endIndex != -1) {
            table = StringUtils.substring(fromAfterSql, 0, endIndex).trim();
        } else {
            table = fromAfterSql;
        }
        return "_order".equalsIgnoreCase(table) ? "order" : table;
    }

    private static String getModelClass(String apiKey) {
        return Character.toUpperCase(apiKey.charAt(0)) + apiKey.substring(1);
    }

    private static void setAdminPrivileges(boolean admin, RkhdHttpData data) {
        if (admin) {
            Map<String, String> headerMap = data.getHeaderMap();

            headerMap.put("xsy-criteria", "10");
        }
    }

    private static void setBatchExecmode(boolean partialSuccess, RkhdHttpData data) {
        Map<String, String> headerMap = data.getHeaderMap();
        if (partialSuccess) {
            headerMap.put("xsy-execmode", "continue");
        } else {
            headerMap.put("xsy-execmode", "stop");
        }
    }

    private void checkResult(JSONObject result) throws ApiEntityServiceException {
        if (result.getLong("code").longValue() != 200L) {
            throw new ApiEntityServiceException(result.getString("msg"), result.getLong("code"));
        }
    }

    private void checkXObject(XObject xObject) throws ApiEntityServiceException {
        if (xObject == null) {
            throw new ApiEntityServiceException("xobject can not be null");
        }
        String apiKey = xObject.getApiKey();
        if (StringUtils.isBlank(apiKey)) {
            throw new ApiEntityServiceException("apikey can not be null");
        }
    }

    private <T extends XObject> void checkList(List<T> xObjects) throws ApiEntityServiceException {
        if (xObjects == null || xObjects.isEmpty()) {
            throw new ApiEntityServiceException("list is empty");
        }
        if (xObjects.contains(null)) {
            throw new ApiEntityServiceException("list can not contains null object");
        }
        int len = xObjects.size();
        for (int i = 1; i < len; i++) {
            if (((XObject) xObjects.get(i)).getApiKey() == null) {
                throw new ApiEntityServiceException("apiKey can not be null");
            }
            if (!((XObject) xObjects.get(i)).getApiKey().equals(((XObject) xObjects.get(i - 1)).getApiKey())) {
                throw new ApiEntityServiceException("xObject's apiKey must be same in the list");
            }
        }
    }

    private <T extends XObject> T convertJsonToXobject(JSONObject json, String apiKey) throws Exception {
        String className = "com.rkhd.platform.sdk.data.model." + apiKey.substring(0, 1).toUpperCase() + apiKey.substring(1);

        Class<?> clazz = Class.forName(className);

        XObject xObject = (XObject) JSON.parseObject(JSON.toJSONString(json, (SerializeFilter) VALUE_FILTER, new SerializerFeature[0]), clazz);
        clearUpdateProperties(xObject);
        return (T) xObject;
    }

    private <T extends XObject> JSONArray convertListToJSONArray(List<T> xObjects) throws Exception {
        JSONArray jsonArray = new JSONArray();

        for (XObject xObject : xObjects) {
            Map<String, Object> properties = getProperties(xObject);
            String objectString = JSON.toJSONString(properties, (SerializeFilter) VALUE_FILTER, new SerializerFeature[0]);
            JSONObject json = new JSONObject();
            json.put("data", JSON.parse(objectString));
            jsonArray.add(json);
        }
        return jsonArray;
    }

    private <T extends XObject> JSONArray convertUpdateListToJSONArray(List<T> xObjects) throws Exception {
        JSONArray jsonArray = new JSONArray();

        for (XObject xObject : xObjects) {
            Map<String, Object> updateProperties = getUpdateProperties(xObject);
            String objectString = JSON.toJSONString(updateProperties, (SerializeFilter) VALUE_FILTER, new SerializerFeature[0]);
            JSONObject jsonObject = JSON.parseObject(objectString);
            jsonObject.put("id", xObject.getId());
            JSONObject json = new JSONObject();
            json.put("data", jsonObject);
            jsonArray.add(json);
        }
        return jsonArray;
    }

    private <T extends XObject> JSONArray convertDeleteListToJSONArray(List<T> xObjects) {
        JSONArray jsonArray = new JSONArray();
        for (XObject xObject : xObjects) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", xObject.getId());
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("data", jsonObject);
            jsonArray.add(jsonObject1);
        }
        return jsonArray;
    }

    private String createUpdateData(XObject xObject) throws Exception {
        JSONObject updateData = new JSONObject();
        Map<String, Object> map = getUpdateProperties(xObject);
        updateData.put("id", xObject.getId());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            updateData.put(entry.getKey(), entry.getValue());
        }
        return JSON.toJSONString(updateData, (SerializeFilter) VALUE_FILTER, new SerializerFeature[]{SerializerFeature.WriteMapNullValue});
    }

    private <T extends XObject> void clearUpdateProperties(T xObject) throws Exception {
        Method clearMethod = XObject.class.getDeclaredMethod("_clearUpdateProperties", new Class[0]);
        clearMethod.setAccessible(true);
        clearMethod.invoke(xObject, new Object[0]);
    }

    private Map<String, Object> getUpdateProperties(XObject xObject) throws Exception {
        Method getMethod = XObject.class.getDeclaredMethod("_getUpdateProperties", new Class[0]);
        getMethod.setAccessible(true);
        return (Map<String, Object>) getMethod.invoke(xObject, new Object[0]);
    }

    private Map<String, Object> getProperties(XObject xObject) throws Exception {
        Method getMethod = XObject.class.getDeclaredMethod("_getProperties", new Class[0]);
        getMethod.setAccessible(true);
        return (Map<String, Object>) getMethod.invoke(xObject, new Object[0]);
    }

    private List<OperateResult> dealBatchResult(JSONArray resultData) {
        List<OperateResult> resultList = new ArrayList<>();
        for (int i = 0; i < resultData.size(); i++) {
            JSONObject object = resultData.getJSONObject(i);
            OperateResult saveResult = new OperateResult();
            if (object.containsKey("code")) {
                if (object.getLong("code").longValue() == 200L) {
                    saveResult.setSuccess(Boolean.valueOf(true));
                    JSONObject data = object.getJSONObject("data");
                    if (data != null) {
                        saveResult.setDataId(data.getLong("id"));
                    }
                } else {
                    saveResult.setSuccess(Boolean.valueOf(false));
                    saveResult.setErrorMessage(object.getString("msg"));
                }
            } else {
                saveResult.setSuccess(Boolean.valueOf(false));
                saveResult.setErrorMessage("Internal Server Error");
            }
            resultList.add(saveResult);
        }
        return resultList;
    }

    private OperateResult createResultByJSONResult(JSONObject jsonResult) {
        OperateResult dmlResult = new OperateResult();
        if (jsonResult.containsKey("code")) {
            dmlResult.setCode(jsonResult.getLong("code"));
            if (jsonResult.getLong("code").longValue() == 200L) {
                dmlResult.setSuccess(Boolean.valueOf(true));
                dmlResult.setDataId(jsonResult.getJSONObject("data").getLong("id"));
            } else {
                dmlResult.setSuccess(Boolean.valueOf(false));
                dmlResult.setErrorMessage(jsonResult.getString("msg"));
            }
        } else {
            dmlResult.setCode(Long.valueOf(-10000L));
            dmlResult.setSuccess(Boolean.valueOf(false));
            dmlResult.setErrorMessage("Internal Server Error");
        }
        return dmlResult;
    }

    private void createLog(RkhdHttpData data, String result) {
        StringBuilder sb = new StringBuilder();
        sb.append("request: [").append(data.toString()).append("]")
                .append(", response: ").append(result);
        log.info(sb.toString());
    }

    private static class DecimalValueFilter implements ValueFilter {
        private DecimalValueFilter() {
        }

        public Object process(Object object, String name, Object value) {
            if (value instanceof BigDecimal || value instanceof Double || value instanceof Float) {
                return new BigDecimal(value.toString());
            }
            return value;
        }
    }
}