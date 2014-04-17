package com.elasticsearch.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.IOException;

public class GetTest {
    public static void main(String[] argv){
        Settings settings = ImmutableSettings.settingsBuilder()
                //指定集群名称  
                .put("cluster.name", "elasticsearch")
                        //探测集群中机器状态
                .put("client.transport.sniff", true).build();  
        /* 
         * 创建客户端，所有的操作都由客户端开始，这个就好像是JDBC的Connection对象 
         * 用完记得要关闭 
         */
        Client client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

        //在这里创建我们要索引的对象
        GetResponse response = client.prepareGet("twitter", "tweet", "1").execute().actionGet();
        System.out.println("response.getId():"+response.getId());

        //通过
        try {
            Long start = System.currentTimeMillis();

            LogModel model = new ObjectMapper().readValue(response.getSourceAsBytes() , LogModel.class);
            System.out.println("index-Header:host->"+model.getHost()+", desc->"+model.getDesc());

            Long end = System.currentTimeMillis();
            System.out.println(end-start);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 