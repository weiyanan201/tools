package wei.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wei.tools.dao.BrokenLimitMapper;
import wei.tools.dao.UpLimitMapper;
import wei.tools.model.BrokenLimit;
import wei.tools.model.EmotionalCycle;
import wei.tools.model.UpLimit;
import wei.tools.service.WenCaiService;
import wei.tools.util.DateUtils;

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
	@Autowired
	private BrokenLimitMapper brokenLimitMapper;

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

	@Test
	void testLimitDetail(){
		List<UpLimit> limits = wenCaiService.queryUpLimit(new EmotionalCycle("2021-06-01"),"2022-03-21",false);
		for (UpLimit upLimit : limits){
			System.out.println("xxx");
		}
		upLimitMapper.batchInsertOrUpdate(limits);
	}

	@Test
	void testBrokenDetail() throws ParseException {
		List<BrokenLimit> limits = wenCaiService.queryBrokenLimit(new EmotionalCycle("2021-06-01"),"2022-03-22",true);
		for (BrokenLimit upLimit : limits){
			System.out.println("xxx");
		}
		brokenLimitMapper.batchInsertOrUpdate(limits);
	}

	public static void main(String[] args) throws ParseException {
		long time = DateUtils.getPMOpenTime("2022-03-22");
		System.out.println(time);
	}

	@Test
	public void testPickPmFirstLimit() throws ParseException {
		wenCaiService.pickPmFirstLimit("2022-01-10");
	}

	@Test
	public void testEarningRate(){
		wenCaiService.queryEarningRate(new EmotionalCycle("2022-04-08"),"2022-04-12");
	}


}
