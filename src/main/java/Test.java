import com.rkhd.platform.sdk.data.model.ServiceCase;
import com.rkhd.platform.sdk.exception.ApiEntityServiceException;
import com.rkhd.platform.sdk.exception.XsyHttpException;
import com.rkhd.platform.sdk.model.QueryResult;
import com.rkhd.platform.sdk.model.XObject;
import com.rkhd.platform.sdk.service.XObjectService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * @author 欧桂源
 * @date 2020/11/7 13:51
 */
@Slf4j
public class Test {
    public static void main(String[] args) throws XsyHttpException, ApiEntityServiceException, IOException {
       /* RkhdHttpClient rkhdHttpClient = RkhdHttpClient.instance();
        RkhdHttpData commonData = new RkhdHttpData();
        commonData.setCallString("/rest/data/v2.0/xobjects/account/1473854063886983");
        commonData.setCall_type("GET");
        JSONObject data = rkhdHttpClient.execute(commonData, s -> {
            JSONObject jsonObject = JSONObject.parseObject(s);
            return jsonObject;
        });
        System.out.println(data.toJSONString());*/
      /*  CommonHttpClient commonHttpClient = CommonHttpClient.instance();
        CommonData commonData = new CommonData();
        commonData.setCallString("https://api-tencent.xiaoshouyi.com/rest/data/v2.0/xobjects/account/1473854063886983");
        commonData.setCall_type("GET");
        CommonResponse<JSONObject> commonResponse = commonHttpClient.execute(commonData, s -> {
            JSONObject jsonObject = JSONObject.parseObject(s);
            return jsonObject;
        });
        JSONObject data = commonResponse.getData();
        System.out.println(data.toJSONString());*/

        //通过访客查询对应的服务工单
       /* String sql = "select id from serviceCase where caseAccountId=1411588178395813";
        QueryResult<ServiceCase> queryResult = XObjectService.instance().query(sql, true);
        if (queryResult.getSuccess()) {
            List<ServiceCase> records = queryResult.getRecords();
            log.info(records.toString());
        } else {
            log.error("查询sql:" + sql + ",报错信息:" + queryResult.getErrorMessage());
        }*/
        String sql = "select id,customItem7__c,customItem14__c,customItem18__c from serviceCase where caseAccountId=1411588178395813";
        QueryResult<ServiceCase> query = XObjectService.instance().query(sql);
        if (query.getSuccess()) {
            List<ServiceCase> records = query.getRecords();
            log.info(records.toString());
        } else {
            log.error("查询sql:" + sql + ",报错信息:" + query.getErrorMessage());
        }
    }
}
