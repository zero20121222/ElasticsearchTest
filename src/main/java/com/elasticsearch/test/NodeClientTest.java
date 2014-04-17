package com.elasticsearch.test;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * Desc:Node实现elsaticsearch集群
 * Mail:v@terminus.io
 * Created by Michael Zhao
 * Date:2014-04-17.
 */
public class NodeClientTest {

    public static void main(String arg[]){
        //可以通过两种方式来连接到elasticsearch（简称es）集群，
        //第一种是通过在你的程序中创建一个嵌入es节点（Node），
        //使之成为es集群的一部分，然后通过这个节点来与es集群通信
        Node node = new NodeBuilder().settings(new Settings.Builder() {
            @Override
            public Settings build() {
                return ImmutableSettings.settingsBuilder()
                        .put("cluster.name", "elasticsearch")
                        .put("client.transport.sniff", true).build();
            }
        }).client(true).node();

        //启动
        Client client = node.client();

        //读取数据
        GetResponse response = client.prepareGet("twitter", "tweet", "1").execute().actionGet();
        try{
            System.out.println(response.getSourceAsString());
        }catch(Exception e){
            e.printStackTrace();
        }
        //关闭
        node.close();
    }
}
