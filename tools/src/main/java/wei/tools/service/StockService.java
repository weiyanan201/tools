package wei.tools.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wei.tools.entity.StockEntity;
import wei.tools.exception.ToolsException;

import java.text.DecimalFormat;


/**
 * @Author: weiyanan
 * @Date: 2022/2/10 17:34
 */
@Service
public class StockService {

    private static String URL_QUERY_HISTORY = "https://q.stock.sohu.com/hisHq?code=%s&start=%s&end=%s";

    @Autowired
    private ApiService apiService;


    public StockEntity getDetailByStockCodeAndDate(String stockCode,String dateStr){
        stockCode = "cn_" + stockCode;
        String url = String.format(URL_QUERY_HISTORY,stockCode,dateStr,dateStr);
        String resultStr = apiService.get(url,String.class);
        JSONArray resultArray = JSONArray.parseArray(resultStr);
        JSONObject resultJson = resultArray.getJSONObject(0);
        if (resultJson.getInteger("status")!=0){
            throw new ToolsException("获取历史股价接口出现异常");
        }

        StockEntity resultEntity = new StockEntity();

        JSONArray dataArray = resultJson.getJSONArray("hq");
        JSONArray detailArray = dataArray.getJSONArray(0);

        float openPrice = detailArray.getFloat(1);
        float closePrice = detailArray.getFloat(2);
        float diffPrice = detailArray.getFloat(3);
        String rateStr = detailArray.getString(4);
        float minPrice = detailArray.getFloat(5);
        float maxPrice = detailArray.getFloat(6);
        String turnoverStr = detailArray.getString(9);
        float swingRate_cal = (maxPrice-minPrice)/(closePrice-diffPrice)*100;
        DecimalFormat df = new DecimalFormat("#.00");
        float swingRate = Float.valueOf(df.format(swingRate_cal));

        resultEntity.setDateStr(dateStr);
        resultEntity.setOpenPrice(openPrice);
        resultEntity.setClosePrice(closePrice);
        resultEntity.setDiffPrice(diffPrice);
        resultEntity.setPriceRatePercentStr(rateStr);
        resultEntity.setPriceRate(Float.valueOf(rateStr.replaceAll("%","")));
        resultEntity.setMinPrice(minPrice);
        resultEntity.setMaxPrice(maxPrice);
        resultEntity.setTurnoverRate(Float.valueOf(turnoverStr.replaceAll("%","")));
        resultEntity.setSwingRate(swingRate);

        return resultEntity;
    }

}
