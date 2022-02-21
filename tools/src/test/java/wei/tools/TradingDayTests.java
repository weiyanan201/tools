package wei.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wei.tools.service.TradingDayService;

import java.text.ParseException;
import java.util.List;

@SpringBootTest
class TradingDayTests {

	@Autowired
	private TradingDayService tradingDayService;

	@Test
	void testTradingDayService() throws ParseException {
		List<String> lists = tradingDayService.getPeriodTradingDays("2021-04-29","2021-02-01");
		for (String str : lists){
			System.out.println(str);
		}
	}

	@Test
	void testGetNextTradingDayStr() throws ParseException {
		System.out.println(tradingDayService.getNextTradingDay("2022-02-18"));
	}


}
