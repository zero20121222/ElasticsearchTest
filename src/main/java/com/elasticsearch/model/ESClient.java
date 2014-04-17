package com.elasticsearch.model;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Desc:elasticsearch搜索引擎测试
 * Mail:v@terminus.io
 * Created by Michael Zhao
 * Date:2014-04-16.
 */
@Component
public class ESClient {
    private final TransportClient client;

    @Autowired
    public ESClient(@Value("#{app.searchHost}") final String host, @Value("#{app.searchPort}") final Integer port,
                    @Value("#{app.searchName}") final String clusterName) {
        Settings settings = ImmutableSettings.settingsBuilder()
                //指定集群名称
                .put("cluster.name", clusterName)
                //探测集群中机器状态
                .put("client.transport.sniff", true).build();

        /*
         * 创建客户端，所有的操作都由客户端开始，这个就好像是JDBC的Connection对象
         * 用完记得要关闭
         */
        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(host , port));
    }

    @After
    public void closeESClient() {
        client.close();
    }
}
