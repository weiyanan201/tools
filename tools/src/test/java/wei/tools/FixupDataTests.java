package wei.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wei.tools.dao.EmotionalCycleMapper;
import wei.tools.dao.UpLimitMapper;
import wei.tools.model.EmotionalCycle;
import wei.tools.model.UpLimit;
import wei.tools.service.ExcelService;
import wei.tools.service.StockReviewService;
import wei.tools.service.TradingDayService;
import wei.tools.service.WenCaiService;

import javax.script.ScriptException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@SpringBootTest
class FixupDataTests {

	@Autowired
	private WenCaiService wenCaiService;
	@Autowired
	private UpLimitMapper upLimitMapper;
	@Autowired
	private EmotionalCycleMapper emotionalCycleMapper;
	@Autowired
	private StockReviewService stockReviewService;
	@Autowired
	private TradingDayService tradingDayService;
	@Autowired
	private ExcelService excelService;


	//补涨停数据+题材
	//mysql uplimit + firstTime字段
	//mysql emotionalCycle + hotTheme
	//excel +hotTheme
	@Test
	void testFixupUpLimit() throws ParseException, IOException {
		List<String> days = tradingDayService.getPeriodTradingDays("2022-02-07","2022-02-21");
		for (String day : days){
			EmotionalCycle emotionalCycle = emotionalCycleMapper.selectByDate(day);
			List<UpLimit> upLimits = wenCaiService.queryUpLimit(emotionalCycle,day,true);
			emotionalCycle.setHotThemeOrderLimit(emotionalCycle.getHotThemeOrderLimit());

			upLimitMapper.batchInsertOrUpdate(upLimits);
			emotionalCycleMapper.insertOrUpdate(emotionalCycle);
			excelService.writeEmotionCycleResult(emotionalCycle,day);

			System.out.println(day + " 运行完成!");
		}
	}




}
