package com.ang.reptile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String getDateStr(Date date) {
        return simpleDateFormat.format(date);
    }

    public static List<IntervalMap> getTimeIntervalItem(String start, String end, Integer timeInterval) {
        List<IntervalMap> list = new ArrayList<>();
        if (start == null || end == null || timeInterval == null) {
            return null;
        }

        try {
            Date startDate = simpleDateFormat.parse(start);
            Date endDate = simpleDateFormat.parse(end);
            Calendar calendar = Calendar.getInstance();

            while (startDate.before(endDate)) {
                IntervalMap map = new IntervalMap();
                calendar.setTime(startDate);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + (timeInterval - 1));
                Date temp = calendar.getTime();
                if (temp.after(endDate)) {//结束时间点超过17天，则直接写入结束时间点
                    map.setStart(simpleDateFormat.format(startDate));
                    map.setEnd(simpleDateFormat.format(endDate));
                    list.add(map);
                    break;
                } else {
                    //缓存长度为17天的起始时间点
                    map.setStart(simpleDateFormat.format(startDate));
                    map.setEnd(simpleDateFormat.format(temp));

                    //下一次的起始时间为第timeInterval+1天
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
                    startDate = calendar.getTime();
                    list.add(map);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;

    }

    public static class IntervalMap{
        private String start;
        private String end;

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }
/*    public static void main(String[] args) {
        List<IntervalMap> list = getTimeIntervalItem("2019-10-15", "2019-11-20", 17);
        for (IntervalMap intervalMap : list) {
            System.out.println(intervalMap.getStart() + " -> " + intervalMap.getEnd());

        }
    }*/
}

