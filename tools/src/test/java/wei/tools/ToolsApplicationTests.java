package wei.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wei.tools.dao.TradingDayMapper;
import wei.tools.entity.WenCaiQueryEntity;
import wei.tools.initCode.CalendarInitService;
import wei.tools.model.TradingDay;
import wei.tools.service.ApiService;
import wei.tools.service.StockService;
import wei.tools.service.WenCaiService;
import wei.tools.util.ExcelTest;

import java.io.IOException;

@SpringBootTest
class ToolsApplicationTests {

	@Autowired
	private ApiService apiService;
	@Autowired
	private CalendarInitService calendarInitService;
	@Autowired
	private TradingDayMapper calendarMapper;
	@Autowired
	private WenCaiService wenCaiService;
	@Autowired
	private StockService stockService;

	@Test
	void contextLoads() {
		calendarInitService.initByYear(2021);
		calendarInitService.initByYear(2022);
	}

	@Test
	void testQuery(){
		TradingDay calendar = calendarMapper.selectByDayStr("2022-05-04");
		System.out.println(calendar);
	}

	@Test
	void testWencai() throws IOException {
//		System.out.println(stockService.getPriceRateByStockCodeAndDate("002235","20220209"));

		wenCaiService.reviewByDate("2022-01-04");
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
