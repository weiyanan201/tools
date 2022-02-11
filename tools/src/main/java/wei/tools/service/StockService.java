package wei.tools.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wei.tools.exception.ToolsException;

/**
 * @Author: weiyanan
 * @Date: 2022/2/10 17:34
 */
@Service
public class StockService {

    private static String URL_QUERY_HISTORY = "https://q.stock.sohu.com/hisHq?code=%s&start=%s&end=%s";

    @Autowired
    private ApiService apiService;

    /**
     * 获取个股某日的涨跌比例
     * 不带百分号
     * @return
     */
    public float getPriceRateByStockCodeAndDate(String stockCode,String dateStr){
        //TODO 严格要检验时间格式并转化
        stockCode = "cn_" + stockCode;
        String url = String.format(URL_QUERY_HISTORY,stockCode,dateStr,dateStr);
        String resultStr = apiService.get(url,String.class);
        JSONArray resultArray = JSONArray.parseArray(resultStr);
        JSONObject resultJson = resultArray.getJSONObject(0);
        if (resultJson.getInteger("status")!=0){
            throw new ToolsException("获取历史股价接口出现异常");
        }
        JSONArray dataArray = resultJson.getJSONArray("hq");
        JSONArray detailArray = dataArray.getJSONArray(0);

        String rateStr = detailArray.getString(4);
        return Float.valueOf(rateStr.replaceAll("%",""));


    }

}
