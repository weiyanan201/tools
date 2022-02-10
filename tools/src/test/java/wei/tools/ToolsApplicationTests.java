package wei.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import wei.tools.dao.CalendarMapper;
import wei.tools.initCode.CalendarInitService;
import wei.tools.model.Calendar;
import wei.tools.service.ApiService;
import wei.tools.service.WenCaiService;

import java.io.IOException;

@SpringBootTest
class ToolsApplicationTests {

	@Autowired
	private ApiService apiService;
	@Autowired
	private CalendarInitService calendarInitService;
	@Autowired
	private CalendarMapper calendarMapper;
	@Autowired
	private WenCaiService wenCaiService;

	@Test
	void contextLoads() {
		calendarInitService.initByYear(2021);
		calendarInitService.initByYear(2022);
	}

	@Test
	void testQuery(){
		Calendar calendar = calendarMapper.selectByDayStr("2022-05-04");
		System.out.println(calendar);
	}

	@Test
	void testWencai() throws IOException {

		wenCaiService.queryByDate("2022-02-10");
//		System.out.println(result);
	}
}
