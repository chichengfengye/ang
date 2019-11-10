package com.ang.reptile.controller;

import com.ang.reptile.model.DataBus;
import com.ang.reptile.model.Message;
import com.ang.reptile.service.DoorTracking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {
    @Autowired
    private DoorTracking doorTracking;

    @RequestMapping("/getData")
    public Message getData() {
        try {
            DataBus<List<String>> dataBus = doorTracking.loopDoorTrackingData();
            if (dataBus.getCode() == DataBus.SUCCESS_CODE) {
                return Message.success(dataBus.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return Message.failure();
    }
}
