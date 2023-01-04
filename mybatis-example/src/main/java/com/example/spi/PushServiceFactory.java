package com.example.spi;

import com.example.spi.service.PushService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

@Component
public class PushServiceFactory {

    private final Map<String, PushService> map = new HashMap();

    /**
     * SPI机制：服务端有标准接口，但没有统一的实现时，由业务方提供具体实现
     * PushService： 公开的标准service接口
     * DefaultPushServiceImpl：spi-service-provider 提供的实现
     * ServiceLoader：在运行时发现并加载service provider
     * <p>
     * 缺点：
     * 不能按需加载，只能通过遍历的方式全部获取
     * 获取某个实现类的方式不够灵活，只能通过迭代器的形式获取。这两点可以参考 Dubbo SPI 实现方式进行业务优化
     */
    @PostConstruct
    public void init() {
        ServiceLoader<PushService> load = ServiceLoader.load(PushService.class);
        Iterator<PushService> iterator = load.iterator();
        while (iterator.hasNext()) {
            PushService pushService = iterator.next();
            map.put(pushService.getName(), pushService);
            pushService.push();
        }
    }


    public PushService getPushService(String beanName) {
        PushService pushService = map.get(beanName);
        return pushService;
    }
//
//    public static void main(String[] args) {
//        ServiceLoader<PushService> load = ServiceLoader.load(PushService.class);
//        Iterator<PushService> iterator = load.iterator();
//        while (iterator.hasNext()) {
//            PushService pushService = iterator.next();
//            pushService.push();
//        }
//    }
}
