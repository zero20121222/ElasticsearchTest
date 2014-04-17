package com.elasticsearch.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
/**
 * 向ES添加索引对象
 */
public class IndexTest {

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
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

        ObjectMapper mapper = new ObjectMapper();


        String json = null;
        try {
            json = mapper.writeValueAsString(new LogModel());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //在这里创建我们要索引的对象
        IndexResponse response = client.prepareIndex("twitter", "tweet")
                //必须为对象单独指定ID
                .setId("1")
                .setSource(json)
                .execute()
                .actionGet();

        //多次index这个版本号会变
        System.out.println("response.version():"+response.getVersion());
        client.close();
    }
}