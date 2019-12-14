package com.ang.reptile.util.excel;

import com.alibaba.fastjson.JSONObject;
import com.imooc.demo.utils.excel.obj.XssFExcelObj;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        addContentAndArrayData();
        System.out.println("==================================");
        addContentAndJSONData();
        System.out.println("==================================");
        addContentAndJSONHeadersAndJSONData();
    }

    public static void addContentAndJSONHeadersAndJSONData() {
        //1. 加载（创建）文件
        try {
            XssFExcelObj xssFExcelObj = XSSFExcelBuilder.load("G:\\excel3\\test3.xlsx");
            if (xssFExcelObj != null) {
                //2.添加标题头
                JSONObject headers = new JSONObject();
                headers.put("name", "名称2");
                headers.put("age", "年龄2");
                headers.put("height", "身高2");

                xssFExcelObj.headers().addHeaders(headers).finish(0);//一次性写入header测试成功

                //3.追加数据
                List<Object> datas = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    JSONObject data = new JSONObject();
                    data.put("name", "靳丰_" + i);
                    data.put("age", "18_" + i);
                    data.put("height", "180_" + i);
                    datas.add(data);
                }

                xssFExcelObj.writeRows(datas, 0);//一次性写入测试成功

                //4.关闭xssFExcelObj
                xssFExcelObj.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. 一次性加入全部的header
     * 2. 数据是JSONObject的格式
     * 3. 一行行写入以及一次多行写入的方式
     */
    public static void addContentAndJSONData() {
        //1. 加载（创建）文件
        try {
            XssFExcelObj xssFExcelObj = XSSFExcelBuilder.load("G:\\test2.xlsx");
            if (xssFExcelObj != null) {
                //2.添加标题头
                LinkedHashMap<String, String> headers = new LinkedHashMap();
                headers.put("name", "名称2");
                headers.put("age", "年龄2");
                headers.put("height", "身高2");

                xssFExcelObj.headers().addHeaders(headers).finish(0);//一次性写入header测试成功

                //3.追加数据
                List<Object> datas = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    JSONObject data = new JSONObject();
                    data.put("name", "靳丰_" + i);
                    data.put("age", "18_" + i);
                    data.put("height", "180_" + i);
                    datas.add(data);
                    xssFExcelObj.writeRow(data, 0);//逐行写入测试成功
                }

                xssFExcelObj.writeRows(datas, 0);//一次性写入测试成功

                //4.关闭xssFExcelObj
                xssFExcelObj.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. 一个个add的方式设置header
     * 2. 数据是list的格式
     * 3. 一行行写入以及一次多行写入的方式
     */
    public static void addContentAndArrayData() {
        //1. 加载（创建）文件
        try {
            XssFExcelObj xssFExcelObj = XSSFExcelBuilder.load("G:\\test1.xlsx");
//        JSONObject headers = new JSONObject();
            if (xssFExcelObj != null) {
                //2.添加标题头
                xssFExcelObj.headers().addHeader("name", "名称")
                        .addHeader("age", "年龄")
                        .addHeader("height", "身高")
                        .finish(0);

                //3.追加数据
                List<Object> datas = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    List<String> data = new ArrayList<>();
                    data.add("靳丰_" + i);
                    data.add("18_" + i);
                    data.add("180_" + i);
                    datas.add(data);
                    xssFExcelObj.writeRow(data, 0);//逐行添加测试通过
                }

                xssFExcelObj.writeRows(datas, 0);//一次性添加测试通过

                //4.关闭xssFExcelObj
                xssFExcelObj.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
