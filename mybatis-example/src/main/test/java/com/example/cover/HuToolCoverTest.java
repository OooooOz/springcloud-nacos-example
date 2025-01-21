package com.example.cover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.example.cover.po.JobPO;
import com.example.cover.po.UserInfoPO;
import com.example.cover.vo.UserInfoVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

public class HuToolCoverTest {

    @Test
    public void po2vo() {
        UserInfoPO po = UserInfoPO.builder().userId(1L).userName("zhangsan").age("18").build();
        System.out.println(JSON.toJSONString(po)); // {"age":"18","userId":1,"userName":"zhangsan"}

        UserInfoVO vo = BeanUtil.copyProperties(po, UserInfoVO.class);
        System.out.println(JSON.toJSONString(vo)); // {"age":"18","userId":1}

        UserInfoVO vo1 = BeanUtil.copyProperties(po, UserInfoVO.class, "age");
        System.out.println(JSON.toJSONString(vo1)); // {"userId":1}

        UserInfoVO vo2 = UserInfoVO.builder().build();
        HashMap<String, String> map = Maps.newHashMap();
        map.put("userName", "name");
        CopyOptions copyOptions = CopyOptions.create().setFieldMapping(map);
        BeanUtil.copyProperties(po, vo2, copyOptions);
        System.out.println(JSON.toJSONString(vo2)); // {"age":"18","name":"zhangsan","userId":1}
    }

    @Test
    public void pos2vos() {
        UserInfoPO po = UserInfoPO.builder().userId(1L).userName("zhangsan").age("18").build();
        ArrayList<UserInfoPO> list = Lists.newArrayList(po);
        System.out.println(JSON.toJSONString(list)); // [{"age":"18","userId":1,"userName":"zhangsan"}]

        List<UserInfoVO> vos = BeanUtil.copyToList(list, UserInfoVO.class);
        System.out.println(JSON.toJSONString(vos)); // [{"age":"18","userId":1}]

        CopyOptions copyOptions = CopyOptions.create().setIgnoreProperties("age");
        List<UserInfoVO> vos1 = BeanUtil.copyToList(list, UserInfoVO.class, copyOptions);
        System.out.println(JSON.toJSONString(vos1)); // [{"userId":1}]

        HashMap<String, String> map = Maps.newHashMap();
        map.put("userName", "name");
        CopyOptions copyOption1 = CopyOptions.create().setFieldMapping(map);
        List<UserInfoVO> vos2 = BeanUtil.copyToList(list, UserInfoVO.class, copyOption1);
        System.out.println(JSON.toJSONString(vos2)); // [{"age":"18","name":"zhangsan","userId":1}]
    }


    @Test
    public void po2map() {
        UserInfoPO po = UserInfoPO.builder().userId(1L).userName("zhangsan").age("18").build();
        System.out.println(JSON.toJSONString(po)); // {"age":"18","userId":1,"userName":"zhangsan"}

        Map<String, Object> map = BeanUtil.beanToMap(po);
        System.out.println(JSON.toJSONString(map)); // {"age":"18","userId":1,"userName":"zhangsan"}

        Map<String, Object> map1 = BeanUtil.beanToMap(po, true, false);
        System.out.println(JSON.toJSONString(map1)); // {"user_id":1,"user_name":"zhangsan","age":"18"}
    }

    @Test
    public void po2voNested() {
        JobPO jobPO = JobPO.builder().jobName("java").jobId(1L).build();
        UserInfoPO po = UserInfoPO.builder().userId(1L).userName("lisi").job(jobPO).build();
        System.out.println(JSON.toJSONString(po)); // {"job":{"jobId":1,"jobName":"java"},"userId":1,"userName":"lisi"}

        UserInfoVO vo = BeanUtil.copyProperties(po, UserInfoVO.class);
        System.out.println(JSON.toJSONString(vo)); // {"job":{"jobId":1},"userId":1}

        UserInfoVO vo1 = BeanUtil.copyProperties(po, UserInfoVO.class, "age");
        System.out.println(JSON.toJSONString(vo1)); // {"job":{"jobId":1},"userId":1}

        UserInfoVO vo2 = UserInfoVO.builder().build();
        HashMap<String, String> map = Maps.newHashMap();
        map.put("userName", "name");
        map.put("jobName", "jName");
        CopyOptions copyOptions = CopyOptions.create().setFieldMapping(map);
        BeanUtil.copyProperties(po, vo2, copyOptions);
        System.out.println(JSON.toJSONString(vo2)); // {"job":{"jobId":1},"name":"lisi","userId":1}

        BeanUtil.copyProperties(po.getJob(), vo2.getJob(), copyOptions);
        System.out.println(JSON.toJSONString(vo2)); // {"job":{"jName":"java","jobId":1},"name":"lisi","userId":1}
    }
}
