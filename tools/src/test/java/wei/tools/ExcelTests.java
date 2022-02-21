package wei.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wei.tools.service.ExcelService;
import wei.tools.service.TradingDayService;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@SpringBootTest
class ExcelTests {

	@Autowired
	private ExcelService excelService;

	@Test
	void test() throws ParseException, IOException {
		excelService.test();
	}




}
