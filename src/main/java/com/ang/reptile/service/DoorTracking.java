package com.ang.reptile.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ang.reptile.dto.DoorTrackingQueryData;
import com.ang.reptile.exception.DBException;
import com.ang.reptile.mapper.SmOrderMapper;
import com.ang.reptile.model.DataBus;
import com.ang.reptile.pojo.SmOrder;
import com.ang.reptile.util.*;
import com.ang.reptile.util.excel.obj.XssFExcelObj;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoorTracking {
    @Autowired
    private SmOrderMapper mapper;

    private Logger logger = LoggerFactory.getLogger(DoorTracking.class);

    private String reqUrl = "http://ases.suning.com/ases-web/main/ui/smOrder/queryListFromES.action";
    private static final int pageSize = 10;
    private static final String companyCode = "1100";
    private static final int maxTimeInterval = 17;//苏宁的借口只支持最大查询时间间隔为17天
    private static HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
//    private static final String userId = "W850057074";
    private static HashMap<String, String> cookieMap = new HashMap<>();

/*    static {
        cookieMap.put("loginUserId", userId);
        cookieMap.put("route", "d9eb1272b3b9b51892b23c8a4f386a9a");
        cookieMap.put("userIdKey", "663b6d166c8f4494a2691bc4952137b4");
    }*/

    //查询上门跟踪的数据

    /**
     * 1. 创建excel并且追加标题头
     * 2. 获取数据，循环插入数据到数据库和excel
     * 3. 关闭excel
     * @return
     */
    public DataBus<List<String>> loopDoorTrackingData() {
        JSONObject suNingCookies = SuNingCookieFileReader.getCookies();

        if (suNingCookies.isEmpty()) {
            return DataBus.failure("没有指定苏宁的Cookie！无法获取数据！");
        }

        cookieMap.put("loginUserId", suNingCookies.getString("loginUserId"));
        cookieMap.put("route", suNingCookies.getString("route"));
        cookieMap.put("userIdKey", suNingCookies.getString("userIdKey"));

        XssFExcelObj writer = ToExcelUtil2.openFile();
        ToExcelUtil2.createHeaders(writer);

        DateUtil.TimeConfig config = DateUtil.loadTimeConfig();
        String startTime = config.getStartTime();
        String endTime = config.getEndTime();

        int allDataSize = 0;
        int allDBItemSize = 0;
        int allExcelRowSize = 0;
        List<String> res = new ArrayList<>();
        List<DateUtil.IntervalMap> timeItem = DateUtil.getTimeIntervalItem(startTime, endTime, maxTimeInterval);

        for (DateUtil.IntervalMap intervalMap : timeItem) {
            logger.debug("==========开始查询时间间隔为【" + intervalMap.getStart() + " -> " + intervalMap.getEnd() + "】的数据...==========");
            int currentPage = 0;
            int totalPage = 1;

            while (currentPage <= totalPage) {
                currentPage += 1;
                DataBus<String> dataBus = getDoorTrackingData(intervalMap.getStart(), intervalMap.getEnd(), currentPage);
                if (dataBus.getCode() == DataBus.SUCCESS_CODE) {
                    DataParseUtil.Result result = DataParseUtil.parseDataFromSuNing(dataBus.getData());
                    if (result == null) {
                        break;
                    }

                    //分页用参数
                    currentPage = result.getCurrentPage();
                    totalPage = result.getTotalPage();
                    List<String> datas = result.getDatas();

                    logger.debug("========= 获取{}条记录==========", datas.size());
                    allDataSize += datas.size();
                    try {
                        logger.debug("=============开始插入数据库，一共{}条==============", datas.size());
                        insertSmOrderByString(datas);
                        allDBItemSize += datas.size();
                        logger.info("========= 插入数据库成功，共插入{}条记录==========", datas.size());
                        res.add(dataBus.getData());
                    } catch (Exception e) {
                        logger.error("============== 插入数据库失败！=================");
                        e.printStackTrace();
                        ToExcelUtil2.close(writer);
                        return DataBus.failure();
                    }

                    //excel
                    try {
                        ToExcelUtil2.toExcel(writer, datas, false);
                        logger.info("========= 插入excel成功，共插入{}条记录==========", datas.size());
                    } catch (Exception e) {
                        logger.error("插入excel错误！");
                        e.printStackTrace();
                    }

                    logger.debug("============= 写入excel成功！写入{}条=====================", datas.size());
                    allExcelRowSize += datas.size();
                }
            }

        }

        logger.info("+++++++++++共获取苏宁 " + allDataSize + "条数据+++++++++++++");
        logger.info("+++++++++++共插入数据库 " + allDBItemSize + "条记录+++++++++++++");
        logger.info("+++++++++++共插入excel " + allExcelRowSize + "行数据+++++++++++++");

        DataBus<List<String>> dataBus = DataBus.success();
        dataBus.setData(res);

        //关闭流
        ToExcelUtil2.close(writer);
        return dataBus;
    }

    private DataBus<String> getDoorTrackingData(String start, String end, int currentPage) {
        DoorTrackingQueryData queryData = new DoorTrackingQueryData();
        queryData.setCompanyCode(companyCode);
        queryData.setSrvSaleCountStart(start);
        queryData.setSrvSaleCountEnd(end);
        //todo wd是啥？
        queryData.setWd("0000967074");
        queryData.setPageSize(pageSize);
        queryData.setPage(currentPage);

        FormBody.Builder builder = new FormBody.Builder();
        HashMap<String, String> urlEncodedMap = UrlEncodedUtil.getUrlencodedMap(queryData, DoorTrackingQueryData.class);
        if (urlEncodedMap != null && urlEncodedMap.size() >= 0) {
            for (Map.Entry<String, String> stringObjectEntry : urlEncodedMap.entrySet()) {
                String name = stringObjectEntry.getKey();
                String value = stringObjectEntry.getValue();
                builder.add(name, value);
            }
        }
        FormBody body = builder.build();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                cookieStore.put(httpUrl, list);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                List<Cookie> cookies = cookieStore.get(httpUrl);
                return cookies != null ? cookies : new ArrayList<Cookie>();

            }
        });
        OkHttpClient okHttpClient = clientBuilder.build();
//        RequestBody body = RequestBody.create(URL_ENCODED, builder);
        String cookieValue = cookieMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(";"));
        Request request = new Request.Builder()
                .url(reqUrl)
                .post(body)
                .addHeader("Cookie", cookieValue)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            String resultStr = response.body().string();
            DataBus<String> dataBus = DataBus.success();
            dataBus.setData(resultStr);
            return dataBus;

        } catch (Exception e) {
            logger.error("============= 查询苏宁数据失败！============");
            e.printStackTrace();
            return DataBus.failure();
        }

    }

    private void insertSmOrderByString(List<String> datas) throws Exception {
        for (String data : datas) {
            try {
                SmOrder smOrder = JSON.parseObject(data, SmOrder.class);
                smOrder.setJsonStr(data);
                mapper.insert(smOrder);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("==============插入数据库出错！！！=================");
                throw new DBException(-1, "==============插入数据库出错！！！=================");
            }
        }


    }
}
