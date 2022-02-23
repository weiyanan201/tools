package wei.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wei.tools.service.StockComputeService;
import wei.tools.service.StockReviewService;
import wei.tools.service.TradingDayService;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

@SpringBootTest
class StockComputeTests {

	@Autowired
	private StockReviewService stockReviewService;
	@Autowired
	private StockComputeService stockComputeService;

	@Test
	void testComputeLimitEarnings() throws IOException, ParseException {
		String last = "2021-06-01";
		String date = "2021-06-02";
		stockComputeService.computeLimitEarnings(last,date);
	}

	@Test
	void testBrokenRate() throws IOException {
		String date = "2021-06-02";
		stockComputeService.computeBrokenRate(date);
	}

	public static void main(String[] args) {
		double result1=0.51111122111111;
		DecimalFormat df = new DecimalFormat("0.00%");
		String r = df.format(result1);
		System.out.println(r);//great
	}


}
