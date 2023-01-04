package com.example.spi;

import com.example.spi.service.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class PushController {

    @Autowired
    private PushServiceFactory pushServiceFactory;

    @GetMapping("spi/push/{name}")
    public String add(@PathVariable String name) {
        PushService pushService = pushServiceFactory.getPushService(name);
        pushService.push();
        return "success";
    }
}
