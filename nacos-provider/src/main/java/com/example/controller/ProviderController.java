package com.example.controller;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/provider")
@RestController
@RefreshScope
class ProviderController {

    @Value("${name}")
    private String name;

    @NacosInjected
    private NamingService namingService;

    @Autowired
    private NacosServiceManager nacosServiceManager;

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @GetMapping("/getValue")
    public String getValue() {
        System.out.println("=========================8080=================" + System.currentTimeMillis());
        return "8080 ==> [name: " + name + "]";
    }


    @RequestMapping(value = "/getService", method = GET)
    public List<Instance> get(@RequestParam String serviceName) throws NacosException {
        // namingService注入为null,采用nacosServiceManager获取namingService注入为null
        namingService = nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties());
        return namingService.getAllInstances("walmart-zt");
    }
}
