package com.elasticsearch.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 瞎编的一个模型，跟日志基本没有关系
 */
@ToString
public class LogModel {
    //主ID
    @Setter
    @Getter
    private long id;

    //次ID
    @Setter
    @Getter
    private int subId;

    /**
     * 系统名称
     */
    @Setter
    @Getter
    private String systemName;

    @Setter
    @Getter
    private String host;

    //日志描述
    @Setter
    @Getter
    private String desc;

    @Setter
    @Getter
    private List<Integer> catIds;

    public LogModel(){
        Random random = new Random();
        this.id = Math.abs(random.nextLong());
        int subId = Math.abs(random.nextInt());
        this.subId = subId;
        List<Integer> list = new ArrayList<Integer>(5);
        for(int i=0;i<5;i++){
            list.add(Math.abs(random.nextInt()));
        }
        this.catIds = list;
        this.systemName = subId%1 == 0?"oa":"cms";
        this.host = subId%1 == 0?"10.0.0.1":"10.2.0.1";
        this.desc = "中文" + UUID.randomUUID().toString();
    }

    public LogModel(long id,int subId,String sysName,String host,String desc,List<Integer> catIds){
        this.id = id;
        this.subId = subId;
        this.systemName = sysName;
        this.host = host;
        this.desc = desc;
        this.catIds = catIds;
    }
}