package wei.tools.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wei.tools.model.BrokenRate;
import wei.tools.model.EmotionalCycle;
import wei.tools.model.LimitEarnings;
import wei.tools.util.DecimalUtils;

import java.io.*;
import java.util.List;

/**
 * @Author: weiyanan
 * @Date: 2022/2/17 14:59
 */
@Service
public class ExcelService {

    @Value("${review.filePath}")
    private String excelFilePath ;

    public void test() throws IOException {
        InputStream in  = new FileInputStream(new File(excelFilePath));
        XSSFWorkbook wb = new XSSFWorkbook(in);
        FormulaEvaluator formulaEvaluator = new XSSFFormulaEvaluator(wb);

        Sheet sheet = wb.getSheet("涨停收益");
        String dateStr = "01.15";
        List<Row> rows = calculateInsertRows(sheet,dateStr,2,3);
        for (Row row:rows){
            Cell cell1 = row.createCell(0);
            cell1.setCellStyle(sheet.getColumnStyle(0));
            cell1.setCellValue(dateStr);
            //红盘
            Cell cell2 = row.createCell(1);
            cell2.setCellStyle(sheet.getColumnStyle(1));
            cell2.setCellValue("222222");
        }

        FileOutputStream out = new FileOutputStream(excelFilePath);
        wb.write(out);

    }

    /**
     * 结果写入excel
     */
    public void writeEmotionCycleResult(EmotionalCycle emotionalCycle, String dateStr) throws IOException {

        String[] dss = dateStr.split("-");
        String dateExStr = dss[1]+"."+dss[2];

        InputStream in  = new FileInputStream(new File(excelFilePath));
        XSSFWorkbook wb = new XSSFWorkbook(in);
        FormulaEvaluator formulaEvaluator = new XSSFFormulaEvaluator(wb);

        Sheet sheet = wb.getSheet("情绪周期表");



        Row insertRow = calculateInsertRow(sheet,dateExStr);
        int insertNum = insertRow.getRowNum()+1;

        //写入数据
        short height = 800;
        insertRow.setHeight(height);
        //日期
        Cell cell1 = insertRow.createCell(0);
        cell1.setCellStyle(sheet.getColumnStyle(0));
        cell1.setCellValue(dateExStr);
        //红盘
        Cell cell2 = insertRow.createCell(1);
        cell2.setCellStyle(sheet.getColumnStyle(1));
        cell2.setCellValue(emotionalCycle.getUpCount());
        //绿盘
        Cell cell3 = insertRow.createCell(2);
        cell3.setCellStyle(sheet.getColumnStyle(2));
        cell3.setCellValue(emotionalCycle.getDropCount());
        //涨停
        Cell cell4 = insertRow.createCell(3);
        cell4.setCellStyle(sheet.getColumnStyle(1));
        cell4.setCellValue(emotionalCycle.getUpLimit());
        //跌停
        Cell cell5 = insertRow.createCell(4);
        cell5.setCellStyle(sheet.getColumnStyle(4));
        cell5.setCellValue(emotionalCycle.getDropLimit());
        //炸板
        Cell cell6 = insertRow.createCell(5);
        cell6.setCellStyle(sheet.getColumnStyle(5));
        cell6.setCellValue(emotionalCycle.getBrokenLimit());
        //cell7 炸板率excel自己计算
        Cell cell7 = insertRow.createCell(6);
        cell7.setCellStyle(sheet.getColumnStyle(6));
//        String cell7Format = "F%d/(D%d+F%d)";
//        cell7.setCellFormula(String.format(cell7Format,insertNum,insertNum,insertNum));
        cell7.setCellValue(DecimalUtils.covertPercent((float) (1.0*emotionalCycle.getBrokenLimit()/(emotionalCycle.getUpLimit()+emotionalCycle.getBrokenLimit()))));
        //cell8 首板excel自己计算
        Cell cell8 = insertRow.createCell(7);
        cell8.setCellStyle(sheet.getColumnStyle(7));
//        String cell8Format = "D%d-I%d-J%d-L%d";
//        cell8.setCellFormula(String.format(cell8Format,insertNum,insertNum,insertNum,insertNum));
        cell8.setCellValue(emotionalCycle.getFirstLimit());
        //2板
        Cell cell9 = insertRow.createCell(8);
        cell9.setCellStyle(sheet.getColumnStyle(8));
        cell9.setCellValue(emotionalCycle.getSecondLimit());
        //3板个数
        Cell cell10 = insertRow.createCell(9);
        cell10.setCellStyle(sheet.getColumnStyle(9));
        cell10.setCellValue(emotionalCycle.getThirdLimit());
        //3板详情
        Cell cell11 = insertRow.createCell(10);
        cell11.setCellStyle(sheet.getColumnStyle(10));
        cell11.setCellValue(emotionalCycle.getThirdLimitStr());
        //3板以上
        Cell cell12 = insertRow.createCell(11);
        cell12.setCellStyle(sheet.getColumnStyle(11));
        cell12.setCellValue(emotionalCycle.getMoreLimit());
        //3板以上详情
        Cell cell13 = insertRow.createCell(12);
        cell13.setCellStyle(sheet.getColumnStyle(12));
        cell13.setCellValue(emotionalCycle.getMoreLimitStr());
        //热门板块
        Cell cell14 = insertRow.createCell(13);
        cell14.setCellStyle(sheet.getColumnStyle(13));
        cell14.setCellValue(emotionalCycle.getHotThemeOrderLimit());
        //热门题材
        Cell cell15 = insertRow.createCell(14);
        cell15.setCellStyle(sheet.getColumnStyle(14));
        cell15.setCellValue(emotionalCycle.getHotBusinessOrderLimit());
//        //昨日涨停
//        Cell cell16 = insertRow.createCell(15);
//        cell16.setCellStyle(sheet.getColumnStyle(15));
//        cell16.setCellValue(emotionalCycle.getEarningLimitRate().floatValue());
//        //昨日连板
//        Cell cell17 = insertRow.createCell(16);
//        cell17.setCellStyle(sheet.getColumnStyle(16));
//        cell17.setCellValue(emotionalCycle.getEarningSequenceLimitRate().floatValue());
//        //昨日炸板
//        Cell cell18 = insertRow.createCell(17);
//        cell18.setCellStyle(sheet.getColumnStyle(17));
//        cell18.setCellValue(emotionalCycle.getEarningBrokenLimitRate().floatValue());
//        //同花顺热门股收益
//        Cell cell19 = insertRow.createCell(18);
//        cell19.setCellStyle(sheet.getColumnStyle(18));
//        cell19.setCellValue(emotionalCycle.getEarningHotRate().floatValue());

//        formulaEvaluator.evaluateAll();

        FileOutputStream out = new FileOutputStream(excelFilePath);
        wb.write(out);

    }

    @Deprecated
    public void fixUpdateEarningRate(EmotionalCycle emotionalCycle, String dateStr) throws IOException {
        String[] dss = dateStr.split("-");
        String dateExStr = dss[1]+"."+dss[2];

        InputStream in  = new FileInputStream(new File(excelFilePath));
        XSSFWorkbook wb = new XSSFWorkbook(in);
        FormulaEvaluator formulaEvaluator = new XSSFFormulaEvaluator(wb);

        Sheet sheet = wb.getSheet("情绪周期表");


        Row insertRow = calculateInsertRow(sheet,dateExStr);
        int insertNum = insertRow.getRowNum()+1;

        //写入数据
        short height = 800;
        insertRow.setHeight(height);

        //昨日涨停
        Cell cell16 = insertRow.createCell(15);
        cell16.setCellStyle(sheet.getColumnStyle(15));
        cell16.setCellValue(emotionalCycle.getEarningLimitRate().floatValue());
        //昨日连板
        Cell cell17 = insertRow.createCell(16);
        cell17.setCellStyle(sheet.getColumnStyle(16));
        cell17.setCellValue(emotionalCycle.getEarningSequenceLimitRate().floatValue());
        //昨日炸板
        Cell cell18 = insertRow.createCell(17);
        cell18.setCellStyle(sheet.getColumnStyle(17));
        cell18.setCellValue(emotionalCycle.getEarningBrokenLimitRate().floatValue());
        //同花顺热门股收益
        Cell cell19 = insertRow.createCell(18);
        cell19.setCellStyle(sheet.getColumnStyle(18));
        cell19.setCellValue(emotionalCycle.getEarningHotRate().floatValue());

//        formulaEvaluator.evaluateAll();

        FileOutputStream out = new FileOutputStream(excelFilePath);
        wb.write(out);
    }

    /**
     * 涨停、炸板次日收益
     * @throws IOException
     */
    public void writeUpLimitEarningResult(List<LimitEarnings> limits,List<LimitEarnings>brokens,String dateStr) throws IOException {


        String[] dss = dateStr.split("-");
        String dateExStr = dss[1]+"."+dss[2];
        String limitSheetName = "涨停收益";
        String brokenSheetName = "炸板收益";

        InputStream in  = new FileInputStream(new File(excelFilePath));
        XSSFWorkbook wb = new XSSFWorkbook(in);
        String sheetName = "";

        //涨停收益
        if (limits!=null && limits.size()>0){
            Sheet sheet = wb.getSheet(limitSheetName);
            List<Row> rows = calculateInsertRows(sheet,dateExStr,2,limits.size());
            for (int i=0;i<rows.size();i++){
                Row row = rows.get(i);
                LimitEarnings limitEarnings = limits.get(i);
                packLimitEarningRow(sheet,row,limitEarnings,dateExStr);
            }
        }
        //炸板收益
        if (brokens!=null && brokens.size()>0){
            Sheet sheet = wb.getSheet(brokenSheetName);
            List<Row> rows = calculateInsertRows(sheet,dateExStr,2,brokens.size());
            for (int i=0;i<rows.size();i++){
                Row row = rows.get(i);
                LimitEarnings limitEarnings = limits.get(i);
                packLimitEarningRow(sheet,row,limitEarnings,dateExStr);
            }
        }

        FileOutputStream out = new FileOutputStream(excelFilePath);
        wb.write(out);
    }

    public void writeBrokenRateResult(BrokenRate brokenRate,String dateStr) throws IOException {
        String dateExStr = coverDate(dateStr);
        InputStream in  = new FileInputStream(new File(excelFilePath));
        XSSFWorkbook wb = new XSSFWorkbook(in);
        FormulaEvaluator formulaEvaluator = new XSSFFormulaEvaluator(wb);

        Sheet sheet = wb.getSheet("炸板率");
        Row insertRow = calculateInsertRow(sheet,dateExStr);
        int insertNum = insertRow.getRowNum()+1;

        //写入数据
        Cell cell1 = insertRow.createCell(0);
        cell1.setCellStyle(sheet.getColumnStyle(0));
        cell1.setCellValue(dateExStr);
        //涨停家数
        Cell cell2 = insertRow.createCell(1);
        cell2.setCellStyle(sheet.getColumnStyle(1));
        cell2.setCellValue(brokenRate.getLimitCount());
        //炸板家数
        Cell cell3 = insertRow.createCell(2);
        cell3.setCellStyle(sheet.getColumnStyle(2));
        cell3.setCellValue(brokenRate.getBrokenCount());
        //炸板率
        Cell cell4 = insertRow.createCell(3);
        cell4.setCellStyle(sheet.getColumnStyle(3));
        cell4.setCellValue(DecimalUtils.covertPercent(brokenRate.getBrokenRate()));
        //首板涨停个数
        Cell cell5 = insertRow.createCell(4);
        cell5.setCellStyle(sheet.getColumnStyle(4));
        cell5.setCellValue(brokenRate.getFirstLimitCount());
        //首板炸板个数
        Cell cell6 = insertRow.createCell(5);
        cell6.setCellStyle(sheet.getColumnStyle(5));
        cell6.setCellValue(brokenRate.getFirstBrokenCount());
        //首板炸板率
        Cell cell7 = insertRow.createCell(6);
        cell7.setCellStyle(sheet.getColumnStyle(6));
        cell7.setCellValue(DecimalUtils.covertPercent(brokenRate.getFirstBrokenRate()));
        //2板涨停个数
        Cell cell8 = insertRow.createCell(7);
        cell8.setCellStyle(sheet.getColumnStyle(7));
        cell8.setCellValue(brokenRate.getSecondLimitCount());
        //2板炸板个数
        Cell cell9 = insertRow.createCell(8);
        cell9.setCellStyle(sheet.getColumnStyle(8));
        cell9.setCellValue(brokenRate.getSecondBrokenCount());
        //2板炸板率
        Cell cell10 = insertRow.createCell(9);
        cell10.setCellStyle(sheet.getColumnStyle(9));
        cell10.setCellValue(DecimalUtils.covertPercent(brokenRate.getSecondBrokenRate()));
        //3板涨停个数
        Cell cell11 = insertRow.createCell(10);
        cell11.setCellStyle(sheet.getColumnStyle(10));
        cell11.setCellValue(brokenRate.getThirdLimitCount());
        //3板炸板个数
        Cell cell12 = insertRow.createCell(11);
        cell12.setCellStyle(sheet.getColumnStyle(11));
        cell12.setCellValue(brokenRate.getThirdBrokenCount());
        //3板炸板率
        Cell cell13 = insertRow.createCell(12);
        cell13.setCellStyle(sheet.getColumnStyle(12));
        cell13.setCellValue(DecimalUtils.covertPercent(brokenRate.getThirdBrokenRate()));
        //3+涨停个数
        Cell cell14 = insertRow.createCell(13);
        cell14.setCellStyle(sheet.getColumnStyle(13));
        cell14.setCellValue(brokenRate.getMoreLimitCount());
        //3+炸板个数
        Cell cell15 = insertRow.createCell(14);
        cell15.setCellStyle(sheet.getColumnStyle(14));
        cell15.setCellValue(brokenRate.getMoreBrokenCount());
        //3+炸板率
        Cell cell16 = insertRow.createCell(15);
        cell16.setCellStyle(sheet.getColumnStyle(15));
        cell16.setCellValue(DecimalUtils.covertPercent(brokenRate.getMoreBrokenRate()));

//        formulaEvaluator.evaluateAll();

        FileOutputStream out = new FileOutputStream(excelFilePath);
        wb.write(out);

    }

    /**
     * mm-dd 转化成 mm.dd
     * @param dateStr
     * @return
     */
    private String coverDate(String dateStr){
        String[] dss = dateStr.split("-");
        String dateExStr = dss[1]+"."+dss[2];
        return dateExStr;
    }

    private void packLimitEarningRow(Sheet sheet,Row row,LimitEarnings limitEarnings,String dateExStr){
        //日期
        Cell cell1 = row.createCell(0);
        cell1.setCellStyle(sheet.getColumnStyle(0));
        cell1.setCellValue(dateExStr);
        //第几版
        Cell cell2 = row.createCell(1);
        cell2.setCellStyle(sheet.getColumnStyle(1));
        cell2.setCellValue(limitEarnings.getLimitType()+"板");
        //个数
        Cell cell3 = row.createCell(2);
        cell3.setCellStyle(sheet.getColumnStyle(2));
        cell3.setCellValue(limitEarnings.getLimitNum());
        //次日开盘收益
        Cell cell4 = row.createCell(3);
        cell4.setCellStyle(sheet.getColumnStyle(3));
        cell4.setCellValue(DecimalUtils.roundValue(limitEarnings.getAvgOpen()));
        //次日最高
        Cell cell5 = row.createCell(4);
        cell5.setCellStyle(sheet.getColumnStyle(4));
        cell5.setCellValue(DecimalUtils.roundValue(limitEarnings.getAvgMax()));
        //次日最低
        Cell cell6 = row.createCell(5);
        cell6.setCellStyle(sheet.getColumnStyle(5));
        cell6.setCellValue(DecimalUtils.roundValue(limitEarnings.getAvgMin()));
        //次日收盘
        Cell cell7 = row.createCell(6);
        cell7.setCellStyle(sheet.getColumnStyle(6));
        cell7.setCellValue(DecimalUtils.roundValue(limitEarnings.getAvgClose()));
    }

    /**
     * 定位到要插入的行，新增则创建新的
     * @param sheet
     * @param dateExStr
     * @return
     */
    private Row calculateInsertRow(Sheet sheet,String dateExStr ){
        //先找要插入的位置
        Row insertRow;
        boolean isNewRow = true;
        int insertIndex = 0;
        for (int i=2;i<=sheet.getLastRowNum();i++){
            //取第一列 比较日期
            Cell cell = sheet.getRow(i).getCell(0);
            if(cell==null || StringUtils.isBlank(cell.getStringCellValue())){
                continue;
            }
            if (StringUtils.compare(dateExStr,cell.getStringCellValue())<=0){
                if (StringUtils.compare(dateExStr,cell.getStringCellValue())==0){
                    //该日期已有数据，覆盖
                    isNewRow = false;
                }
                insertIndex = i;
                break;
            }
        }

        if (isNewRow){
            if (insertIndex==0){
                //不需要插入，追加数据
                insertRow = sheet.createRow(sheet.getLastRowNum()+1);
            }else{
                //插入，后移原数据再插入
                sheet.shiftRows(insertIndex, sheet.getLastRowNum(), 1);
                //poi shiftRows bug
                if (sheet instanceof XSSFSheet) {
                    XSSFSheet xSSFSheet = (XSSFSheet) sheet;
                    for (int r = xSSFSheet.getFirstRowNum(); r < sheet.getLastRowNum() + 1; r++) {
                        XSSFRow row = xSSFSheet.getRow(r);
                        if (row != null) {
                            long rRef = row.getCTRow().getR();
                            for (Cell cell : row) {
                                String cRef = ((XSSFCell) cell).getCTCell().getR();
                                ((XSSFCell) cell).getCTCell().setR(cRef.replaceAll("[0-9]", "") + rRef);
                            }
                        }
                    }
                }
                insertRow = sheet.createRow(insertIndex);
            }
        }else{
            insertRow = sheet.getRow(insertIndex);
        }
        return insertRow;
    }


    /**
     * 定位到要插入的行，新增则创建新的,原来的数据被删除
     * @param sheet
     * @param dateExStr
     * @param dataBeginIndex
     * @param nums
     * @return
     */
    private List<Row> calculateInsertRows(Sheet sheet, String dateExStr,int dataBeginIndex,int nums ){
        //先找要插入的位置
        List<Row> rows = Lists.newArrayList();
        int insertIndex = 0;
        boolean hasDelete = false;
        int deleteCount = 0;
        //bug 最后一行数据与插入数据相同，没有发生移动
        boolean isLastDelete = false;
        for (int i=dataBeginIndex;i<=sheet.getLastRowNum();i++){
            //取第一列 比较日期
            Cell cell = sheet.getRow(i).getCell(0);
            if(cell==null || StringUtils.isBlank(cell.getStringCellValue())){
                continue;
            }
            if (StringUtils.compare(dateExStr,cell.getStringCellValue())<=0){
                if (StringUtils.compare(dateExStr,cell.getStringCellValue())==0){
                    //该日期已有数据，删除多行
                    sheet.removeRow(sheet.getRow(i));
                    if (!hasDelete){
                        insertIndex = i;
                        hasDelete = true;
                        isLastDelete = true;
                    }
                    deleteCount++;
                    continue;
                }else {
                    if (hasDelete){
                        shiftRows(sheet,insertIndex+deleteCount,-deleteCount);
                        isLastDelete = false;
                    }else{
                        insertIndex = i;
                    }
                    break;
                }
            }
        }
        if (isLastDelete){
            //最后数据被删除，excel默认行不存在
            insertIndex=0;
        }

        if (insertIndex==0){
            //不需要插入，追加数据
            int oldTotal = sheet.getLastRowNum();
            for (int i=1;i<=nums;i++){
                Row insertRow = sheet.createRow(oldTotal+i);
                rows.add(insertRow);
            }
        }else{
            //插入，后移原数据再插入
            shiftRows(sheet,insertIndex,nums);
            for(int i=0;i<nums;i++){
                Row insertRow = sheet.createRow(insertIndex+i);
                rows.add(insertRow);
            }
        }

        return rows;
    }

    private void shiftRows(Sheet sheet,int insertIndex, int nums){
        sheet.shiftRows(insertIndex, sheet.getLastRowNum(), nums);
        //poi shiftRows bug
        if (sheet instanceof XSSFSheet) {
            XSSFSheet xSSFSheet = (XSSFSheet) sheet;
            for (int r = xSSFSheet.getFirstRowNum(); r < sheet.getLastRowNum() + 1; r++) {
                XSSFRow row = xSSFSheet.getRow(r);
                if (row != null) {
                    long rRef = row.getCTRow().getR();
                    for (Cell cell : row) {
                        String cRef = ((XSSFCell) cell).getCTCell().getR();
                        ((XSSFCell) cell).getCTCell().setR(cRef.replaceAll("[0-9]", "") + rRef);
                    }
                }
            }
        }
    }


}
