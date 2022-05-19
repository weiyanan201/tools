package wei.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wei.tools.aop.PageAspect;
import wei.tools.service.StockReviewService;
import wei.tools.service.TradingDayService;

import java.io.*;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class StockReviewTests {

	@Autowired
	private StockReviewService stockReviewService;
	@Autowired
	private TradingDayService tradingDayService;

	@Test
	void testFixData() throws ParseException, IOException, InterruptedException {
		List<String> days = tradingDayService.getPeriodTradingDays("2022-05-07","2022-05-17");
		for (String day : days){
			stockReviewService.reviewByDate(day,true);
		}
	}

	@Test
	void testReviewByDate() throws IOException, ParseException {
		String dateStr = "2022-05-06";
		stockReviewService.reviewByDate(dateStr);
	}

	@Test
	void testOpening() throws IOException, ParseException {
		stockReviewService.openingQuery("2022-03-01");
	}

	@Test
	void testOpenLoop() throws IOException, ParseException, InterruptedException {


		stockReviewService.openLoopQuery();
	}

	@Test
	void testFixupCrawlerStockDetails() throws ParseException {
		List<String> days = tradingDayService.getPeriodTradingDays("2021-03-11","2021-07-15");

		for (String day : days){
			stockReviewService.fixupCrawlerStockDetails(tradingDayService.getNextTradingDay(day),day);
			System.out.println(day+"股价详情数据以爬完");
		}

	}

	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\homay\\Desktop\\杰哥-2022.05.html");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		StringBuilder sb = new StringBuilder();
		String line = "";
		while((line=br.readLine())!=null){
			sb.append(line);
		}
		Pattern datePattern = Pattern.compile("class=\"date\">(.*?)</div>");
		Pattern answerPattern = Pattern.compile("class=\"answer\">(.*?)</div>");
		Pattern questionPattern = Pattern.compile("class=\"question-contain\">(.*?)</div>") ;
		//杰哥回答问题
		Pattern answerAllPattern = Pattern.compile("class=\"date\">(.*?)</div>(.*?)class=\"question-contain\">(.*?)</div>(.*?)class=\"answer\">(.*?)</div>");

		Pattern commentPattern = Pattern.compile("class=\"comment group-owner-light\">杰哥(.*?)class=\"text\">(.*?)</span>");
		//杰哥发言
		//杰哥评论
		Matcher matcher = answerAllPattern.matcher(sb.toString());
		while(matcher.find()){
			System.out.println("xxx");
		}
	}


}
