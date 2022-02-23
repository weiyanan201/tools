package wei.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wei.tools.service.StockReviewService;
import wei.tools.service.TradingDayService;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@SpringBootTest
class StockReviewTests {

	@Autowired
	private StockReviewService stockReviewService;
	@Autowired
	private TradingDayService tradingDayService;

	@Test
	void testFixData() throws ParseException, IOException, InterruptedException {
		List<String> days = tradingDayService.getPeriodTradingDays("2022-02-09","2022-02-09");
		for (String day : days){
			stockReviewService.reviewByDate(day,true);
			Thread.sleep(1000l*30);
		}
	}

	@Test
	void testReviewByDate() throws IOException, ParseException {
		stockReviewService.reviewByDate("2022-02-21");
	}

	@Test
	void testOpening() throws IOException, ParseException {
		stockReviewService.openingQuery("2022-02-22");
	}

	@Test
	void testFixupCrawlerStockDetails() throws ParseException {
		List<String> days = tradingDayService.getPeriodTradingDays("2021-03-11","2021-07-15");

		for (String day : days){
			stockReviewService.fixupCrawlerStockDetails(tradingDayService.getNextTradingDay(day),day);
			System.out.println(day+"股价详情数据以爬完");
		}

	}


}
