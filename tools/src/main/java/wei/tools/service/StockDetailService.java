package wei.tools.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wei.tools.dao.StockDetailMapper;
import wei.tools.exception.ToolsException;
import wei.tools.model.StockDetail;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @Author: weiyanan
 * @Date: 2022/2/13 23:15
 */
@Service
public class StockDetailService {

    private static Logger logger = LoggerFactory.getLogger(StockDetailService.class);

    private DecimalFormat df = new DecimalFormat("#.00");;

    private static String URL_QUERY_HISTORY = "https://q.stock.sohu.com/hisHq?code=%s&start=%s&end=%s";

    @Autowired
    private ApiService apiService;
    @Autowired
    private StockDetailMapper stockDetailMapper;

    /**
     * 个股股价详情
     * @param stockCode
     * @param stockName
     * @param dateUnSymbolStr 无符号
     * @return
     */
    public StockDetail getDetailByStockCodeAndDate(String stockCode,String stockName, String dateUnSymbolStr){

        String queryCode = "cn_" + stockCode;
        String url = String.format(URL_QUERY_HISTORY,queryCode,dateUnSymbolStr,dateUnSymbolStr);
        String resultStr = apiService.get(url,String.class);
        JSONArray resultArray = JSONArray.parseArray(resultStr);
        JSONObject resultJson = resultArray.getJSONObject(0);
        if (resultJson.getInteger("status")!=0){
            throw new ToolsException("获取历史股价接口出现异常");
        }

        StockDetail stockDetail = new StockDetail();

        JSONArray dataArray = resultJson.getJSONArray("hq");
        JSONArray detailArray = dataArray.getJSONArray(0);

        float openPrice = detailArray.getFloat(1);
        float closePrice = detailArray.getFloat(2);
        float diffPrice = detailArray.getFloat(3);
        String closePriceRateStr = detailArray.getString(4);
        float minPrice = detailArray.getFloat(5);
        float maxPrice = detailArray.getFloat(6);
        float lastDayClosePrice = closePrice-diffPrice;
        float openPriceRateCal = (openPrice-lastDayClosePrice)/lastDayClosePrice*100;
        float minPriceRateCal = (minPrice-lastDayClosePrice)/lastDayClosePrice*100;
        float maxPriceRateCal = (maxPrice-lastDayClosePrice)/lastDayClosePrice*100;

        String turnoverStr = detailArray.getString(9);
        float swingRateCal = (maxPrice-minPrice)/(closePrice-diffPrice)*100;
        float swingRate = Float.valueOf(df.format(swingRateCal));

        stockDetail.setCode(stockCode);
        stockDetail.setName(stockName);
        stockDetail.setDateStr(dateUnSymbolStr);
        stockDetail.setOpenPrice(getBigDecimal(openPrice));
        stockDetail.setOpenPriceRate(getBigDecimal(openPriceRateCal));
        stockDetail.setClosePrice(getBigDecimal(closePrice));
        stockDetail.setClosePriceRate(getBigDecimal(Float.valueOf(closePriceRateStr.replaceAll("%",""))));
        stockDetail.setDiffPrice(getBigDecimal(diffPrice));
        stockDetail.setMinPrice(getBigDecimal(minPrice));
        stockDetail.setMinPriceRate(getBigDecimal(minPriceRateCal));
        stockDetail.setMaxPrice(getBigDecimal(maxPrice));
        stockDetail.setMaxPriceRate(getBigDecimal(maxPriceRateCal));
        stockDetail.setTurnoverRate(getBigDecimal(Float.valueOf(turnoverStr.replaceAll("%",""))));
        stockDetail.setSwingRate(getBigDecimal(Float.valueOf(df.format(swingRate))));
        stockDetail.setLastDayClosePrice(getBigDecimal(lastDayClosePrice));

        return stockDetail;
    }

    /**
     * 批量获取股价
     * @param codes
     * @param codeNameMap code->name
     * @param dateStr
     * @return
     */
    public List<StockDetail> getDetailsByDate(List<String> codes, Map<String,String> codeNameMap, String dateStr){

        List<StockDetail> results = Lists.newArrayList();
        if (codeNameMap==null || codeNameMap.size()==0){
            throw new ToolsException("stock name to name map is empty！");
        }

        for (String code : codes){
            if (!codeNameMap.containsKey(code)){
                logger.warn("获取{}股价出现异常!",code);
                continue;
            }
            results.add(getDetailByStockCodeAndDate(code,codeNameMap.get(code),dateStr));
        }
        return results;
    }

    private BigDecimal getBigDecimal(Float number){
        return new BigDecimal(number).setScale(2,BigDecimal.ROUND_HALF_UP);
    }


    public static void main(String[] args) {

    }
}
