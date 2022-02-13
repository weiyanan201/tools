package wei.tools;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wei.tools.entity.WenCaiQueryEntity;
import wei.tools.initCode.CalendarInitService;
import wei.tools.model.StockDetail;
import wei.tools.service.*;

import java.io.IOException;
import java.text.ParseException;

@SpringBootTest
class ToolsApplicationTests {

	@Autowired
	private ApiService apiService;
	@Autowired
	private CalendarInitService calendarInitService;
	@Autowired
	private TradingDayService tradingDayService;
	@Autowired
	private WenCaiService wenCaiService;
	@Autowired
	private StockDetailService stockDetailService;

	@Test
	void testTradingDayService() throws ParseException {
		String str = tradingDayService.getLastTradingDay("2021-01-01");
		System.out.println(str);
		System.out.println(StringUtils.isBlank(str));
		System.out.println(str==null);
	}

	@Test
	void testStockEntity(){
		StockDetail entity = stockDetailService.getDetailByStockCodeAndDate("002235","安妮股份","20220211");
		System.out.println(entity);
	}

	@Test
	void testWencai() throws IOException, ParseException {
//		System.out.println(stockService.getPriceRateByStockCodeAndDate("002235","20220209"));

		wenCaiService.reviewByDate("2022-02-11");
//		System.out.println(wenCaiService.queryStockTheme("安妮股份"));
//		System.out.println(result);

		WenCaiQueryEntity entity = new WenCaiQueryEntity();
		entity.setUpCount(598);
		entity.setDropCount(4044);
		entity.setUpLimitCount(49);
		entity.setDropLimitCount(9);
		entity.setBreakCount(50);
		entity.setSecondCount(7);
		entity.setThirdCount(3);
		entity.setThirdStr("京蓝科技、出版传媒、御银股份");
		entity.setMoreCount(6);
		entity.setMoreStr("保利联合7、恒宝股份5、冀东装备5、重庆建工4、元隆雅图4、浙江建投4");
//		ExcelTest.readReviewFile(entity);

//		wenCaiService.writeResult(entity,"2022-02-11");
	}
}
