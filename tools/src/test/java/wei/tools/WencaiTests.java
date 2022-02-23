package wei.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wei.tools.dao.UpLimitMapper;
import wei.tools.model.EmotionalCycle;
import wei.tools.model.UpLimit;
import wei.tools.service.WenCaiService;

import javax.script.ScriptException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@SpringBootTest
class WencaiTests {

	@Autowired
	private WenCaiService wenCaiService;
	@Autowired
	private UpLimitMapper upLimitMapper;

	@Test
	void testHeaders() throws ParseException, IOException, InterruptedException, ScriptException, NoSuchMethodException {
//		wenCaiService.getCookieHeaders();
//		wenCaiService.getCookieHeaders();
//		wenCaiService.getCookieHeaders();
//		wenCaiService.getCookieHeaders();
//		wenCaiService.getCookieHeaders();
	}

	@Test
	void testUpList(){
		List<UpLimit> limitList = wenCaiService.queryUpLimit(new EmotionalCycle("2021-06-01"),"2021-06-01",true);
		upLimitMapper.batchInsertOrUpdate(limitList);
	}




}
