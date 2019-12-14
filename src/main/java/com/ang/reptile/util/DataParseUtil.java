package com.ang.reptile.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataParseUtil {

    public static Result parseDataFromSuNing(String resultStr) {
        List<String> list = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        if (jsonObject != null) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null && !data.isEmpty()) {
                JSONArray datas = data.getJSONArray("datas");
                if (data != null && !datas.isEmpty()) {
                    for (Object o : datas) {
                        list.add(o.toString());
                    }
                }
            }

            return new Result(data.getInteger("currentPage"), data.getInteger("totalPages"), list);

        }

        return null;

    }

    public static class Result {
        private int currentPage;
        private int totalPage;
        private List<String> datas;

        public Result(int currentPage, int totalPage, List<String> datas) {
            this.currentPage = currentPage;
            this.totalPage = totalPage;
            this.datas = datas;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public List<String> getDatas() {
            return datas;
        }

        public void setDatas(List<String> datas) {
            this.datas = datas;
        }
    }
}
