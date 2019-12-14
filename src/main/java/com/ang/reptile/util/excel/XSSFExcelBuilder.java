package com.ang.reptile.util.excel;

import com.imooc.demo.utils.excel.obj.XssFExcelObj;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class XSSFExcelBuilder {
    private static Logger logger = LoggerFactory.getLogger(XSSFExcelBuilder.class);
    private static String FILENAME_EXTENSION = ".xlsx";

    /**
     * 获取实例： 加载文件，如果不存在则创建
     *
     * @param path
     * @return
     */
    public static XssFExcelObj load(String path) throws Exception{
        try {
            logger.debug("open file【{}】", path);

            FileInputStream fileInputStream = new FileInputStream(path);
            XSSFWorkbook wb;
            if (fileInputStream.getChannel().size() > 0) {
                wb = new XSSFWorkbook(fileInputStream);
            } else {
                logger.debug("this file has no content and workbook, creating as default...");

                wb = new XSSFWorkbook();
            }
            return new XssFExcelObj(wb, path);
        } catch (FileNotFoundException e) {
            logger.debug("file does not exists! creating as default...");
            try {
                return createXssFExcelWithPath(path);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public static XssFExcelObj createXssFExcelWithPath(String path) throws Exception {
        String[] directoryArr = path.replaceAll("\\\\", "/").split("/");
        if (directoryArr.length == 0) {
            throw new Exception("file path invalid！");
        }
        logger.debug("get file path: {}", Arrays.toString(directoryArr));
        directoryArr[directoryArr.length - 1] = "";
        String directory = String.join("/", directoryArr);
        logger.debug("get directory: {}", directory);

        File file = new File(directory);
        if (!file.exists()) {
            file.mkdir();
            logger.debug("file directory【{}】 created...", directory);
        }

        return new XssFExcelObj(new XSSFWorkbook(), path);
    }
}