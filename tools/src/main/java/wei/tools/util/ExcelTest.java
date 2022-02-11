package wei.tools.util;

import cn.hutool.core.exceptions.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import wei.tools.entity.WenCaiQueryEntity;

import java.io.*;

/**
 * @Author: weiyanan
 * @Date: 2022/2/11 11:01
 */
public class ExcelTest {

    public static void createExcel(String excelName) throws Exception {
        //创建工作簿
        XSSFWorkbook wb = new XSSFWorkbook();
        //创建一个sheet
        XSSFSheet sheet = wb.createSheet();
        // 创建单元格样式
        XSSFCellStyle style =  wb.createCellStyle();
        //创建字体
        XSSFFont redFont = wb.createFont();
        //设置字体颜色
        redFont.setColor(Font.COLOR_RED);
        //设置字体大小
        redFont.setFontHeightInPoints((short) 10);
        style.setFillForegroundColor((short)4); //设置要添加表格背景颜色
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND); //solid 填充
        style.setBorderBottom(BorderStyle.THIN); //底边框加黑
        style.setBorderLeft(BorderStyle.THIN);  //左边框加黑
        style.setBorderRight(BorderStyle.THIN); // 有边框加黑
        style.setBorderTop(BorderStyle.THIN); //上边框加黑
        style.setFont(redFont);
        //为单元格添加背景样式
        for (int i = 0; i < 6; i++) { //需要6行表格
            Row row =	sheet.createRow(i); //创建行
            for (int j = 0; j < 6; j++) {//需要6列
                row.createCell(j).setCellStyle(style);
            }
        }


        //合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));//合并单元格，cellRangAddress四个参数，第一个起始行，第二终止行，第三个起始列，第四个终止列
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 5));

        //填入数据
        XSSFRow row = sheet.getRow(0); //获取第一行
        row.getCell(1).setCellValue("2018期末考试"); //在第一行中创建一个单元格并赋值
        //设置字体样式为字体低端有下划线
        XSSFFont line = wb.createFont();
        line.setColor(Font.COLOR_NORMAL);
        //文字下划线
        line.setUnderline(FontUnderline.SINGLE);
        XSSFCellStyle lineStyle = wb.createCellStyle();
        lineStyle.setFont(line);
        row.getCell(1).setCellStyle(lineStyle);
        //设置超链接
        XSSFCreationHelper createHelper = new XSSFCreationHelper((XSSFWorkbook) wb);
        XSSFHyperlink  baiduLink = createHelper.createHyperlink(HyperlinkType.FILE);
        baiduLink.setAddress("http://www.baidu.com/baidu?wd="+row.getCell(1).getStringCellValue());
        row.getCell(1).setHyperlink(baiduLink);


        XSSFRow row1 = sheet.getRow(1); //获取第二行，为每一列添加字段
        row1.getCell(1).setCellValue("语文");
        row1.getCell(2).setCellValue("数学");
        row1.getCell(3).setCellValue("英语");
        row1.getCell(4).setCellValue("物理");
        row1.getCell(5).setCellValue("化学");
        XSSFRow row2 = sheet.getRow(2); //获取第三行
        row2.getCell(0).setCellValue("张三");
        XSSFRow row3 = sheet.getRow(3); //获取第四行
        row3.getCell(0).setCellValue("张三");
        XSSFRow row4 = sheet.getRow(4); //获取第五行
        row4.getCell(0).setCellValue("张三");
        XSSFRow row5 = sheet.getRow(5); //获取第五行
        row5.getCell(0).setCellValue("张三");

        //创建第二个工作区
        XSSFSheet sheet1 = wb.createSheet("第二个工作区");
        for (int i = 0; i < 6; i++) { //需要6行表格
            Row sheet1_row =	sheet1.createRow(i); //创建行
            for (int j = 0; j < 6; j++) {//需要6列
                sheet1_row.createCell(j);//创建列
            }
        }
        XSSFRow sheet1_row_xxs = sheet1.getRow(0); //获取第一个工作间的第一行
        sheet1_row_xxs.getCell(0).setCellValue("test"); //在第一行中创建一个单元格并赋值
        //将数据写入文件
        FileOutputStream out = new FileOutputStream(excelName);
        wb.write(out);
    }

    public static void readExcel(String excelName) throws IOException, IOException {
        //将文件读入
        InputStream in  = new FileInputStream(new File(excelName));
        //创建工作簿
        XSSFWorkbook wb = new XSSFWorkbook(in);
        //读取第一个sheet
        Sheet sheet = wb.getSheetAt(0);
        //获取第二行
        //循环读取科目

        //插入行
        sheet.shiftRows(4, sheet.getLastRowNum(), 1); //shifts rows between row 5 (index 4) and last row one row down

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
//        in.close();


        Row insertRow = sheet.createRow(4);
        short height = 650;
        insertRow.setHeight(height);
        Cell cell = insertRow.createCell(1);
        cell.setCellStyle(sheet.getColumnStyle(1));
        cell.setCellValue("4500");

        FileOutputStream out = new FileOutputStream("D://test.xlsx");
        wb.write(out);
    }

    public static void readReviewFile(WenCaiQueryEntity entity) throws IOException {

        String dateStr = "2022-02-08";
        String[] dss = dateStr.split("-");
        String dateExStr = dss[1]+"."+dss[2];

        String fileName = "C:\\Users\\homay\\iCloudDrive\\stock\\2022\\2022复盘记录.xlsx";
        //将文件读入
        InputStream in  = new FileInputStream(new File(fileName));
        XSSFWorkbook wb = new XSSFWorkbook(in);

        FormulaEvaluator formulaEvaluator = new XSSFFormulaEvaluator(wb);

        Sheet sheet = wb.getSheet("情绪周期表");
        //先找要插入的位置
        int insertIndex = 0;
        for (int i=2;i<sheet.getLastRowNum();i++){
            //取第一列 比较日期
            Cell cell = sheet.getRow(i).getCell(0);
            if(cell==null || StringUtils.isBlank(cell.getStringCellValue())){
                continue;
            }
            if (StringUtils.compare(dateExStr,cell.getStringCellValue())<0){
                insertIndex = i;
                break;
            }
        }

        Row insertRow ;
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

        //写入数据
        short height = 650;
        insertRow.setHeight(height);
        //
        Cell cell = insertRow.createCell(1);
        cell.setCellStyle(sheet.getColumnStyle(1));
        cell.setCellValue("4500");

        FileOutputStream out = new FileOutputStream("D://test.xlsx");
        wb.write(out);

    }

    public static void main(String[] args) throws Exception {
//        ExcelTest.createExcel("D://test.xlsx");
//        ExcelTest.readExcel("D://test.xlsx");
        WenCaiQueryEntity entity = new WenCaiQueryEntity();
        entity.setBreakCount(50);
        entity.setDropLimitCount(10);
        entity.setUpLimitCount(100);
        entity.setDropCount(1000);
        entity.setUpCount(3500);
        entity.setSecondCount(9);
        entity.setThirdStr("新华联、华扬联众、普邦股份、金财互联");
        entity.setThirdCount(4);
        entity.setMoreCount(6);
        entity.setMoreStr("保利联合7、恒宝股份5、冀东装备5、重庆建工4、元隆雅图4、浙江建投4");
        ExcelTest.readReviewFile(entity);
    }

}
