package wei.tools;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
	@Autowired
	private StockReviewService stockReviewService;

	@Test
	void testTradingDayService() throws ParseException {
		String str = tradingDayService.getLastTradingDay("2021-01-01");
		System.out.println(str);
		System.out.println(StringUtils.isBlank(str));
		System.out.println(str==null);
	}

	@Test
	void testStockEntity(){
		System.out.println(wenCaiService.isST("603996","st中新","20210104"));
	}


	/**
	 * 每日复盘
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void reviewByDate() throws IOException, ParseException {
		stockReviewService.reviewByDate("2021-01-05");

	}

	/**
	 * 盘中监控
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testOpeningQuery() throws IOException, ParseException {
		stockReviewService.openingQuery("2022-02-17");
	}
}
