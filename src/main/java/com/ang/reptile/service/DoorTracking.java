package com.ang.reptile.service;

import com.ang.reptile.dbmodel.DoorTrackingModel;
import com.ang.reptile.dto.DoorTrackingQueryData;
import com.ang.reptile.exception.DBException;
import com.ang.reptile.mapper.DoorTrackingMapper;
import com.ang.reptile.model.DataBus;
import com.ang.reptile.util.DateUtil;
import com.ang.reptile.util.UrlEncodedUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoorTracking {
    @Autowired
    private DoorTrackingMapper mapper;

    Logger logger = LoggerFactory.getLogger(DoorTracking.class);

    private static final int pageSize = 10;
    private static final String companyCode = "1100";
    private static final int maxTimeInterval = 17;//苏宁的借口只支持最大查询时间间隔为17天
    private static HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
    private static final String userId = "W850057074";
    static HashMap<String, String> cookieMap = new HashMap<>();

    static {
        cookieMap.put("loginUserId", userId);
        cookieMap.put("rememberUserNameKey", userId);
        cookieMap.put("route", "902a8fe628dd56177b3db648f4afacf5");
        cookieMap.put("userIdKey", "e2c88cfdda38498e84cf4402173e9cb0");
        cookieMap.put("CSRF-TOKEN", "kr538r3y3czhsknxf8nmcydx6ucnwuq7dnj3");

/*
        List<Cookie> cookies = new ArrayList<>();
        for (Map.Entry<String, String> stringStringEntry : cookieMap.entrySet()) {
            Cookie cookie = new Cookie.Builder()
                    .domain("ases.suning.com")
                    .name(stringStringEntry.getKey())
                    .value(stringStringEntry.getValue())
                    .build();
            cookies.add(cookie);
        }


        cookieStore.put(new HttpUrl.Builder().host("ases.suning.com").build(), cookies);
*/


    }

    //查询上门跟踪的数据
    public DataBus<List<String>> loopDoorTrackingData(){
        List<String> res = new ArrayList<>();
        List<DateUtil.IntervalMap> timeItem = DateUtil.getTimeIntervalItem("2019-10-15", "2019-11-20", maxTimeInterval);
        for (DateUtil.IntervalMap intervalMap : timeItem) {
            logger.info("==========开始查询时间间隔为【" + intervalMap.getStart() + " -> " + intervalMap.getEnd() + "】的数据...==========");
            DataBus<String> dataBus = getDoorTrackingData(intervalMap.getStart(), intervalMap.getEnd());
            if (dataBus.getCode() == DataBus.SUCCESS_CODE) {
                res.add(dataBus.getData());
            }
        }

        DataBus<List<String>> dataBus = DataBus.success();
        dataBus.setData(res);
        return dataBus;
    }

    public DataBus<String> getDoorTrackingData(String start, String end) {
        String url = "http://ases.suning.com/ases-web/main/ui/smOrder/queryListFromES.action";
        DoorTrackingQueryData queryData = new DoorTrackingQueryData();
        queryData.setCompanyCode(companyCode);
        queryData.setSrvSaleCountStart(start);
        queryData.setSrvSaleCountEnd(end);
        queryData.setWd("0000967074");
        queryData.setPageSize(pageSize);
        queryData.setPage(1);

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
                .url(url)
                .post(body)
                .addHeader("Cookie", cookieValue)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            String resultStr = response.body().string();

            //如数据库
            insertIntoDB(resultStr);
            DataBus<String> dataBus = DataBus.success();
            dataBus.setData(resultStr);
            return dataBus;

        } catch (Exception e) {

            e.printStackTrace();
            return DataBus.failure();
        }

    }


    private void insertIntoDB(String jsonStr) throws Exception {
        try {
            //todo mybatis operation
            DoorTrackingModel model = new DoorTrackingModel();
            model.setJsonstr(jsonStr);
            mapper.insertModel(model);

            //logger info
            logger.info("==============插入数据库成功["+jsonStr+"]！！！=================");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("==============插入数据库出错！！！=================");
            throw new DBException(-1, "==============插入数据库出错！！！=================");
        }

    }
}
