package com.example.cover;

import com.alibaba.fastjson.JSON;
import com.example.cover.mapper.UserInfoAssembler;
import com.example.cover.mapper.UserInfoCover;
import com.example.cover.po.JobPO;
import com.example.cover.po.UserInfoPO;
import com.example.cover.vo.UserInfoVO;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MapStructCoverTest {

    @Test
    public void po2voNested() {
        JobPO jobPO = JobPO.builder().jobName("java").jobId(1L).build();
        UserInfoPO po = UserInfoPO.builder().userId(1L).userName("lisi").job(jobPO).build();
        System.out.println(JSON.toJSONString(po));  // {"job":{"jobId":1,"jobName":"java"},"userId":1,"userName":"lisi"}

        UserInfoVO vo = UserInfoCover.INSTANCE.toConvertUserInfoVo(po);
        System.out.println(JSON.toJSONString(vo));  // {"job":{"jName":"java","jobId":1},"name":"lisi","userId":1}
    }

    @Test
    public void pos2vos() {
        UserInfoPO po = UserInfoPO.builder().userId(1L).userName("zhangsan").age("18").build();
        ArrayList<UserInfoPO> list = Lists.newArrayList(po);
        System.out.println(JSON.toJSONString(list)); // [{"age":"18","userId":1,"userName":"zhangsan"}]

        List<UserInfoVO> vos = UserInfoCover.INSTANCE.toConvertUserInfoVos(list);
        System.out.println(JSON.toJSONString(vos)); // [{"name":"zhangsan","userId":1}]
    }

    @Test
    public void po2voAfter() {
        JobPO jobPO = JobPO.builder().jobName("java").jobId(1L).build();
        UserInfoPO po = UserInfoPO.builder().userId(1L).userName("lisi").job(jobPO).build();
        System.out.println(JSON.toJSONString(po));  // {"job":{"jobId":1,"jobName":"java"},"userId":1,"userName":"lisi"}

        UserInfoVO vo = UserInfoAssembler.INSTANCE.map(po);
        System.out.println(JSON.toJSONString(vo));  // {"job":{"jName":"java","jobId":1},"name":"LISI","userId":1}
    }
}
