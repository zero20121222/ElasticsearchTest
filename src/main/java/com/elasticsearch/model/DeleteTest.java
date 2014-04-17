package com.elasticsearch.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.IOException;

public class DeleteTest {
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
        // 这里可以同时连接集群的服务器,可以多个,并且连接服务是可访问的
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

        //在这里创建我们要索引的对象
        DeleteResponse response = client.prepareDelete("twitter", "tweet", "1").execute().actionGet();

        System.out.println("index-Id:"+response.getId());
    }
}  