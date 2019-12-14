package com.ang.reptile.util.excel.obj;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class XssFExcelObj {
    private Logger logger = LoggerFactory.getLogger(XssFExcelObj.class);
    private String url;
    private XSSFWorkbook xSSFWorkbook;
    private FileOutputStream out;
    private ExcelHeaders headers;

    public XssFExcelObj(XSSFWorkbook xSSFWorkbook, String url) {
        this.xSSFWorkbook = xSSFWorkbook;
        this.url = url;
    }

    /**
     * 获取header对象
     *
     * @return
     */
    public ExcelHeaders headers() {
        headers = new ExcelHeaders(this);
        return headers;
    }

    /**
     * 获取指定指针的工作簿
     *
     * @param sheetIndex
     * @return
     */
    public XSSFSheet getSheet(int sheetIndex) {
        XSSFSheet sheet;
        try {
            sheet = xSSFWorkbook.getSheetAt(sheetIndex); //获取到工作表，因为一个excel可能有多个工作表
        } catch (IllegalArgumentException e) {
            sheetIndex = 0;
            logger.error("当前文档没有工作簿，自动创建第" + sheetIndex + "个工作簿！");
            sheet = xSSFWorkbook.createSheet();
        }

        return sheet;
    }

    /**
     * 在sheet最后创建新的一行并返回
     *
     * @param sheet
     * @return
     */
    public XSSFRow getNextRow(XSSFSheet sheet) {
        //得到最后一行的行号
        int currentNum = sheet.getLastRowNum();
        int addedLast = 0;
        if (currentNum == 0) {
            //有两种可能,一种是真的只有一行(base=0),另一种就是真的没有数据,因为getLastRowNum函数如果没数据也返回0
            //如果有数据，则下一行追加即可
            //如果没有数据，那么就得在第一行加数据，也就是addedLast得等于0
            XSSFRow row = sheet.getRow(0);
            if (row == null || row.equals("")) {//真的一行都没有
                currentNum = -1;
            }
        }
        addedLast = currentNum + 1;
        logger.debug("当前工作簿有{}行【最后行号：{}】，创建新的一行【行号：{}】", (sheet.getLastRowNum() + 1), currentNum, addedLast);
        XSSFRow row = sheet.createRow((short) (addedLast)); //在现有行号后追加数据
        return row;
    }


    private Map<?, ?> aliasMap(@NotNull Map<?, ?> map) {
        Map<String, String> aliasHeaders = headers.getHeaders();
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> stringStringEntry : aliasHeaders.entrySet()) {
            String key = stringStringEntry.getKey();
            linkedHashMap.put(aliasHeaders.get(key), map.get(key).toString());
        }
        return linkedHashMap;
    }

    /**
     * 在最后一行后面追加数据
     *
     * @param map
     */
    public void writeRow(Map<?, ?> map, int sheetIndex) {
        Map<?, ?> aliasMap = aliasMap(map);
        writeRow(aliasMap.values(), sheetIndex);
    }

    /**
     * 在最后一行后面追加数据
     *
     * @param iterable
     */
    public void writeRow(Iterable iterable, int sheetIndex) {
        XSSFSheet sheet = getSheet(sheetIndex);
        String sheetName = sheet.getSheetName();
        logger.info("当前在【{}】号工作簿处理数据", sheetName);
        XSSFRow row = getNextRow(sheet);
        logger.debug("增加数据到行【行号： " + sheet.getLastRowNum() + ", 工作簿： " + sheetName + "】");
        flushRowData(row, iterable);
        logger.info("目前文件的工作簿【" + sheetName + "】有【 " + sheet.getPhysicalNumberOfRows() + "/ " + (sheet.getLastRowNum() + 1) + "】行， 最后一行有【" + row.getPhysicalNumberOfCells() + " / " + row.getLastCellNum() + "】列");
    }

    public void writeRows(Iterable<Object> iterables, int sheetIndex) {
        XSSFSheet sheet = getSheet(sheetIndex);
        String sheetName = sheet.getSheetName();
        logger.info("当前在【{}】号工作簿处理数据", sheetName);
        Iterator iterator = iterables.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            XSSFRow row = getNextRow(sheet);
            logger.debug("增加数据到行【行号： " + sheet.getLastRowNum() + ", 工作簿： " + sheetName + "】");
            if (obj instanceof Iterable) {
                setDataToRow(row, (Iterable) obj);
            } else if (obj instanceof Map) {
                Map<?, ?> data = aliasMap((Map<?, ?>) obj);
                setDataToRow(row, data.values());
            }
        }
        logger.info("目前文件的工作簿【{}】有【{} / {}】行",
                sheetName, sheet.getPhysicalNumberOfRows(), (sheet.getLastRowNum() + 1));

        closeOutPutStreamAndFlushData();
    }

    /**
     * 将数据设置到指定行 并 输出
     *
     * @param row
     * @param iterable
     */
    private void flushRowData(XSSFRow row, Iterable iterable) {
        setDataToRow(row, iterable);
        logger.debug("写入标题头,并关闭输出流...");
        closeOutPutStreamAndFlushData();
    }

    /**
     * 关闭输出流 和 输入流
     */
    public void close() {
        try {
            if (out != null) {
                out.close();
                logger.info("已关闭输出流");
            }
            if (xSSFWorkbook != null) {
                xSSFWorkbook.close();
                logger.info("已关闭XSSFWorkbook");
            }
            out = null;
            xSSFWorkbook = null;
            //两个互相引用的对象相互释放对方的指针
            headers.setExcelObj(null);
            headers = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void flushHeader(int sheetIndex) {
        logger.info("创建标题头");
        XSSFSheet sheet = getSheet(sheetIndex);
        XSSFRow row = getNextRow(sheet);
        flushRowData(row, headers.values());
    }

    /**
     * 设置数据到行
     *
     * @param row
     * @param iterable
     */
    private void setDataToRow(XSSFRow row, Iterable iterable) {
        Iterator<Object> iterator = iterable.iterator();
        int index = 0;
        while (iterator.hasNext()) {//iterable目前只支持list 、array类型
            Object o = iterator.next();
            row.createCell(index).setCellValue(o.toString());
            index++;
        }
    }

    /**
     * 写出数据,自动关闭流
     */
    private void closeOutPutStreamAndFlushData() {
        try {
            out = new FileOutputStream(this.url);
            out.flush();//这段貌似不需要
            xSSFWorkbook.write(out);
            out.close();
            logger.info("已关闭输出流");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
