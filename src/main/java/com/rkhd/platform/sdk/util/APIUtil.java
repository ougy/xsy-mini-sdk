package com.rkhd.platform.sdk.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.platform.sdk.common.OauthConfig;
import com.rkhd.platform.sdk.common.TokenCache;
import com.rkhd.platform.sdk.http.*;
import com.rkhd.platform.sdk.model.DataModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class APIUtil {
    private static final String HTTP_TYPE_POST = "POST";
    private static final String HTTP_TYPE_GET = "GET";
    private static final String HTTP_TYPE_PATCH = "PATCH";
    private static final String HTTP_TYPE_DELETE = "DELETE";
    private static final String ERROR_CODE = "error_code";
    private static final String QUERY_URI = "/data/v1/query";
    private static final String GET_INFO_BY_ID_URI = "/data/v1/objects/%s/info?id=%s";
    private static final String OBJECT_CREATE_URI = "/data/v1/objects/%s/create";
    private static final String OBJECT_UPDATE_URI = "/data/v1/objects/%s/update";
    private static final String OBJECT_DELETE_URI = "/data/v1/objects/%s/delete";
    private static final String QUERY_OBJECT_URI = "/data/v1/objects/%s/%s";
    private static final String STANDARD_OBJECT_DESCRIBE_URI = "/data/v1/objects/%s/describe";
    private static final String CUSTOMIZE_OBJECT_DESCRIBE_URI = "/data/v1/objects/customize/describe?belongId=%s";

    private static final String QUERY_URI_V2 = "/rest/data/v2/query?q=%s";
    private static final String GET_INFO_BY_ID_URI_V2 = "/rest/data/v2/objects/%s/%s";
    private static final String OBJECT_CREATE_URI_V2 = "/rest/data/v2/objects/%s";
    private static final String OBJECT_UPDATE_URI_V2 = "/rest/data/v2/objects/%s/%s";
    private static final String OBJECT_DESCRIBE_URI_V2 = "/rest/data/v2/objects/%s/description";

    private static final String OBJECT_CREATE_URI_DEV = "/rest/data/v2.0/xobjects/%s";
    private static final String OBJECT_UPDATE_URI_DEV = "/rest/data/v2.0/xobjects/%s/%s";
    private static final String OBJECT_DESCRIBE_URI_DEV = "/rest/data/v2.0/xobjects/%s/description";
    private static final String GET_INFO_BY_ID_URI_DEV = "/rest/data/v2.0/xobjects/%s/%s";

    private static final String COMMON_QUERY_URI;
    private static final String COMMON_GET_INFO_BY_ID_URI;
    private static final String COMMON_OBJECT_CREATE_URI;
    private static final String COMMON_OBJECT_UPDATE_URI;
    private static final String COMMON_OBJECT_DELETE_URI;
    private static final String COMMON_QUERY_OBJECT_URI;
    private static final String COMMON_STANDARD_OBJECT_DESCRIBE_URI;
    private static final String COMMON_CUSTOMIZE_OBJECT_DESCRIBE_URI;

    private static final String COMMON_QUERY_URI_V2;
    private static final String COMMON_GET_INFO_BY_ID_URI_V2;
    private static final String COMMON_OBJECT_CREATE_URI_V2;
    private static final String COMMON_OBJECT_UPDATE_URI_V2;
    private static final String COMMON_OBJECT_DESCRIBE_URI_V2;

    private static final String COMMON_OBJECT_CREATE_URI_DEV;
    private static final String COMMON_OBJECT_UPDATE_URI_DEV;
    private static final String COMMON_OBJECT_DESCRIBE_URI_DEV;
    private static final String COMMON_GET_INFO_BY_ID_URI_DEV;

    private static final String CREATE_ASYNC_JOB_URI_V2;
    private static final String CLOSE_ASYNC_JOB_URI_V2;
    private static final String CREATE_ASYNC_BATCH_URI_V2;

    private static final String DOMAIN;
    private static final String USER_NAME;
    private static final String PASSWORD;
    private static final String SECURITY_CODE;
    private static final String CLIENT_ID;
    private static final String CLIENT_SECRET;

    static {
        OauthConfig oauthConfig = new OauthConfig();
        DOMAIN = oauthConfig.getDomain();
        USER_NAME = oauthConfig.getUserName();
        PASSWORD = oauthConfig.getPassword();
        SECURITY_CODE = oauthConfig.getSecurityCode();
        CLIENT_ID = oauthConfig.getClientId();
        CLIENT_SECRET = oauthConfig.getClientSecret();

        COMMON_QUERY_URI = DOMAIN + "/data/v1/query";
        COMMON_GET_INFO_BY_ID_URI = DOMAIN + "/data/v1/objects/%s/info?id=%s";
        COMMON_OBJECT_CREATE_URI = DOMAIN + "/data/v1/objects/%s/create";
        COMMON_OBJECT_UPDATE_URI = DOMAIN + "/data/v1/objects/%s/update";
        COMMON_OBJECT_DELETE_URI = DOMAIN + "/data/v1/objects/%s/delete";
        COMMON_QUERY_OBJECT_URI = DOMAIN + "/data/v1/objects/%s/%s";
        COMMON_STANDARD_OBJECT_DESCRIBE_URI = DOMAIN + "/data/v1/objects/%s/describe";
        COMMON_CUSTOMIZE_OBJECT_DESCRIBE_URI = DOMAIN + "/data/v1/objects/customize/describe?belongId=%s";

        COMMON_QUERY_URI_V2 = DOMAIN + "/rest/data/v2/query?q=%s";
        COMMON_GET_INFO_BY_ID_URI_V2 = DOMAIN + "/rest/data/v2/objects/%s/%s";
        COMMON_OBJECT_CREATE_URI_V2 = DOMAIN + "/rest/data/v2/objects/%s";
        COMMON_OBJECT_UPDATE_URI_V2 = DOMAIN + "/rest/data/v2/objects/%s/%s";
        COMMON_OBJECT_DESCRIBE_URI_V2 = DOMAIN + "/rest/data/v2/objects/%s/description";

        COMMON_OBJECT_CREATE_URI_DEV = DOMAIN + "/rest/data/v2.0/xobjects/%s";
        COMMON_OBJECT_UPDATE_URI_DEV = DOMAIN + "/rest/data/v2.0/xobjects/%s/%s";
        COMMON_OBJECT_DESCRIBE_URI_DEV = DOMAIN + "/rest/data/v2.0/xobjects/%s/description";
        COMMON_GET_INFO_BY_ID_URI_DEV = DOMAIN + "/rest/data/v2.0/xobjects/%s/%s";

        CREATE_ASYNC_JOB_URI_V2 = DOMAIN + "/rest/bulk/v2/job";
        CLOSE_ASYNC_JOB_URI_V2 = DOMAIN + "/rest/bulk/v2/job/%s";
        CREATE_ASYNC_BATCH_URI_V2 = DOMAIN + "/rest/bulk/v2/batch";
    }

    public enum ItemType {

        SELECTITEM("selectitem"),
        CHECKITEM("checkitem");

        private String description;

        ItemType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return this.description;
        }

    }

    public enum APIVersion {
        V1,
        V2,
        DEV
    }

    public enum ObjectType {
        STANDARD,
        CUSTOMIZE
    }

    public static String constructorImpl(CommonHttpClient commonHttpClient) {
        String accessToken = TokenCache.getKey("accessToken");
        if (accessToken == null) {
            commonHttpClient.setContentEncoding("UTF-8");
            commonHttpClient.setContentType("application/json");
            String oauthUrl = DOMAIN + "/oauth2/token?grant_type=password" + "&client_id=" + CLIENT_ID
                    + "&client_secret=" + CLIENT_SECRET + "&username=" + USER_NAME + "&password=" + PASSWORD
                    + SECURITY_CODE;
            CommonData commonData = new CommonData();
            commonData.setCall_type(HTTP_TYPE_GET);
            commonData.setCallString(oauthUrl);

            HttpResult result = commonHttpClient.execute(commonData);
            if (result != null && StringUtils.isNotBlank(result.getResult())) {
                JSONObject jsonObject = JSONObject.parseObject(result.getResult());
                if (jsonObject.containsKey("access_token")) {
                    accessToken = jsonObject.getString("access_token");
                    log.debug("从中接口获取token:" + accessToken);
                    TokenCache.setKey("accessToken", accessToken);
                } else {
                    if (jsonObject.containsKey("error_description")) {
                        log.error(jsonObject.getString("error_description"));
                    } else {
                        log.error(jsonObject.toString());
                    }
                }
            } else {
                log.error("can not get the accessToken,please check your config");
            }
        }
        return accessToken;
    }

    /**
     * 查询对象，不分页，查询记录小于300条/v2小于100条
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param sql        SQL
     * @return JSON Object
     * @throws IOException IOException
     */
    public static JSONObject queryObjects(RkhdHttpClient httpClient,
                                          APIVersion version,
                                          String sql)
            throws IOException {

        JSONObject jsonObject = null;
        String result;

        RkhdHttpData httpData = new RkhdHttpData();

        if (version == APIVersion.V1) {
            httpData.setCall_type(HTTP_TYPE_POST);
            httpData.setCallString(QUERY_URI);
            httpData.putFormData("q", sql);

            result = httpClient.performRequest(httpData);

            if (result.contains(ERROR_CODE)) {
                String msg = String.format("在类%s方法%s发生错误: %s，查询SQL: %s", APIUtil.class.getName(), "queryObjects", result, sql);
                log.error(msg);
            } else {
                jsonObject = JSONObject.parseObject(result);
                log.debug(String.format("根据SQL查询: %s，查询结果: %s", sql, result));
            }

            return jsonObject;
        } else {
            String url = String.format(QUERY_URI_V2, URLEncoder.encode(sql, CharEncoding.UTF_8));
            httpData.setCall_type(HTTP_TYPE_GET);
            httpData.setCallString(url);
            result = httpClient.performRequest(httpData);

            return version == APIVersion.V2 ? parseV2ApiQueryResult(url, result, "result") :
                    parseV2ApiQueryResult(url, result, "data");
        }
    }

    /**
     * 根据id查询数据
     *
     * @param httpClient  HTTP Client
     * @param version     API版本
     * @param queryObject Query Object
     * @param id          id
     * @return Query Object
     * @throws IOException IOException
     */
    public static JSONObject queryObjectsById(RkhdHttpClient httpClient,
                                              APIVersion version,
                                              String queryObject,
                                              long id)
            throws IOException {

        RkhdHttpData httpData = new RkhdHttpData();
        String result;

        if (version == APIVersion.V1) {
            String url = String.format(GET_INFO_BY_ID_URI, queryObject, id);

            JSONObject jsonObject = null;
            httpData.setCall_type(HTTP_TYPE_GET);
            httpData.setCallString(url);

            result = httpClient.performRequest(httpData);

            if (result.contains(ERROR_CODE)) {
                String msg = String.format("在类%s方法%s发生错误: %s，查询url: %s", APIUtil.class.getName(), "queryObjects", result, url);
                log.error(msg);
            } else {
                jsonObject = JSONObject.parseObject(result);
                log.debug(String.format("根据id查询: %s，查询结果: %s", url, result));
            }

            return jsonObject;
        } else {
            String url = (version == APIVersion.V2) ?
                    String.format(GET_INFO_BY_ID_URI_V2, queryObject, id) :
                    String.format(GET_INFO_BY_ID_URI_DEV, queryObject, id);

            httpData.setCall_type(HTTP_TYPE_GET);
            httpData.setCallString(url);

            result = httpClient.performRequest(httpData);

            return version == APIVersion.V2 ?
                    parseV2ApiQueryResult(url, result, "result") :
                    parseV2ApiQueryResult(url, result, "data");
        }
    }

    /**
     * 查询单条记录
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param sql        SQL
     * @return records里面的单条记录
     * @throws IOException IOException
     */
    public static JSONObject querySingleObject(RkhdHttpClient httpClient,
                                               APIVersion version,
                                               String sql)
            throws IOException {

        JSONObject queryObject = (version == APIVersion.V1) ? APIUtil.queryObjects(httpClient, APIVersion.V1, sql) :
                APIUtil.queryObjects(httpClient, APIVersion.V2, sql);

        JSONObject returnObject = null;

        if (queryObject != null) {
            JSONArray jsonArray = queryObject.getJSONArray("records");

            if (jsonArray != null && jsonArray.size() > 0) {
                returnObject = jsonArray.getJSONObject(0);
            }
        }

        if (returnObject != null) {
            log.debug(String.format("根据SQL: %s 查询单条记录结果: %s", sql, returnObject.toString()));
        } else {
            log.warn(String.format("根据SQL: %s 查询单条记录结果: %s", sql, "查找不到结果"));
        }

        return returnObject;
    }


    /**
     * 查询单个对象
     *
     * @param httpClient  HTTP Client
     * @param queryObject 查询的对象名称
     * @param path        对象路径
     * @param params      参数
     * @return 查询对象
     * @throws IOException IOException
     */
    @Deprecated
    public static JSONObject querySingleObject(RkhdHttpClient httpClient,
                                               String queryObject,
                                               String path,
                                               Map<String, Object> params)
            throws IOException {
        String uri = String.format(QUERY_OBJECT_URI, queryObject, path);

        JSONObject jsonObject = null;

        RkhdHttpData httpData = new RkhdHttpData();
        httpData.setCall_type(HTTP_TYPE_POST);
        httpData.setCallString(uri);

        for (String key : params.keySet()) {
            httpData.putFormData(key, params.get(key));
        }

        String result = httpClient.performRequest(httpData);

        if (result.contains(ERROR_CODE)) {
            String msg = String.format("在类%s方法%s发生错误: %s，查询url: %s", APIUtil.class.getName(), "querySingleObject", result, uri);
            log.error(msg);
        } else {
            jsonObject = JSONObject.parseObject(result);
            log.debug(String.format("查询单个对象查询: %s，查询结果: %s", uri, result));
        }

        return jsonObject;
    }

    /**
     * 分页查询记录
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param sql        SQL
     * @return 查询对象数组
     * @throws IOException IOException
     */
    public static JSONArray queryList(RkhdHttpClient httpClient,
                                      APIVersion version,
                                      String sql)
            throws IOException {

        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();

        int start = 0, totalSize;

        final int PAGE_SIZE = version == APIVersion.V1 ? 300 : 100; //每页300行记录

        RkhdHttpData httpData = new RkhdHttpData();

        if (version == APIVersion.V1) {

            httpData.setCall_type(HTTP_TYPE_POST);
            httpData.setCallString(QUERY_URI);

            //先查询总数量
            String soql = String.format("%s limit %s, %s", sql, start, PAGE_SIZE);

            log.debug(String.format("分页查询记录，查询SQL: %s", soql));

            httpData.putFormData("q", soql);
            String result = httpClient.performRequest(httpData);
            jsonObject = JSONObject.parseObject(result);

            if (jsonObject.containsKey("totalSize") && jsonObject.containsKey("count")
                    && jsonObject.containsKey("records")) {

                totalSize = jsonObject.getInteger("totalSize");

                //分页查询，每页300条
                for (int i = 0; i < Math.ceil((double) totalSize / PAGE_SIZE); i++) {
                    soql = String.format("%s limit %s, %s", sql, i * PAGE_SIZE,
                            PAGE_SIZE);

                    JSONObject obj = APIUtil.queryObjects(httpClient, APIVersion.V1, soql);
                    JSONArray arr = obj.getJSONArray("records");
                    jsonArray.addAll(arr);
                }

                return jsonArray;
            } else {
                log.warn(String.format("分页查询查找不到数据:%s", soql));
                return null;
            }
        } else {
            httpData.setCall_type(HTTP_TYPE_GET);

            //先查询总数量
            String urlParam = String.format("%s limit %s, %s", sql, start, PAGE_SIZE);
            String url = String.format(QUERY_URI_V2, URLEncoder.encode(urlParam, CharEncoding.UTF_8));

            log.debug(String.format("分页查询记录，查询路径: %s", URLDecoder.decode(url, CharEncoding.UTF_8)));

            httpData.setCallString(url);
            String result = httpClient.performRequest(httpData);
            JSONObject returnObject = JSONObject.parseObject(result);

            if (returnObject.containsKey("code") && returnObject.containsKey("result")) {

                JSONObject recordObject = returnObject.getJSONObject("result");

                totalSize = APIUtil.getInt(APIUtil.getObjectAttribute(recordObject, "totalSize"));

                //分页查询，每页100条
                for (int i = 0; i < Math.ceil((double) totalSize / PAGE_SIZE); i++) {
                    urlParam = String.format("%s limit %s, %s", sql, i * PAGE_SIZE,
                            PAGE_SIZE);

                    JSONObject obj = APIUtil.queryObjects(httpClient, APIVersion.V2, urlParam);
                    JSONArray arr = obj.getJSONArray("records");
                    jsonArray.addAll(arr);
                }

                return jsonArray;
            } else {
                log.warn(String.format("分页查询查找不到数据:%s，", url));
                return null;
            }
        }
    }

    /**
     * 更新自定义对象
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param jsonString JSON字符串
     * @param objName    对象名称，仅对V2版本有效，V1版本传入空值
     * @param id         对象id，仅对V2版本有效，V1版本传入0L
     * @return 返回消息
     * @throws IOException IOException
     */
    public static String updateCustomizeObject(RkhdHttpClient httpClient,
                                               APIVersion version,
                                               String jsonString,
                                               String objName,
                                               long id)
            throws IOException {

        return (version == APIVersion.V1) ?
                updateObjectV1(httpClient, ObjectType.CUSTOMIZE, "", jsonString) :
                updateObjectV2(httpClient, version, objName, id, jsonString);

    }

    /**
     * V1 API 更新对象
     *
     * @param httpClient HTTP Client
     * @param objectType 对象类型
     * @param objName    对象名称
     * @param jsonString json字符串
     * @return 更新结果
     * @throws IOException IOException
     */
    private static String updateObjectV1(RkhdHttpClient httpClient,
                                         ObjectType objectType,
                                         String objName,
                                         String jsonString
    ) throws IOException {

        RkhdHttpData httpData = new RkhdHttpData();
        httpData.setCall_type(HTTP_TYPE_POST);

        String url = (objectType == ObjectType.CUSTOMIZE) ?
                String.format(OBJECT_UPDATE_URI, "customize") :
                String.format(OBJECT_UPDATE_URI, objName);

        httpData.setCallString(url);
        log.debug(String.format("更新对象url: %s", url));
        httpData.setBody(jsonString);
        String msg = httpClient.performRequest(httpData);

        if (msg.contains(ERROR_CODE)) {
            log.error(String.format("更新对象错误: %s，更新内容: %s", msg, jsonString));
        } else {
            log.debug(String.format("更新对象结果: %s，更新内容: %s", msg, jsonString));
        }

        return msg;
    }

    /**
     * V2 API更新对象
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param objName    对象名称
     * @param id         对象id
     * @param jsonString 更新的JSON字符串
     * @return 更新结果
     * @throws IOException IOException
     */
    private static String updateObjectV2(RkhdHttpClient httpClient,
                                         APIVersion version,
                                         String objName,
                                         long id,
                                         String jsonString)
            throws IOException {

        RkhdHttpData httpData = new RkhdHttpData();

        String url = (version == APIVersion.V2) ? String.format(OBJECT_UPDATE_URI_V2, objName, id) :
                String.format(OBJECT_UPDATE_URI_DEV, objName, id);

        httpData.setCall_type(HTTP_TYPE_PATCH);
        httpData.setCallString(url);
        JSONObject dataObject = new JSONObject();
        dataObject.put("data", JSONObject.parseObject(jsonString));
        httpData.setBody(dataObject.toString());

        String msg = httpClient.performRequest(httpData);
        JSONObject msgObject = JSONObject.parseObject(msg);

        if (msgObject.containsKey("code") && msgObject.containsKey("msg")) {
            if (APIUtil.getInt(APIUtil.getObjectAttribute(msgObject, "code")) == 200) {
                log.debug(String.format("更新对象结果: %s，更新url: %s, 更新内容: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url, dataObject.toString()));
            } else {
                log.error(String.format("更新对象错误: %s，更新url: %s, 更新内容: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url, dataObject.toString()));
            }
        } else {
            log.error(String.format("更新对象结果错误，更新url: %s, 更新内容: %s", url, dataObject.toString()));
        }
        return msg;
    }

    /**
     * V1 API创建对象
     *
     * @param httpClient HTTP Client
     * @param objectType 对象类型
     * @param objName    对象名称
     * @param jsonString 创建对象json字符串
     * @return 创建信息
     * @throws IOException IOException
     */
    private static String createObjectV1(RkhdHttpClient httpClient,
                                         ObjectType objectType,
                                         String objName,
                                         String jsonString)
            throws IOException {
        RkhdHttpData httpData = new RkhdHttpData();
        httpData.setCall_type(HTTP_TYPE_POST);

        String url = (objectType == ObjectType.CUSTOMIZE) ?
                String.format(OBJECT_CREATE_URI, "customize") :
                String.format(OBJECT_CREATE_URI, objName);

        log.debug(String.format("新建对象url: %s", url));
        httpData.setBody(jsonString);
        httpData.setCallString(url);
        String msg = httpClient.performRequest(httpData);

        if (msg.contains(ERROR_CODE)) {
            log.error(String.format("新建自定义对象错误: %s，新建参数: %s", msg, jsonString));
        } else {
            log.debug(String.format("新建自定义对象结果: %s，新建参数: %s", msg, jsonString));
        }

        return msg;
    }

    /**
     * 新建标准对象
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param jsonString JSON字符串
     * @return 返回消息
     * @throws IOException IOException
     */
    public static String createStandardObject(RkhdHttpClient httpClient,
                                              APIVersion version,
                                              String objName,
                                              String jsonString)
            throws IOException {
        return (version == APIVersion.V1) ?
                createObjectV1(httpClient, ObjectType.STANDARD, objName, jsonString) :
                createObjectV2(httpClient, version, objName, jsonString);
    }

    /**
     * 创建自定义对象
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param objName    对象名称
     * @param jsonString 新建的json字符串
     * @return 创建信息
     * @throws IOException IOException
     */
    public static String createCustomizeObject(RkhdHttpClient httpClient,
                                               APIVersion version,
                                               String objName,
                                               String jsonString)
            throws IOException {
        return (version == APIVersion.V1) ?
                createObjectV1(httpClient, ObjectType.CUSTOMIZE, objName, jsonString) :
                createObjectV2(httpClient, version, objName, jsonString);
    }

    /**
     * V2 API创建对象
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param objName    对象名称
     * @param jsonString JSON字符串
     * @return 创建结果
     * @throws IOException IOException
     */
    private static String createObjectV2(RkhdHttpClient httpClient,
                                         APIVersion version,
                                         String objName,
                                         String jsonString)
            throws IOException {

        RkhdHttpData httpData = new RkhdHttpData();
        JSONObject dataObject = new JSONObject();
        dataObject.put("data", JSONObject.parseObject(jsonString));

        String url = (version == APIVersion.V2) ? String.format(OBJECT_CREATE_URI_V2, objName) :
                String.format(OBJECT_CREATE_URI_DEV, objName);

        httpData.setCall_type(HTTP_TYPE_POST);
        httpData.setCallString(url);
        httpData.setBody(dataObject.toString());
        String msg = httpClient.performRequest(httpData);
        JSONObject msgObject = JSONObject.parseObject(msg);

        if (msgObject.containsKey("code") && msgObject.containsKey("msg")) {
            if (APIUtil.getInt(APIUtil.getObjectAttribute(msgObject, "code")) == 200) {
                log.debug(String.format("创建对象结果: %s，创建url: %s, 创建内容: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url, dataObject.toString()));
            } else {
                log.error(String.format("创建对象错误: %s，创建url: %s, 创建内容: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url, dataObject.toString()));
            }

        } else {
            log.error(String.format("创建对象结果错误，创建url: %s, 创建内容: %s", url, dataObject.toString()));
        }

        return msg;
    }

    /**
     * 删除自定义对象
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param objName    对象名称 仅对V2版本有效，V1版本传入空值
     * @param id         对象id 仅对V2版本有效，V1版本传入0
     * @return 返回消息
     * @throws IOException IOException
     */
    public static String deleteCustomizeObject(RkhdHttpClient httpClient,
                                               APIVersion version,
                                               String objName,
                                               long id)
            throws IOException {

        return (version == APIVersion.V1) ?
                deleteObjectV1(httpClient, ObjectType.CUSTOMIZE, objName, id) :
                deleteObjectV2(httpClient, version, objName, id);
    }

    /**
     * V2 API删除对象
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param objName    对象名称
     * @param id         对象id
     * @return 删除结果
     * @throws IOException IOException
     */
    private static String deleteObjectV2(RkhdHttpClient httpClient,
                                         APIVersion version,
                                         String objName,
                                         long id)
            throws IOException {
        RkhdHttpData httpData = new RkhdHttpData();

        String url = (version == APIVersion.V2) ? String.format(OBJECT_UPDATE_URI_V2, objName, id) :
                String.format(OBJECT_UPDATE_URI_DEV, objName, id);

        httpData.setCall_type(HTTP_TYPE_DELETE);
        httpData.setCallString(url);

        String msg = httpClient.performRequest(httpData);
        JSONObject msgObject = JSONObject.parseObject(msg);

        if (msgObject.containsKey("code") && msgObject.containsKey("msg")) {
            if (APIUtil.getInt(APIUtil.getObjectAttribute(msgObject, "code")) == 200) {
                log.debug(String.format("删除对象结果: %s，删除url: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url));
            } else {
                log.error(String.format("删除对象错误: %s，删除url: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url));
            }
        } else {
            log.error(String.format("删除对象结果错误，删除url: %s", url));
        }

        return msg;
    }

    /**
     * 删除标准对象
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param id         对象id
     * @return 返回消息
     * @throws IOException IOException
     */
    public static String deleteStandardObject(RkhdHttpClient httpClient,
                                              APIVersion version,
                                              String objName,
                                              long id)
            throws IOException {

        return (version == APIVersion.V1) ?
                deleteObjectV1(httpClient, ObjectType.STANDARD, objName, id) :
                deleteObjectV2(httpClient, version, objName, id);
    }

    /**
     * V1 API删除对象
     *
     * @param httpClient HTTP Client
     * @param objectType 对象类型
     * @param objName    对象名称
     * @param id         对象id
     * @return 删除结果
     * @throws IOException IOException
     */
    private static String deleteObjectV1(RkhdHttpClient httpClient,
                                         ObjectType objectType,
                                         String objName,
                                         long id)
            throws IOException {
        RkhdHttpData httpData = new RkhdHttpData();

        String url = (objectType == ObjectType.STANDARD) ?
                String.format(OBJECT_DELETE_URI, objName) :
                String.format(OBJECT_DELETE_URI, "customize");

        log.debug(String.format("删除对象url：%s", url));

        httpData.setCall_type(HTTP_TYPE_POST);
        httpData.setCallString(url);
        httpData.putFormData("id", id);

        String msg = httpClient.performRequest(httpData);

        if (msg.contains(ERROR_CODE)) {
            log.error(String.format("删除对象错误: %s，对象名称: %s，对象id: %s", msg, objName, id));
        } else {
            log.debug(String.format("删除对象结果: %s，对象名称: %s，对象id: %s", msg, objName, id));
        }

        return msg;
    }

    /**
     * 更新标准对象
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param objName    对象名称
     * @param id         对象id，仅适用V2 API, V1传入0L
     * @param jsonString 更新传入的JSON字符串
     * @return 更新结果
     * @throws IOException IOException
     */
    public static String updateStandardObject(RkhdHttpClient httpClient,
                                              APIVersion version,
                                              String objName,
                                              long id,
                                              String jsonString)
            throws IOException {

        return (version == APIVersion.V1) ?
                updateObjectV1(httpClient, ObjectType.STANDARD, objName, jsonString) :
                updateObjectV2(httpClient, version, objName, id, jsonString);

    }

    /**
     * 获取选择型字段label描述
     *
     * @param httpClient   HTTP Client
     * @param objectName   对象名称
     * @param propertyName 属性名称
     * @param itemType     类型：selectitem，checkitem
     * @param value        字段对应值
     * @return 字段对应描述
     * @throws IOException IOException
     */
    public static String getSingleSelectItemLabel(RkhdHttpClient httpClient,
                                                  String objectName,
                                                  String propertyName,
                                                  APIVersion version,
                                                  ItemType itemType,
                                                  int value)
            throws IOException {

        Map<String, Map<Integer, String>> itemsLabelMap = getSelectItemLabels(httpClient,
                version, objectName, itemType);

        if (itemsLabelMap.containsKey(propertyName)) {
            Map<Integer, String> labelMap = itemsLabelMap.get(propertyName);

            if (labelMap.containsKey(value)) {
                String desc = labelMap.get(value);
                log.debug(String.format("查询选择型字段描述，查询对象名: %s, 查询字段: %s, 查询值: %s, 字段描述值: %s",
                        objectName, propertyName, value, desc));
                return desc;
            }
        }

        log.error(String.format("查询选择型字段描述，查询对象名: %s, 查询字段: %s, 查询值: %s, 查找不到描述值。",
                objectName, propertyName, value));

        return "";
    }

    /**
     * 获取选择型字段label描述
     *
     * @param itemsLabelMap 对象所有字段描述集合
     * @param propertyName  字段名称
     * @param value         字段值
     * @return 字段描述
     */
    public static String getSingleSelectItemLabel(Map<String, Map<Integer, String>> itemsLabelMap,
                                                  String propertyName,
                                                  int value) {

        if (itemsLabelMap.containsKey(propertyName)) {
            Map<Integer, String> labelMap = itemsLabelMap.get(propertyName);

            if (labelMap.containsKey(value)) {
                String desc = labelMap.get(value);
                log.debug(String.format("查询选择型字段描述，查询字段: %s, 查询值: %s, 字段描述值: %s",
                        propertyName, value, desc));
                return desc;
            }

            labelMap.clear();
        }

        log.debug(String.format("查询选择型字段描述，查询字段: %s, 查询值: %s, 查找不到描述值。",
                propertyName, value));

        return "";

    }

    /**
     * 根据字段描述值获取字段值
     *
     * @param itemsLabelMap 选择型字段描述值
     * @param propertyName  属性值
     * @param label         字段描述
     * @return 字段值
     */
    public static int getSingleSelectItemValue(Map<String, Map<Integer, String>> itemsLabelMap,
                                               String propertyName,
                                               String label) {

        if (itemsLabelMap.containsKey(propertyName)) {
            Map<Integer, String> labelMap = itemsLabelMap.get(propertyName);

            for (Map.Entry<Integer, String> entrySet : labelMap.entrySet()) {
                if (entrySet.getValue().trim().equals(label.trim())) {
                    int value = entrySet.getKey();
                    log.debug(String.format("查询选择型字段值，查询字段: %s, 查询描述: %s, 字段值: %s",
                            propertyName, label, value));
                    return value;
                }
            }

            labelMap.clear();
        }

        return 0;
    }

    /**
     * 根据对象名、选项类型获取所有选项的label描述值
     *
     * @param httpClient HTTP Client
     * @param version    API版本
     * @param objectName 标准对象名称
     * @param itemType   类型：selectitem，checkitem
     * @return 所有选择型字段对应的描述
     * @throws IOException IOException
     */
    public static Map<String, Map<Integer, String>> getSelectItemLabels(RkhdHttpClient httpClient,
                                                                        APIVersion version,
                                                                        String objectName,
                                                                        ItemType itemType)
            throws IOException {

        String url = "";

        switch (version) {
            case V1:
                url = String.format(STANDARD_OBJECT_DESCRIBE_URI, objectName);
                break;
            case V2:
                url = String.format(OBJECT_DESCRIBE_URI_V2, objectName);
                break;
            case DEV:
                url = String.format(OBJECT_DESCRIBE_URI_DEV, objectName);
                break;
            default:
                break;
        }
        log.debug(String.format("查找标准对象选择型字段描述值，查找url: %s", url));
        return separateItemsLabelsFromObjectDescription(httpClient, version, url, itemType);
    }

    /**
     * 根据对象所属id、选项类型获取所有选项的label描述值，仅适用V1版本API
     *
     * @param httpClient HTTP Client
     * @param belongId   自定义对象id
     * @param itemType   类型：selectitem，checkitem
     * @return 所有选择型字段对应的描述
     * @throws IOException IOException
     */
    public static Map<String, Map<Integer, String>> getSelectItemLabels(RkhdHttpClient httpClient,
                                                                        long belongId,
                                                                        ItemType itemType)
            throws IOException {

        String url = String.format(CUSTOMIZE_OBJECT_DESCRIBE_URI, belongId);
        log.debug(String.format("查找自定义对象选择型字段描述值，查找url: %s", url));

        return separateItemsLabelsFromObjectDescription(httpClient, APIVersion.V1, url, itemType);
    }

    /**
     * 从对象描述中分离出选择型字段的描述
     *
     * @param httpClient HTTP Client
     * @param url        对象描述url
     * @param itemType   类型：selectitem，checkitem
     * @return 所有选择型字段对应的描述
     * @throws IOException IOException
     */
    private static Map<String, Map<Integer, String>> separateItemsLabelsFromObjectDescription(RkhdHttpClient httpClient,
                                                                                              APIVersion version,
                                                                                              String url,
                                                                                              ItemType itemType)
            throws IOException {

        RkhdHttpData httpData = new RkhdHttpData();
        httpData.setCall_type(HTTP_TYPE_GET);
        httpData.setCallString(url);

        String msg = httpClient.performRequest(httpData);

        String rootNodeField; //api根节点
        String fieldNameKey; //属性字段名
        switch (version) {
            case V1:
                rootNodeField = "fields";
                fieldNameKey = "propertyname";
                break;
            case V2:
                rootNodeField = "data";
                fieldNameKey = "apikey";
                break;
            case DEV:
                rootNodeField = "data";
                fieldNameKey = "apiKey";
                break;
            default:
                rootNodeField = "";
                fieldNameKey = "";
                break;
        }

        //描述Map，key:字段名，value: item的值-item项标签
        Map<String, Map<Integer, String>> itemsLabelMap = new HashMap<>();

        JSONObject describeObject;
        if (msg != null && !msg.isEmpty() && msg.contains(rootNodeField)) {

            if (version == APIVersion.V1) {
                describeObject = JSONObject.parseObject(msg);
            } else {
                JSONObject rootObject = JSONObject.parseObject(msg);
                describeObject = rootObject.getJSONObject("data");
            }

            if (describeObject != null) {
                JSONArray fieldsArray = describeObject.getJSONArray("fields");
                JSONObject fieldObject;
                JSONObject itemObject;
                JSONArray itemArray;
                Map<Integer, String> labelMap;

                for (int i = 0; i < fieldsArray.size(); i++) {
                    fieldObject = fieldsArray.getJSONObject(i);
                    if (fieldObject == null) {
                        continue;
                    }
                    if (!fieldObject.containsKey(fieldNameKey)) {
                        continue;
                    }
                    if (!fieldObject.containsKey(itemType.getDescription())) {
                        continue;
                    }

                    itemArray = fieldObject.getJSONArray(itemType.getDescription());

                    labelMap = new HashMap<>();

                    if (itemArray != null && itemArray.size() > 0) {
                        for (int j = 0; j < itemArray.size(); j++) {
                            itemObject = itemArray.getJSONObject(j);
                            if (itemObject != null
                                    && itemObject.size() > 0
                                    && itemObject.containsKey("value")
                                    && itemObject.containsKey("label")) {
                                if (version == APIVersion.DEV) {
                                    labelMap.put(itemObject.getInteger("label"), itemObject.getString("value"));
                                } else {
                                    labelMap.put(itemObject.getInteger("value"), itemObject.getString("label"));
                                }
                            }
                        }
                    }
                    if (labelMap.size() > 0) {
                        itemsLabelMap.put(fieldObject.getString(fieldNameKey), labelMap);
                    }

                }
            }
        }

        return itemsLabelMap;

    }

    /**
     * V2版API返回结果处理函数
     *
     * @param url          查询URL
     * @param resultString 查询返回字符串
     * @return 查询结果
     */
    private static JSONObject parseV2ApiQueryResult(String url,
                                                    String resultString,
                                                    String dataKey)
            throws IOException {
        JSONObject queryObject = JSONObject.parseObject(resultString);

        if (!queryObject.containsKey("code") &&
                !queryObject.containsKey("msg") &&
                !queryObject.containsKey(dataKey)) {

            log.error(String.format("查询url: %s，查询错误", URLDecoder.decode(url, CharEncoding.UTF_8)));
            return null;
        } else {
            int code = APIUtil.getInt(APIUtil.getObjectAttribute(queryObject, "code"));
            String msg = APIUtil.getObjectAttribute(queryObject, "msg");

            if (code != 200) {
                log.error(String.format("查询url: %s，查询错误: %s", URLDecoder.decode(url, CharEncoding.UTF_8), msg));
                return null;
            } else {
                JSONObject resultObject = queryObject.getJSONObject(dataKey);

                log.debug(String.format("查询url: %s，查询结果: %s", URLDecoder.decode(url, CharEncoding.UTF_8), resultObject));
                return resultObject;

            }
        }
    }

    /**
     * 查询对象，不分页，查询记录小于300条/v2小于100条
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param sql              SQL
     * @return JSON Object
     * @throws IOException IOException
     */
    public static JSONObject queryObjects(CommonHttpClient commonHttpClient,
                                          APIVersion version,
                                          String sql)
            throws IOException {

        JSONObject jsonObject = null;
        String result;

        CommonData commonData = new CommonData();
        String accessToken = constructorImpl(commonHttpClient);
        commonData.putHeader("Authorization", accessToken);
        if (version == APIVersion.V1) {
            commonData.setCall_type(HTTP_TYPE_POST);
            commonData.setCallString(COMMON_QUERY_URI);
            commonData.putFormData("q", sql);

            result = commonHttpClient.performRequest(commonData);

            if (result.contains(ERROR_CODE)) {
                String msg = String.format("在类%s方法%s发生错误: %s，查询SQL: %s", APIUtil.class.getName(), "queryObjects", result, sql);
                log.error(msg);
            } else {
                jsonObject = JSONObject.parseObject(result);
                log.debug(String.format("根据SQL查询: %s，查询结果: %s", sql, result));
            }

            return jsonObject;
        } else {
            String url = String.format(COMMON_QUERY_URI_V2, URLEncoder.encode(sql, CharEncoding.UTF_8));
            commonData.setCall_type(HTTP_TYPE_GET);
            commonData.setCallString(url);
            result = commonHttpClient.performRequest(commonData);

            return version == APIVersion.V2 ? parseV2ApiQueryResult(url, result, "result") :
                    parseV2ApiQueryResult(url, result, "data");
        }
    }

    /**
     * 查询对象，不分页，查询记录小于300条/v2小于100条
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param sql              SQL
     * @return JSON Object
     * @throws IOException IOException
     */
    public static JSONObject queryObjects(CommonHttpClient commonHttpClient,
                                          APIVersion version,
                                          String sql,
                                          String accessToken)
            throws IOException {

        JSONObject jsonObject = null;
        String result;

        CommonData commonData = new CommonData();
        commonData.putHeader("Authorization", accessToken);
        if (version == APIVersion.V1) {
            commonData.setCall_type(HTTP_TYPE_POST);
            commonData.setCallString(COMMON_QUERY_URI);
            commonData.putFormData("q", sql);

            result = commonHttpClient.performRequest(commonData);

            if (result.contains(ERROR_CODE)) {
                String msg = String.format("在类%s方法%s发生错误: %s，查询SQL: %s", APIUtil.class.getName(), "queryObjects", result, sql);
                log.error(msg);
            } else {
                jsonObject = JSONObject.parseObject(result);
                log.debug(String.format("根据SQL查询: %s，查询结果: %s", sql, result));
            }

            return jsonObject;
        } else {
            String url = String.format(COMMON_QUERY_URI_V2, URLEncoder.encode(sql, CharEncoding.UTF_8));
            commonData.setCall_type(HTTP_TYPE_GET);
            commonData.setCallString(url);
            result = commonHttpClient.performRequest(commonData);

            return version == APIVersion.V2 ? parseV2ApiQueryResult(url, result, "result") :
                    parseV2ApiQueryResult(url, result, "data");
        }
    }

    /**
     * 根据id查询数据
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param queryObject      Query Object
     * @param id               id
     * @return Query Object
     * @throws IOException IOException
     */
    public static JSONObject queryObjectsById(CommonHttpClient commonHttpClient,
                                              APIVersion version,
                                              String queryObject,
                                              long id)
            throws IOException {

        CommonData commonData = new CommonData();
        String accessToken = constructorImpl(commonHttpClient);
        commonData.putHeader("Authorization", accessToken);
        String result;

        if (version == APIVersion.V1) {
            String url = String.format(COMMON_GET_INFO_BY_ID_URI, queryObject, id);

            JSONObject jsonObject = null;
            commonData.setCall_type(HTTP_TYPE_GET);
            commonData.setCallString(url);

            result = commonHttpClient.performRequest(commonData);

            if (result.contains(ERROR_CODE)) {
                String msg = String.format("在类%s方法%s发生错误: %s，查询url: %s", APIUtil.class.getName(), "queryObjects", result, url);
                log.error(msg);
            } else {
                jsonObject = JSONObject.parseObject(result);
                log.debug(String.format("根据id查询: %s，查询结果: %s", url, result));
            }

            return jsonObject;
        } else {
            String url = (version == APIVersion.V2) ?
                    String.format(COMMON_GET_INFO_BY_ID_URI_V2, queryObject, id) :
                    String.format(COMMON_GET_INFO_BY_ID_URI_DEV, queryObject, id);

            commonData.setCall_type(HTTP_TYPE_GET);
            commonData.setCallString(url);

            result = commonHttpClient.performRequest(commonData);

            return version == APIVersion.V2 ?
                    parseV2ApiQueryResult(url, result, "result") :
                    parseV2ApiQueryResult(url, result, "data");
        }
    }

    /**
     * 根据id查询数据
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param queryObject      Query Object
     * @param id               id
     * @return Query Object
     * @throws IOException IOException
     */
    public static JSONObject queryObjectsById(CommonHttpClient commonHttpClient,
                                              APIVersion version,
                                              String queryObject,
                                              long id,
                                              String accessToken)
            throws IOException {

        CommonData commonData = new CommonData();
        commonData.putHeader("Authorization", accessToken);
        String result;

        if (version == APIVersion.V1) {
            String url = String.format(COMMON_GET_INFO_BY_ID_URI, queryObject, id);

            JSONObject jsonObject = null;
            commonData.setCall_type(HTTP_TYPE_GET);
            commonData.setCallString(url);

            result = commonHttpClient.performRequest(commonData);

            if (result.contains(ERROR_CODE)) {
                String msg = String.format("在类%s方法%s发生错误: %s，查询url: %s", APIUtil.class.getName(), "queryObjects", result, url);
                log.error(msg);
            } else {
                jsonObject = JSONObject.parseObject(result);
                log.debug(String.format("根据id查询: %s，查询结果: %s", url, result));
            }

            return jsonObject;
        } else {
            String url = (version == APIVersion.V2) ?
                    String.format(COMMON_GET_INFO_BY_ID_URI_V2, queryObject, id) :
                    String.format(COMMON_GET_INFO_BY_ID_URI_DEV, queryObject, id);

            commonData.setCall_type(HTTP_TYPE_GET);
            commonData.setCallString(url);

            result = commonHttpClient.performRequest(commonData);

            return version == APIVersion.V2 ?
                    parseV2ApiQueryResult(url, result, "result") :
                    parseV2ApiQueryResult(url, result, "data");
        }
    }

    /**
     * 查询单条记录
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param sql              SQL
     * @return records里面的单条记录
     * @throws IOException IOException
     */
    public static JSONObject querySingleObject(CommonHttpClient commonHttpClient,
                                               APIVersion version,
                                               String sql)
            throws IOException {

        JSONObject queryObject = (version == APIVersion.V1) ? APIUtil.queryObjects(commonHttpClient, APIVersion.V1, sql) :
                APIUtil.queryObjects(commonHttpClient, APIVersion.V2, sql);

        JSONObject returnObject = null;

        if (queryObject != null) {
            JSONArray jsonArray = queryObject.getJSONArray("records");

            if (jsonArray != null && jsonArray.size() > 0) {
                returnObject = jsonArray.getJSONObject(0);
            }
        }

        if (returnObject != null) {
            log.debug(String.format("根据SQL: %s 查询单条记录结果: %s", sql, returnObject.toString()));
        } else {
            log.warn(String.format("根据SQL: %s 查询单条记录结果: %s", sql, "查找不到结果"));
        }

        return returnObject;
    }

    /**
     * 查询单个对象
     *
     * @param commonHttpClient HTTP Client
     * @param queryObject      查询的对象名称
     * @param path             对象路径
     * @param params           参数
     * @return 查询对象
     * @throws IOException IOException
     */
    @Deprecated
    public static JSONObject querySingleObject(CommonHttpClient commonHttpClient,
                                               String queryObject,
                                               String path,
                                               Map<String, Object> params)
            throws IOException {
        String uri = String.format(COMMON_QUERY_OBJECT_URI, queryObject, path);

        JSONObject jsonObject = null;

        CommonData commonData = new CommonData();
        String accessToken = constructorImpl(commonHttpClient);
        commonData.putHeader("Authorization", accessToken);
        commonData.setCall_type(HTTP_TYPE_POST);
        commonData.setCallString(uri);

        for (String key : params.keySet()) {
            commonData.putFormData(key, params.get(key));
        }


        String result = commonHttpClient.performRequest(commonData);

        if (result.contains(ERROR_CODE)) {
            String msg = String.format("在类%s方法%s发生错误: %s，查询url: %s", APIUtil.class.getName(), "querySingleObject", result, uri);
            log.error(msg);
        } else {
            jsonObject = JSONObject.parseObject(result);
            log.debug(String.format("查询单个对象查询: %s，查询结果: %s", uri, result));
        }

        return jsonObject;
    }

    /**
     * 查询单个对象
     *
     * @param commonHttpClient HTTP Client
     * @param queryObject      查询的对象名称
     * @param path             对象路径
     * @param params           参数
     * @return 查询对象
     * @throws IOException IOException
     */
    @Deprecated
    public static JSONObject querySingleObject(CommonHttpClient commonHttpClient,
                                               String queryObject,
                                               String path,
                                               Map<String, Object> params,
                                               String accessToken)
            throws IOException {
        String uri = String.format(COMMON_QUERY_OBJECT_URI, queryObject, path);

        JSONObject jsonObject = null;

        CommonData commonData = new CommonData();
        commonData.putHeader("Authorization", accessToken);
        commonData.setCall_type(HTTP_TYPE_POST);
        commonData.setCallString(uri);

        for (String key : params.keySet()) {
            commonData.putFormData(key, params.get(key));
        }


        String result = commonHttpClient.performRequest(commonData);

        if (result.contains(ERROR_CODE)) {
            String msg = String.format("在类%s方法%s发生错误: %s，查询url: %s", APIUtil.class.getName(), "querySingleObject", result, uri);
            log.error(msg);
        } else {
            jsonObject = JSONObject.parseObject(result);
            log.debug(String.format("查询单个对象查询: %s，查询结果: %s", uri, result));
        }

        return jsonObject;
    }

    /**
     * 分页查询记录
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param sql              SQL
     * @return 查询对象数组
     * @throws IOException IOException
     */
    public static JSONArray queryList(CommonHttpClient commonHttpClient,
                                      APIVersion version,
                                      String sql)
            throws IOException {

        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();

        int start = 0, totalSize;

        final int PAGE_SIZE = version == APIVersion.V1 ? 300 : 100; //每页300行记录

        CommonData commonData = new CommonData();
        String accessToken = constructorImpl(commonHttpClient);
        commonData.putHeader("Authorization", accessToken);
        if (version == APIVersion.V1) {

            commonData.setCall_type(HTTP_TYPE_POST);
            commonData.setCallString(COMMON_QUERY_URI);

            //先查询总数量
            String soql = String.format("%s limit %s, %s", sql, start, PAGE_SIZE);

            log.debug(String.format("分页查询记录，查询SQL: %s", soql));

            commonData.putFormData("q", soql);
            String result = commonHttpClient.performRequest(commonData);
            jsonObject = JSONObject.parseObject(result);

            if (jsonObject.containsKey("totalSize") && jsonObject.containsKey("count")
                    && jsonObject.containsKey("records")) {

                totalSize = jsonObject.getInteger("totalSize");

                //分页查询，每页300条
                for (int i = 0; i < Math.ceil((double) totalSize / PAGE_SIZE); i++) {
                    soql = String.format("%s limit %s, %s", sql, i * PAGE_SIZE,
                            PAGE_SIZE);

                    JSONObject obj = APIUtil.queryObjects(commonHttpClient, APIVersion.V1, soql);
                    JSONArray arr = obj.getJSONArray("records");
                    jsonArray.addAll(arr);
                }

                return jsonArray;
            } else {
                log.warn(String.format("分页查询查找不到数据:%s", soql));
                return null;
            }
        } else {
            commonData.setCall_type(HTTP_TYPE_GET);

            //先查询总数量
            String urlParam = String.format("%s limit %s, %s", sql, start, PAGE_SIZE);
            String url = String.format(COMMON_QUERY_URI_V2, URLEncoder.encode(urlParam, CharEncoding.UTF_8));

            log.debug(String.format("分页查询记录，查询路径: %s", URLDecoder.decode(url, CharEncoding.UTF_8)));

            commonData.setCallString(url);
            String result = commonHttpClient.performRequest(commonData);
            JSONObject returnObject = JSONObject.parseObject(result);

            if (returnObject.containsKey("code") && returnObject.containsKey("result")) {

                JSONObject recordObject = returnObject.getJSONObject("result");

                totalSize = APIUtil.getInt(APIUtil.getObjectAttribute(recordObject, "totalSize"));

                //分页查询，每页100条
                for (int i = 0; i < Math.ceil((double) totalSize / PAGE_SIZE); i++) {
                    urlParam = String.format("%s limit %s, %s", sql, i * PAGE_SIZE,
                            PAGE_SIZE);

                    JSONObject obj = APIUtil.queryObjects(commonHttpClient, APIVersion.V2, urlParam);
                    JSONArray arr = obj.getJSONArray("records");
                    jsonArray.addAll(arr);
                }

                return jsonArray;
            } else {
                log.warn(String.format("分页查询查找不到数据:%s，", url));
                return null;
            }
        }


    }

    /**
     * 分页查询记录
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param sql              SQL
     * @return 查询对象数组
     * @throws IOException IOException
     */
    public static JSONArray queryList(CommonHttpClient commonHttpClient,
                                      APIVersion version,
                                      String sql,
                                      String accessToken)
            throws IOException {

        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();

        int start = 0, totalSize;

        final int PAGE_SIZE = version == APIVersion.V1 ? 300 : 100; //每页300行记录

        CommonData commonData = new CommonData();
        commonData.putHeader("Authorization", accessToken);
        if (version == APIVersion.V1) {

            commonData.setCall_type(HTTP_TYPE_POST);
            commonData.setCallString(COMMON_QUERY_URI);

            //先查询总数量
            String soql = String.format("%s limit %s, %s", sql, start, PAGE_SIZE);

            log.debug(String.format("分页查询记录，查询SQL: %s", soql));

            commonData.putFormData("q", soql);
            String result = commonHttpClient.performRequest(commonData);
            jsonObject = JSONObject.parseObject(result);

            if (jsonObject.containsKey("totalSize") && jsonObject.containsKey("count")
                    && jsonObject.containsKey("records")) {

                totalSize = jsonObject.getInteger("totalSize");

                //分页查询，每页300条
                for (int i = 0; i < Math.ceil((double) totalSize / PAGE_SIZE); i++) {
                    soql = String.format("%s limit %s, %s", sql, i * PAGE_SIZE,
                            PAGE_SIZE);

                    JSONObject obj = APIUtil.queryObjects(commonHttpClient, APIVersion.V1, soql);
                    JSONArray arr = obj.getJSONArray("records");
                    jsonArray.addAll(arr);
                }

                return jsonArray;
            } else {
                log.warn(String.format("分页查询查找不到数据:%s", soql));
                return null;
            }
        } else {
            commonData.setCall_type(HTTP_TYPE_GET);

            //先查询总数量
            String urlParam = String.format("%s limit %s, %s", sql, start, PAGE_SIZE);
            String url = String.format(COMMON_QUERY_URI_V2, URLEncoder.encode(urlParam, CharEncoding.UTF_8));

            log.debug(String.format("分页查询记录，查询路径: %s", URLDecoder.decode(url, CharEncoding.UTF_8)));

            commonData.setCallString(url);
            String result = commonHttpClient.performRequest(commonData);
            JSONObject returnObject = JSONObject.parseObject(result);

            if (returnObject.containsKey("code") && returnObject.containsKey("result")) {

                JSONObject recordObject = returnObject.getJSONObject("result");

                totalSize = APIUtil.getInt(APIUtil.getObjectAttribute(recordObject, "totalSize"));

                //分页查询，每页100条
                for (int i = 0; i < Math.ceil((double) totalSize / PAGE_SIZE); i++) {
                    urlParam = String.format("%s limit %s, %s", sql, i * PAGE_SIZE,
                            PAGE_SIZE);

                    JSONObject obj = APIUtil.queryObjects(commonHttpClient, APIVersion.V2, urlParam);
                    JSONArray arr = obj.getJSONArray("records");
                    jsonArray.addAll(arr);
                }

                return jsonArray;
            } else {
                log.warn(String.format("分页查询查找不到数据:%s，", url));
                return null;
            }
        }
    }

    /**
     * 更新自定义对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param jsonString       JSON字符串
     * @param objName          对象名称，仅对V2版本有效，V1版本传入空值
     * @param id               对象id，仅对V2版本有效，V1版本传入0L
     * @return 返回消息
     * @throws IOException IOException
     */
    public static String updateCustomizeObject(CommonHttpClient commonHttpClient,
                                               APIVersion version,
                                               String jsonString,
                                               String objName,
                                               long id)
            throws IOException {

        return (version == APIVersion.V1) ?
                updateObjectV1(commonHttpClient, ObjectType.CUSTOMIZE, "", jsonString) :
                updateObjectV2(commonHttpClient, version, objName, id, jsonString);

    }

    /**
     * 更新自定义对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param jsonString       JSON字符串
     * @param objName          对象名称，仅对V2版本有效，V1版本传入空值
     * @param id               对象id，仅对V2版本有效，V1版本传入0L
     * @return 返回消息
     * @throws IOException IOException
     */
    public static String updateCustomizeObject(CommonHttpClient commonHttpClient,
                                               APIVersion version,
                                               String jsonString,
                                               String objName,
                                               long id,
                                               String accessToken)
            throws IOException {

        return (version == APIVersion.V1) ?
                updateObjectV1(commonHttpClient, ObjectType.CUSTOMIZE, "", jsonString, accessToken) :
                updateObjectV2(commonHttpClient, version, objName, id, jsonString, accessToken);

    }

    /**
     * V1 API 更新对象
     *
     * @param commonHttpClient HTTP Client
     * @param objectType       对象类型
     * @param objName          对象名称
     * @param jsonString       json字符串
     * @return 更新结果
     * @throws IOException IOException
     */
    private static String updateObjectV1(CommonHttpClient commonHttpClient,
                                         ObjectType objectType,
                                         String objName,
                                         String jsonString
    ) throws IOException {

        CommonData commonData = new CommonData();
        commonData.setCall_type(HTTP_TYPE_POST);
        String accessToken = constructorImpl(commonHttpClient);
        commonData.putHeader("Authorization", accessToken);
        String url = (objectType == ObjectType.CUSTOMIZE) ?
                String.format(COMMON_OBJECT_UPDATE_URI, "customize") :
                String.format(COMMON_OBJECT_UPDATE_URI, objName);

        commonData.setCallString(url);

        log.debug(String.format("更新对象url: %s", url));

        commonData.setBody(jsonString);

        String msg = commonHttpClient.performRequest(commonData);

        if (msg.contains(ERROR_CODE)) {
            log.error(String.format("更新对象错误: %s，更新内容: %s", msg, jsonString));
        } else {
            log.debug(String.format("更新对象结果: %s，更新内容: %s", msg, jsonString));
        }

        return msg;
    }

    /**
     * V1 API 更新对象
     *
     * @param commonHttpClient HTTP Client
     * @param objectType       对象类型
     * @param objName          对象名称
     * @param jsonString       json字符串
     * @return 更新结果
     * @throws IOException IOException
     */
    private static String updateObjectV1(CommonHttpClient commonHttpClient,
                                         ObjectType objectType,
                                         String objName,
                                         String jsonString,
                                         String accessToken
    ) throws IOException {

        CommonData commonData = new CommonData();
        commonData.setCall_type(HTTP_TYPE_POST);
        commonData.putHeader("Authorization", accessToken);
        String url = (objectType == ObjectType.CUSTOMIZE) ?
                String.format(COMMON_OBJECT_UPDATE_URI, "customize") :
                String.format(COMMON_OBJECT_UPDATE_URI, objName);

        commonData.setCallString(url);

        log.debug(String.format("更新对象url: %s", url));

        commonData.setBody(jsonString);

        String msg = commonHttpClient.performRequest(commonData);

        if (msg.contains(ERROR_CODE)) {
            log.error(String.format("更新对象错误: %s，更新内容: %s", msg, jsonString));
        } else {
            log.debug(String.format("更新对象结果: %s，更新内容: %s", msg, jsonString));
        }

        return msg;
    }

    /**
     * V2 API更新对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objName          对象名称
     * @param id               对象id
     * @param jsonString       更新的JSON字符串
     * @return 更新结果
     * @throws IOException IOException
     */
    private static String updateObjectV2(CommonHttpClient commonHttpClient,
                                         APIVersion version,
                                         String objName,
                                         long id,
                                         String jsonString)
            throws IOException {

        CommonData commonData = new CommonData();
        String accessToken = constructorImpl(commonHttpClient);
        commonData.putHeader("Authorization", accessToken);
        String url = (version == APIVersion.V2) ? String.format(COMMON_OBJECT_UPDATE_URI_V2, objName, id) :
                String.format(COMMON_OBJECT_UPDATE_URI_DEV, objName, id);

        commonData.setCall_type(HTTP_TYPE_PATCH);
        commonData.setCallString(url);
        JSONObject dataObject = new JSONObject();
        dataObject.put("data", JSONObject.parseObject(jsonString));
        commonData.setBody(dataObject.toString());

        String msg = commonHttpClient.performRequest(commonData);
        JSONObject msgObject = JSONObject.parseObject(msg);

        if (msgObject.containsKey("code") && msgObject.containsKey("msg")) {
            if (APIUtil.getInt(APIUtil.getObjectAttribute(msgObject, "code")) == 200) {
                log.debug(String.format("更新对象结果: %s，更新url: %s, 更新内容: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url, dataObject.toString()));
            } else {
                log.error(String.format("更新对象错误: %s，更新url: %s, 更新内容: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url, dataObject.toString()));
            }

        } else {
            log.error(String.format("更新对象结果错误，更新url: %s, 更新内容: %s", url, dataObject.toString()));
        }

        return msg;
    }

    /**
     * V2 API更新对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objName          对象名称
     * @param id               对象id
     * @param jsonString       更新的JSON字符串
     * @return 更新结果
     * @throws IOException IOException
     */
    private static String updateObjectV2(CommonHttpClient commonHttpClient,
                                         APIVersion version,
                                         String objName,
                                         long id,
                                         String jsonString,
                                         String accessToken)
            throws IOException {

        CommonData commonData = new CommonData();
        commonData.putHeader("Authorization", accessToken);
        String url = (version == APIVersion.V2) ? String.format(COMMON_OBJECT_UPDATE_URI_V2, objName, id) :
                String.format(COMMON_OBJECT_UPDATE_URI_DEV, objName, id);

        commonData.setCall_type(HTTP_TYPE_PATCH);
        commonData.setCallString(url);
        JSONObject dataObject = new JSONObject();
        dataObject.put("data", JSONObject.parseObject(jsonString));
        commonData.setBody(dataObject.toString());

        String msg = commonHttpClient.performRequest(commonData);
        JSONObject msgObject = JSONObject.parseObject(msg);

        if (msgObject.containsKey("code") && msgObject.containsKey("msg")) {
            if (APIUtil.getInt(APIUtil.getObjectAttribute(msgObject, "code")) == 200) {
                log.debug(String.format("更新对象结果: %s，更新url: %s, 更新内容: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url, dataObject.toString()));
            } else {
                log.error(String.format("更新对象错误: %s，更新url: %s, 更新内容: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url, dataObject.toString()));
            }

        } else {
            log.error(String.format("更新对象结果错误，更新url: %s, 更新内容: %s", url, dataObject.toString()));
        }

        return msg;
    }

    /**
     * V1 API创建对象
     *
     * @param commonHttpClient HTTP Client
     * @param objectType       对象类型
     * @param objName          对象名称
     * @param jsonString       创建对象json字符串
     * @return 创建信息
     * @throws IOException IOException
     */
    private static String createObjectV1(CommonHttpClient commonHttpClient,
                                         ObjectType objectType,
                                         String objName,
                                         String jsonString)
            throws IOException {
        CommonData commonData = new CommonData();
        commonData.setCall_type(HTTP_TYPE_POST);
        String accessToken = constructorImpl(commonHttpClient);
        commonData.putHeader("Authorization", accessToken);
        String url = (objectType == ObjectType.CUSTOMIZE) ?
                String.format(COMMON_OBJECT_CREATE_URI, "customize") :
                String.format(COMMON_OBJECT_CREATE_URI, objName);

        log.debug(String.format("新建对象url: %s", url));
        commonData.setBody(jsonString);
        commonData.setCallString(url);
        String msg = commonHttpClient.performRequest(commonData);

        if (msg.contains(ERROR_CODE)) {
            log.error(String.format("新建自定义对象错误: %s，新建参数: %s", msg, jsonString));
        } else {
            log.debug(String.format("新建自定义对象结果: %s，新建参数: %s", msg, jsonString));
        }

        return msg;
    }

    /**
     * V1 API创建对象
     *
     * @param commonHttpClient HTTP Client
     * @param objectType       对象类型
     * @param objName          对象名称
     * @param jsonString       创建对象json字符串
     * @return 创建信息
     * @throws IOException IOException
     */
    private static String createObjectV1(CommonHttpClient commonHttpClient,
                                         ObjectType objectType,
                                         String objName,
                                         String jsonString,
                                         String accessToken)
            throws IOException {
        CommonData commonData = new CommonData();
        commonData.setCall_type(HTTP_TYPE_POST);
        commonData.putHeader("Authorization", accessToken);
        String url = (objectType == ObjectType.CUSTOMIZE) ?
                String.format(COMMON_OBJECT_CREATE_URI, "customize") :
                String.format(COMMON_OBJECT_CREATE_URI, objName);

        log.debug(String.format("新建对象url: %s", url));
        commonData.setBody(jsonString);
        commonData.setCallString(url);
        String msg = commonHttpClient.performRequest(commonData);

        if (msg.contains(ERROR_CODE)) {
            log.error(String.format("新建自定义对象错误: %s，新建参数: %s", msg, jsonString));
        } else {
            log.debug(String.format("新建自定义对象结果: %s，新建参数: %s", msg, jsonString));
        }

        return msg;
    }

    /**
     * 新建标准对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param jsonString       JSON字符串
     * @return 返回消息
     * @throws IOException IOException
     */
    public static String createStandardObject(CommonHttpClient commonHttpClient,
                                              APIVersion version,
                                              String objName,
                                              String jsonString)
            throws IOException {
        return (version == APIVersion.V1) ?
                createObjectV1(commonHttpClient, ObjectType.STANDARD, objName, jsonString) :
                createObjectV2(commonHttpClient, version, objName, jsonString);
    }

    /**
     * 新建标准对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param jsonString       JSON字符串
     * @return 返回消息
     * @throws IOException IOException
     */
    public static String createStandardObject(CommonHttpClient commonHttpClient,
                                              APIVersion version,
                                              String objName,
                                              String jsonString,
                                              String accessToken)
            throws IOException {
        return (version == APIVersion.V1) ?
                createObjectV1(commonHttpClient, ObjectType.STANDARD, objName, jsonString, accessToken) :
                createObjectV2(commonHttpClient, version, objName, jsonString, accessToken);
    }

    /**
     * 创建自定义对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objName          对象名称
     * @param jsonString       新建的json字符串
     * @return 创建信息
     * @throws IOException IOException
     */
    public static String createCustomizeObject(CommonHttpClient commonHttpClient,
                                               APIVersion version,
                                               String objName,
                                               String jsonString)
            throws IOException {
        return (version == APIVersion.V1) ?
                createObjectV1(commonHttpClient, ObjectType.CUSTOMIZE, objName, jsonString) :
                createObjectV2(commonHttpClient, version, objName, jsonString);
    }

    /**
     * 创建自定义对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objName          对象名称
     * @param jsonString       新建的json字符串
     * @return 创建信息
     * @throws IOException IOException
     */
    public static String createCustomizeObject(CommonHttpClient commonHttpClient,
                                               APIVersion version,
                                               String objName,
                                               String jsonString,
                                               String accessToken)
            throws IOException {
        return (version == APIVersion.V1) ?
                createObjectV1(commonHttpClient, ObjectType.CUSTOMIZE, objName, jsonString, accessToken) :
                createObjectV2(commonHttpClient, version, objName, jsonString, accessToken);
    }

    /**
     * V2 API创建对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objName          对象名称
     * @param jsonString       JSON字符串
     * @return 创建结果
     * @throws IOException IOException
     */
    private static String createObjectV2(CommonHttpClient commonHttpClient,
                                         APIVersion version,
                                         String objName,
                                         String jsonString)
            throws IOException {

        CommonData commonData = new CommonData();
        String accessToken = constructorImpl(commonHttpClient);
        commonData.putHeader("Authorization", accessToken);
        JSONObject dataObject = new JSONObject();
        dataObject.put("data", JSONObject.parseObject(jsonString));

        String url = (version == APIVersion.V2) ? String.format(COMMON_OBJECT_CREATE_URI_V2, objName) :
                String.format(COMMON_OBJECT_CREATE_URI_DEV, objName);

        commonData.setCall_type(HTTP_TYPE_POST);
        commonData.setCallString(url);
        commonData.setBody(dataObject.toString());
        String msg = commonHttpClient.performRequest(commonData);
        JSONObject msgObject = JSONObject.parseObject(msg);

        if (msgObject.containsKey("code") && msgObject.containsKey("msg")) {
            if (APIUtil.getInt(APIUtil.getObjectAttribute(msgObject, "code")) == 200) {
                log.debug(String.format("创建对象结果: %s，创建url: %s, 创建内容: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url, dataObject.toString()));
            } else {
                log.error(String.format("创建对象错误: %s，创建url: %s, 创建内容: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url, dataObject.toString()));
            }

        } else {
            log.error(String.format("创建对象结果错误，创建url: %s, 创建内容: %s", url, dataObject.toString()));
        }

        return msg;
    }

    /**
     * V2 API创建对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objName          对象名称
     * @param jsonString       JSON字符串
     * @return 创建结果
     * @throws IOException IOException
     */
    private static String createObjectV2(CommonHttpClient commonHttpClient,
                                         APIVersion version,
                                         String objName,
                                         String jsonString,
                                         String accessToken)
            throws IOException {

        CommonData commonData = new CommonData();
        commonData.putHeader("Authorization", accessToken);
        JSONObject dataObject = new JSONObject();
        dataObject.put("data", JSONObject.parseObject(jsonString));

        String url = (version == APIVersion.V2) ? String.format(COMMON_OBJECT_CREATE_URI_V2, objName) :
                String.format(COMMON_OBJECT_CREATE_URI_DEV, objName);

        commonData.setCall_type(HTTP_TYPE_POST);
        commonData.setCallString(url);
        commonData.setBody(dataObject.toString());
        String msg = commonHttpClient.performRequest(commonData);
        JSONObject msgObject = JSONObject.parseObject(msg);

        if (msgObject.containsKey("code") && msgObject.containsKey("msg")) {
            if (APIUtil.getInt(APIUtil.getObjectAttribute(msgObject, "code")) == 200) {
                log.debug(String.format("创建对象结果: %s，创建url: %s, 创建内容: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url, dataObject.toString()));
            } else {
                log.error(String.format("创建对象错误: %s，创建url: %s, 创建内容: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url, dataObject.toString()));
            }

        } else {
            log.error(String.format("创建对象结果错误，创建url: %s, 创建内容: %s", url, dataObject.toString()));
        }

        return msg;
    }

    /**
     * 删除自定义对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objName          对象名称 仅对V2版本有效，V1版本传入空值
     * @param id               对象id 仅对V2版本有效，V1版本传入0
     * @return 返回消息
     * @throws IOException IOException
     */
    public static String deleteCustomizeObject(CommonHttpClient commonHttpClient,
                                               APIVersion version,
                                               String objName,
                                               long id)
            throws IOException {

        return (version == APIVersion.V1) ?
                deleteObjectV1(commonHttpClient, ObjectType.CUSTOMIZE, objName, id) :
                deleteObjectV2(commonHttpClient, version, objName, id);
    }

    /**
     * 删除自定义对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objName          对象名称 仅对V2版本有效，V1版本传入空值
     * @param id               对象id 仅对V2版本有效，V1版本传入0
     * @return 返回消息
     * @throws IOException IOException
     */
    public static String deleteCustomizeObject(CommonHttpClient commonHttpClient,
                                               APIVersion version,
                                               String objName,
                                               long id,
                                               String accessToken)
            throws IOException {

        return (version == APIVersion.V1) ?
                deleteObjectV1(commonHttpClient, ObjectType.CUSTOMIZE, objName, id, accessToken) :
                deleteObjectV2(commonHttpClient, version, objName, id, accessToken);
    }

    /**
     * V2 API删除对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objName          对象名称
     * @param id               对象id
     * @return 删除结果
     * @throws IOException IOException
     */
    private static String deleteObjectV2(CommonHttpClient commonHttpClient,
                                         APIVersion version,
                                         String objName,
                                         long id)
            throws IOException {
        CommonData commonData = new CommonData();
        String accessToken = constructorImpl(commonHttpClient);
        commonData.putHeader("Authorization", accessToken);
        String url = (version == APIVersion.V2) ? String.format(COMMON_OBJECT_UPDATE_URI_V2, objName, id) :
                String.format(COMMON_OBJECT_UPDATE_URI_DEV, objName, id);

        commonData.setCall_type(HTTP_TYPE_DELETE);
        commonData.setCallString(url);

        String msg = commonHttpClient.performRequest(commonData);
        JSONObject msgObject = JSONObject.parseObject(msg);

        if (msgObject.containsKey("code") && msgObject.containsKey("msg")) {
            if (APIUtil.getInt(APIUtil.getObjectAttribute(msgObject, "code")) == 200) {
                log.debug(String.format("删除对象结果: %s，删除url: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url));
            } else {
                log.error(String.format("删除对象错误: %s，删除url: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url));
            }

        } else {
            log.error(String.format("删除对象结果错误，删除url: %s", url));
        }

        return msg;
    }

    /**
     * V2 API删除对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objName          对象名称
     * @param id               对象id
     * @return 删除结果
     * @throws IOException IOException
     */
    private static String deleteObjectV2(CommonHttpClient commonHttpClient,
                                         APIVersion version,
                                         String objName,
                                         long id,
                                         String accessToken) throws IOException {
        CommonData commonData = new CommonData();
        commonData.putHeader("Authorization", accessToken);
        String url = (version == APIVersion.V2) ? String.format(COMMON_OBJECT_UPDATE_URI_V2, objName, id) :
                String.format(COMMON_OBJECT_UPDATE_URI_DEV, objName, id);

        commonData.setCall_type(HTTP_TYPE_DELETE);
        commonData.setCallString(url);

        String msg = commonHttpClient.performRequest(commonData);
        JSONObject msgObject = JSONObject.parseObject(msg);

        if (msgObject.containsKey("code") && msgObject.containsKey("msg")) {
            if (APIUtil.getInt(APIUtil.getObjectAttribute(msgObject, "code")) == 200) {
                log.debug(String.format("删除对象结果: %s，删除url: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url));
            } else {
                log.error(String.format("删除对象错误: %s，删除url: %s",
                        APIUtil.getObjectAttribute(msgObject, "msg"), url));
            }

        } else {
            log.error(String.format("删除对象结果错误，删除url: %s", url));
        }

        return msg;
    }

    /**
     * 删除标准对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param id               对象id
     * @return 返回消息
     * @throws IOException IOException
     */
    public static String deleteStandardObject(CommonHttpClient commonHttpClient,
                                              APIVersion version,
                                              String objName,
                                              long id)
            throws IOException {

        return (version == APIVersion.V1) ?
                deleteObjectV1(commonHttpClient, ObjectType.STANDARD, objName, id) :
                deleteObjectV2(commonHttpClient, version, objName, id);
    }

    /**
     * 删除标准对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param id               对象id
     * @return 返回消息
     * @throws IOException IOException
     */
    public static String deleteStandardObject(CommonHttpClient commonHttpClient,
                                              APIVersion version,
                                              String objName,
                                              long id,
                                              String accessToken)
            throws IOException {

        return (version == APIVersion.V1) ?
                deleteObjectV1(commonHttpClient, ObjectType.STANDARD, objName, id, accessToken) :
                deleteObjectV2(commonHttpClient, version, objName, id, accessToken);
    }

    /**
     * V1 API删除对象
     *
     * @param commonHttpClient HTTP Client
     * @param objectType       对象类型
     * @param objName          对象名称
     * @param id               对象id
     * @return 删除结果
     * @throws IOException IOException
     */
    private static String deleteObjectV1(CommonHttpClient commonHttpClient,
                                         ObjectType objectType,
                                         String objName,
                                         long id)
            throws IOException {
        CommonData commonData = new CommonData();
        String accessToken = constructorImpl(commonHttpClient);
        commonData.putHeader("Authorization", accessToken);
        String url = (objectType == ObjectType.STANDARD) ?
                String.format(COMMON_OBJECT_DELETE_URI, objName) :
                String.format(COMMON_OBJECT_DELETE_URI, "customize");

        log.debug(String.format("删除对象url：%s", url));

        commonData.setCall_type(HTTP_TYPE_POST);
        commonData.setCallString(url);
        commonData.putFormData("id", id);

        String msg = commonHttpClient.performRequest(commonData);

        if (msg.contains(ERROR_CODE)) {
            log.error(String.format("删除对象错误: %s，对象名称: %s，对象id: %s", msg, objName, id));
        } else {
            log.debug(String.format("删除对象结果: %s，对象名称: %s，对象id: %s", msg, objName, id));
        }

        return msg;
    }

    /**
     * V1 API删除对象
     *
     * @param commonHttpClient HTTP Client
     * @param objectType       对象类型
     * @param objName          对象名称
     * @param id               对象id
     * @return 删除结果
     * @throws IOException IOException
     */
    private static String deleteObjectV1(CommonHttpClient commonHttpClient,
                                         ObjectType objectType,
                                         String objName,
                                         long id,
                                         String accessToken)
            throws IOException {
        CommonData commonData = new CommonData();
        commonData.putHeader("Authorization", accessToken);
        String url = (objectType == ObjectType.STANDARD) ?
                String.format(COMMON_OBJECT_DELETE_URI, objName) :
                String.format(COMMON_OBJECT_DELETE_URI, "customize");

        log.debug(String.format("删除对象url：%s", url));

        commonData.setCall_type(HTTP_TYPE_POST);
        commonData.setCallString(url);
        commonData.putFormData("id", id);

        String msg = commonHttpClient.performRequest(commonData);

        if (msg.contains(ERROR_CODE)) {
            log.error(String.format("删除对象错误: %s，对象名称: %s，对象id: %s", msg, objName, id));
        } else {
            log.debug(String.format("删除对象结果: %s，对象名称: %s，对象id: %s", msg, objName, id));
        }

        return msg;
    }

    /**
     * 更新标准对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objName          对象名称
     * @param id               对象id，仅适用V2 API, V1传入0L
     * @param jsonString       更新传入的JSON字符串
     * @return 更新结果
     * @throws IOException IOException
     */
    public static String updateStandardObject(CommonHttpClient commonHttpClient,
                                              APIVersion version,
                                              String objName,
                                              long id,
                                              String jsonString)
            throws IOException {

        return (version == APIVersion.V1) ?
                updateObjectV1(commonHttpClient, ObjectType.STANDARD, objName, jsonString) :
                updateObjectV2(commonHttpClient, version, objName, id, jsonString);

    }

    /**
     * 更新标准对象
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objName          对象名称
     * @param id               对象id，仅适用V2 API, V1传入0L
     * @param jsonString       更新传入的JSON字符串
     * @return 更新结果
     * @throws IOException IOException
     */
    public static String updateStandardObject(CommonHttpClient commonHttpClient,
                                              APIVersion version,
                                              String objName,
                                              long id,
                                              String jsonString,
                                              String accessToken)
            throws IOException {

        return (version == APIVersion.V1) ?
                updateObjectV1(commonHttpClient, ObjectType.STANDARD, objName, jsonString, accessToken) :
                updateObjectV2(commonHttpClient, version, objName, id, jsonString, accessToken);

    }


    /**
     * 获取选择型字段label描述
     *
     * @param commonHttpClient HTTP Client
     * @param objectName       对象名称
     * @param propertyName     属性名称
     * @param itemType         类型：selectitem，checkitem
     * @param value            字段对应值
     * @return 字段对应描述
     * @throws IOException IOException
     */
    public static String getSingleSelectItemLabel(CommonHttpClient commonHttpClient,
                                                  String objectName,
                                                  String propertyName,
                                                  APIVersion version,
                                                  ItemType itemType,
                                                  int value)
            throws IOException {

        Map<String, Map<Integer, String>> itemsLabelMap = getSelectItemLabels(commonHttpClient,
                version, objectName, itemType);

        if (itemsLabelMap.containsKey(propertyName)) {
            Map<Integer, String> labelMap = itemsLabelMap.get(propertyName);

            if (labelMap.containsKey(value)) {
                String desc = labelMap.get(value);
                log.debug(String.format("查询选择型字段描述，查询对象名: %s, 查询字段: %s, 查询值: %s, 字段描述值: %s",
                        objectName, propertyName, value, desc));
                return desc;
            }
        }

        log.error(String.format("查询选择型字段描述，查询对象名: %s, 查询字段: %s, 查询值: %s, 查找不到描述值。",
                objectName, propertyName, value));

        return "";

    }

    /**
     * 根据对象名、选项类型获取所有选项的label描述值
     *
     * @param commonHttpClient HTTP Client
     * @param version          API版本
     * @param objectName       标准对象名称
     * @param itemType         类型：selectitem，checkitem
     * @return 所有选择型字段对应的描述
     * @throws IOException IOException
     */
    public static Map<String, Map<Integer, String>> getSelectItemLabels(CommonHttpClient commonHttpClient,
                                                                        APIVersion version,
                                                                        String objectName,
                                                                        ItemType itemType)
            throws IOException {

        String url = "";

        switch (version) {
            case V1:
                url = String.format(COMMON_STANDARD_OBJECT_DESCRIBE_URI, objectName);
                break;
            case V2:
                url = String.format(COMMON_OBJECT_DESCRIBE_URI_V2, objectName);
                break;
            case DEV:
                url = String.format(COMMON_OBJECT_DESCRIBE_URI_DEV, objectName);
                break;
            default:
                break;
        }

        log.debug(String.format("查找标准对象选择型字段描述值，查找url: %s", url));

        return separateItemsLabelsFromObjectDescription(commonHttpClient, version, url, itemType);

    }

    /**
     * 根据对象所属id、选项类型获取所有选项的label描述值，仅适用V1版本API
     *
     * @param commonHttpClient HTTP Client
     * @param belongId         自定义对象id
     * @param itemType         类型：selectitem，checkitem
     * @return 所有选择型字段对应的描述
     * @throws IOException IOException
     */
    public static Map<String, Map<Integer, String>> getSelectItemLabels(CommonHttpClient commonHttpClient,
                                                                        long belongId,
                                                                        ItemType itemType)
            throws IOException {

        String url = String.format(COMMON_CUSTOMIZE_OBJECT_DESCRIBE_URI, belongId);
        log.debug(String.format("查找自定义对象选择型字段描述值，查找url: %s", url));

        return separateItemsLabelsFromObjectDescription(commonHttpClient, APIVersion.V1, url, itemType);

    }

    /**
     * 从对象描述中分离出选择型字段的描述
     *
     * @param commonHttpClient HTTP Client
     * @param url              对象描述url
     * @param itemType         类型：selectitem，checkitem
     * @return 所有选择型字段对应的描述
     * @throws IOException IOException
     */
    private static Map<String, Map<Integer, String>> separateItemsLabelsFromObjectDescription(CommonHttpClient commonHttpClient,
                                                                                              APIVersion version,
                                                                                              String url,
                                                                                              ItemType itemType)
            throws IOException {

        CommonData commonData = new CommonData();
        String accessToken = constructorImpl(commonHttpClient);
        commonData.putHeader("Authorization", accessToken);
        commonData.setCall_type(HTTP_TYPE_GET);
        commonData.setCallString(url);

        String msg = commonHttpClient.performRequest(commonData);

        String rootNodeField; //api根节点
        String fieldNameKey; //属性字段名
        switch (version) {
            case V1:
                rootNodeField = "fields";
                fieldNameKey = "propertyname";
                break;
            case V2:
                rootNodeField = "data";
                fieldNameKey = "apikey";
                break;
            case DEV:
                rootNodeField = "data";
                fieldNameKey = "apiKey";
                break;
            default:
                rootNodeField = "";
                fieldNameKey = "";
                break;
        }

        //描述Map，key:字段名，value: item的值-item项标签
        Map<String, Map<Integer, String>> itemsLabelMap = new HashMap<>();


        JSONObject describeObject;
        if (msg != null && !msg.isEmpty() && msg.contains(rootNodeField)) {

            if (version == APIVersion.V1) {
                describeObject = JSONObject.parseObject(msg);
            } else {
                JSONObject rootObject = JSONObject.parseObject(msg);
                describeObject = rootObject.getJSONObject("data");
            }

            if (describeObject != null) {
                JSONArray fieldsArray = describeObject.getJSONArray("fields");
                JSONObject fieldObject;
                JSONObject itemObject;
                JSONArray itemArray;
                Map<Integer, String> labelMap;
                for (int i = 0; i < fieldsArray.size(); i++) {
                    fieldObject = fieldsArray.getJSONObject(i);
                    if (fieldObject == null) {
                        continue;
                    }
                    if (!fieldObject.containsKey(fieldNameKey)) {
                        continue;
                    }
                    if (!fieldObject.containsKey(itemType.getDescription())) {
                        continue;
                    }
                    itemArray = fieldObject.getJSONArray(itemType.getDescription());
                    labelMap = new HashMap<>();
                    if (itemArray != null && itemArray.size() > 0) {
                        for (int j = 0; j < itemArray.size(); j++) {
                            itemObject = itemArray.getJSONObject(j);
                            if (itemObject != null
                                    && itemObject.size() > 0
                                    && itemObject.containsKey("value")
                                    && itemObject.containsKey("label")) {

                                if (version == APIVersion.DEV) {
                                    labelMap.put(itemObject.getInteger("label"), itemObject.getString("value"));
                                } else {
                                    labelMap.put(itemObject.getInteger("value"), itemObject.getString("label"));
                                }
                            }
                        }
                    }
                    if (labelMap.size() > 0) {
                        itemsLabelMap.put(fieldObject.getString(fieldNameKey), labelMap);
                    }
                }
            }
        }
        return itemsLabelMap;
    }

    /**
     * 从对象描述中分离出选择型字段的描述
     *
     * @param commonHttpClient HTTP Client
     * @param url              对象描述url
     * @param itemType         类型：selectitem，checkitem
     * @return 所有选择型字段对应的描述
     * @throws IOException IOException
     */
    private static Map<String, Map<Integer, String>> separateItemsLabelsFromObjectDescription(CommonHttpClient commonHttpClient,
                                                                                              APIVersion version,
                                                                                              String url,
                                                                                              ItemType itemType,
                                                                                              String accessToken)
            throws IOException {

        CommonData commonData = new CommonData();
        commonData.putHeader("Authorization", accessToken);
        commonData.setCall_type(HTTP_TYPE_GET);
        commonData.setCallString(url);

        String msg = commonHttpClient.performRequest(commonData);

        String rootNodeField; //api根节点
        String fieldNameKey; //属性字段名
        switch (version) {
            case V1:
                rootNodeField = "fields";
                fieldNameKey = "propertyname";
                break;
            case V2:
                rootNodeField = "data";
                fieldNameKey = "apikey";
                break;
            case DEV:
                rootNodeField = "data";
                fieldNameKey = "apiKey";
                break;
            default:
                rootNodeField = "";
                fieldNameKey = "";
                break;
        }

        //描述Map，key:字段名，value: item的值-item项标签
        Map<String, Map<Integer, String>> itemsLabelMap = new HashMap<>();


        JSONObject describeObject;
        if (msg != null && !msg.isEmpty() && msg.contains(rootNodeField)) {

            if (version == APIVersion.V1) {
                describeObject = JSONObject.parseObject(msg);
            } else {
                JSONObject rootObject = JSONObject.parseObject(msg);
                describeObject = rootObject.getJSONObject("data");
            }

            if (describeObject != null) {
                JSONArray fieldsArray = describeObject.getJSONArray("fields");
                JSONObject fieldObject;
                JSONObject itemObject;
                JSONArray itemArray;
                Map<Integer, String> labelMap;
                for (int i = 0; i < fieldsArray.size(); i++) {
                    fieldObject = fieldsArray.getJSONObject(i);
                    if (fieldObject == null) {
                        continue;
                    }
                    if (!fieldObject.containsKey(fieldNameKey)) {
                        continue;
                    }
                    if (!fieldObject.containsKey(itemType.getDescription())) {
                        continue;
                    }
                    itemArray = fieldObject.getJSONArray(itemType.getDescription());
                    labelMap = new HashMap<>();
                    if (itemArray != null && itemArray.size() > 0) {
                        for (int j = 0; j < itemArray.size(); j++) {
                            itemObject = itemArray.getJSONObject(j);
                            if (itemObject != null
                                    && itemObject.size() > 0
                                    && itemObject.containsKey("value")
                                    && itemObject.containsKey("label")) {

                                if (version == APIVersion.DEV) {
                                    labelMap.put(itemObject.getInteger("label"), itemObject.getString("value"));
                                } else {
                                    labelMap.put(itemObject.getInteger("value"), itemObject.getString("label"));
                                }
                            }
                        }
                    }
                    if (labelMap.size() > 0) {
                        itemsLabelMap.put(fieldObject.getString(fieldNameKey), labelMap);
                    }
                }
            }
        }
        return itemsLabelMap;
    }

    /**
     * @param object      操作的对象，可通过对象查询接口获取objectname
     * @param operation   执行的操作，目前支持：insert,update,delete,query
     * @param accessToken
     */
    public static JSONObject createAsyncJob(String object,
                                            String operation,
                                            String accessToken) {
        JSONObject createJobResultJson = null;
        JSONObject createJobParam = new JSONObject();
        JSONObject dataParam = new JSONObject();
        createJobParam.put("object", object);
        createJobParam.put("operation", operation);
        JSONArray execOptionArray = new JSONArray();
        execOptionArray.add("TRIGGER_EVENT");
        execOptionArray.add("CHECK_DUPLICATE");
        createJobParam.put("execOption", execOptionArray.toJSONString());
        dataParam.put("data", createJobParam);
        CommonHttpClient commonHttpClient = CommonHttpClient.instance();
        CommonData commonData = new CommonData();

        commonData.putHeader("Authorization", accessToken);
        commonData.setCall_type(HTTP_TYPE_POST);
        commonData.setBody(dataParam.toString());
        log.debug("创建异步作业参数=" + dataParam.toString());

        commonData.setCallString(CREATE_ASYNC_JOB_URI_V2);
        String createJobResult = commonHttpClient.performRequest(commonData);
        log.debug("创建异步作业createJobResult=" + createJobResult);
        createJobResultJson = JSONObject.parseObject(createJobResult);
        log.debug("创建异步作业createJobResultJson=" + createJobResultJson);
        return createJobResultJson;
    }

    //关闭异步作业Job
    public static JSONObject closeAsyncJob(String jobId, String accessToken) {
        JSONObject closeJobResultJson = null;
        CommonHttpClient commonHttpClient = CommonHttpClient.instance();
        CommonData commonData = new CommonData();
        JSONObject dataParam = new JSONObject();
        JSONObject closeJobParam = new JSONObject();
        closeJobParam.put("status", 3);//值为2：ABORT、值为3：CLOSE
        dataParam.put("data", closeJobParam);
        commonData.putHeader("Authorization", accessToken);
        commonData.setCall_type(HTTP_TYPE_PATCH);
        commonData.setBody(dataParam.toString());
        log.debug("关闭异步作业参数=" + dataParam.toString());
        String callString = String.format(CLOSE_ASYNC_JOB_URI_V2, jobId);
        commonData.setCallString(callString);
        String closeJobResult = commonHttpClient.performRequest(commonData);
        log.debug("关闭异步作业closeJobResult" + closeJobResult);
        closeJobResultJson = JSONObject.parseObject(closeJobResult);
        log.debug("创建异步作业closeJobResultJson=" + closeJobResultJson);
        return closeJobResultJson;
    }

    //创建异步批量任务
    public static JSONObject createAsyncBatch(String jobId, JSONArray datas, String accessToken) {
        JSONObject createBatchTaskResultJson = null;
        CommonHttpClient commonHttpClient = CommonHttpClient.instance();
        CommonData commonData = new CommonData();
        JSONObject dataParam = new JSONObject();
        JSONObject batchTaskParam = new JSONObject();
        batchTaskParam.put("jobId", jobId);
        batchTaskParam.put("datas", datas);
        dataParam.put("data", batchTaskParam);
        commonData.putHeader("Authorization", accessToken);
        commonData.setCall_type(HTTP_TYPE_POST);
        commonData.setBody(dataParam.toString());
        log.debug("创建异步批量任务参数=" + dataParam.toString());
        commonData.setCallString(CREATE_ASYNC_BATCH_URI_V2);
        String batchTaskResult = commonHttpClient.performRequest(commonData);
        log.debug("创建异步任务批量返回batchTaskResult=" + batchTaskResult);
        createBatchTaskResultJson = JSONObject.parseObject(batchTaskResult);
        log.debug("创建异步作业createBatchTaskResultJson=" + createBatchTaskResultJson);
        return createBatchTaskResultJson;
    }

    /**
     * 获取JSONObject里面的属性值
     *
     * @param obj      JSONObject
     * @param attrName 属性名称
     * @return 属性值
     */
    public static String getObjectAttribute(JSONObject obj, String attrName) {
        String value = "";

        if (obj != null && obj.containsKey(attrName)) {
            Object attrValue = obj.get(attrName);
            if (attrValue != null) {
                value = attrValue.toString();
            }
        }

        if (value.equals("null")) {
            value = "";
        }

        value = value.replace("\n", "");

        return value;
    }

    /**
     * 获取Model里面的属性值
     *
     * @param model    Model
     * @param attrName 属性名称
     * @return 属性值
     */
    public static String getObjectAttribute(DataModel model, String attrName) {
        String value = "";

        if (model != null) {
            Object attrValue = model.getAttribute(attrName);
            if (attrValue != null) {
                value = attrValue.toString();
            }
        }

        if (value.equals("null")) {
            value = "";
        }

        value = value.replace("\n", "");

        return value;
    }

    /**
     * 获取长整型数值
     *
     * @param value 字符型参数
     * @return 转换的长整型数值
     */
    public static long getLong(String value) throws NumberFormatException {
        long result = 0L;

        if (!value.isEmpty()) {
            result = Long.valueOf(value);

        }

        return result;
    }

    /**
     * 获取整型数值
     *
     * @param value 字符型参数
     * @return 转换的整型数值
     */
    public static int getInt(String value) throws NumberFormatException {
        int result = 0;

        if (!value.isEmpty()) {
            result = Integer.valueOf(value);
        }

        return result;
    }

    /**
     * 获取双精度浮点数数值
     *
     * @param value 字符型参数
     * @return 转换的双精度浮点数数值
     */
    public static double getDouble(String value) throws NumberFormatException {
        double result = 0d;

        if (!value.isEmpty()) {
            result = Double.valueOf(value);
        }

        return result;
    }

    /**
     * 获取单精度浮点数值
     *
     * @param value 字符型参数
     * @return 转换的单精度浮点数数值
     */
    public static float getFloat(String value) throws NumberFormatException {
        float result = 0f;

        if (!value.isEmpty()) {
            result = Float.valueOf(value);
        }

        return result;
    }
}