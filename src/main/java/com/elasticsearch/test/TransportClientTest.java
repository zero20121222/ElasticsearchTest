package com.elasticsearch.test;

import com.elasticsearch.util.ClusterParam;
import com.elasticsearch.util.UserModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;

import java.io.IOException;

/**
 * Desc:Transport实现elsaticsearch集群
 * Mail:v@terminus.io
 * Created by Michael Zhao
 * Date:2014-04-17.
 */
public class TransportClientTest {

    //创建集群
    @Test
    public void createIndexTest(){
		/*
		 * 创建客户端，所有的操作都由客户端开始，这个就好像是JDBC的Connection对象
		 * 用完记得要关闭
		 */
        Client client = new TransportClient(ClusterParam.getSettings(1))
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

        //在这里创建我们要索引的对象
        //其中zeroIndex为索引库名，一个es集群中可以有多个索引库。zeroType为索引类型，是用来区分同索引库下不同类型的数据的，一个索引库下可以有多个索引类型。
        //index必需是小写(ID可以不设置对象单独指定)
        client.prepareIndex("zeroindex", "zerotype", "1")
                //必须为对象单独指定ID
                .setSource(transform(new UserModel()))
                .execute()
                .actionGet();

        client.close();
    }

    @Test
    public void bulkTest(){
        Client client = new TransportClient(ClusterParam.getSettings(1))
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

        //一个模块化提交索引方式
        BulkRequestBuilder requestBuilder = client.prepareBulk();

        //提交100条数据
        for(int i=0 ; i<1000; i++){
            requestBuilder.add(client.prepareIndex("zeroindex", "zerotype", i+"").setSource(transform(new UserModel())));
        }

        BulkResponse responses = requestBuilder.execute().actionGet();
        if(responses.hasFailures()){
            System.out.println("response failed");
        }

        client.close();
    }

    @Test
    public void getIndexTest(){
        Client client = new TransportClient(ClusterParam.getSettings(1))
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

        GetResponse response = client.prepareGet("zeroindex", "zerotype", "1")
                .execute()
                .actionGet();

        System.out.println(transformJSON(response.getSourceAsString() , UserModel.class).getUserName());
        client.close();
    }

    @Test
    public void deleteIndexTest(){
        Client client = new TransportClient(ClusterParam.getSettings(1))
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

        DeleteResponse response = client.prepareDelete("zeroindex", "zerotype", "1").execute().actionGet();

        System.out.println(response.getHeaders());
        client.close();
    }

    /*
        elasticsearch 的搜索方式（可以添加过滤条件类似于普通的MySql查询类时）
     */
    @Test
    public void searchTest(){
        Client client = new TransportClient(ClusterParam.getSettings(1))
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

        //可以搜索多个索引库名
        SearchResponse response = client.prepareSearch("zeroindex")
                //搜索的索引类型需要更索引库名称相同
                .setTypes("zerotype")
                //除了一个初始的分散相去计算分布的长期频率更准确的评分
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)//.setQuery(QueryBuilders.termQuery("age", "2"))
                //FilterBuilders设置过滤操作处理（实现类似于数据库的查询方式）
                .setPostFilter(FilterBuilders.rangeFilter("age").from(12).to(18))
                //.setPostFilter(FilterBuilders.rangeFilter("age").filterName("1"))
                .setFrom(0).setSize(60).setExplain(true)
                .execute().actionGet();

        SearchHits shs = response.getHits();
        for(SearchHit hit : shs){
            System.out.println("花费时间:"+hit.getScore()+"\nObject:"+hit.getSourceAsString());
        }

        client.close();
    }

    /*
        游标的使用
     */
    @Test
    public void scrollTest(){
        Client client = new TransportClient(ClusterParam.getSettings(1))
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

        //使用游标保存一部分查询数据一段时间
        SearchResponse response = client.prepareSearch("zeroindex").setTypes("zerotype")
                .setSearchType(SearchType.SCAN).setScroll(new TimeValue(60000))
                .setPostFilter(FilterBuilders.rangeFilter("age").from(1).to(40))
                .setSize(20).execute().actionGet();

        //一直循环知道时间结束，游标时间过期
        while (true) {
            String scrollId = response.getScrollId();
            System.out.println("scrollId->"+scrollId);

            response = client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            for (SearchHit hit : response.getHits()) {
                System.out.println("花费时间:"+hit.getScore()+"\nObject:"+hit.getSourceAsString());
            }

            if (response.getHits().getHits().length == 0) {
                break;
            }
        }

        client.close();
    }

    @Test
    public void likeQueryTest(){
        Client client = new TransportClient(ClusterParam.getSettings(1))
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

        QueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("description" , "This");

        SearchResponse response = client.prepareSearch("zeroindex").setTypes("zerotype")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder)
                .setFrom(0).setSize(60).setExplain(true)
                .execute().actionGet();

        SearchHits shs = response.getHits();
        for(SearchHit hit : shs){
            System.out.println("花费时间:"+hit.getScore()+"\nObject:"+hit.getSourceAsString());
        }

        client.close();
    }

    private String transform(Object object){
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";

        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }

    private <T> T transformJSON(String ioInput, Class<T> classBody){
        ObjectMapper objectMapper = new ObjectMapper();

        T object = null;
        try {
            object = objectMapper.readValue(ioInput , classBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return object;
    }
}
