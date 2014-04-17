package com.elasticsearch.util;

import com.google.common.base.Objects;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

/**
 * Desc:
 * Mail:v@terminus.io
 * Created by Michael Zhao
 * Date:2014-04-17.
 */
public enum ClusterParam {
    /*
    //指定集群名称
    .put("cluster.name", "elasticsearch")
    //探测集群中机器状态
    .put("client.transport.sniff", true)
     */
    ZERO(1 , ImmutableSettings.settingsBuilder().put("cluster.name", "Zero").put("client.transport.sniff" , true).build()),
    ONE(2 , ImmutableSettings.settingsBuilder().put("cluster.name" , "One").put("client.transport.sniff", true).build()),
    TOW(3 , ImmutableSettings.settingsBuilder().put("cluster.name" , "Tow").put("client.transport.sniff" , true).build());

    private final Integer index;

    private final Settings settings;

    private ClusterParam(Integer index, Settings settings){
        this.index = index;
        this.settings = settings;
    }

    public static Settings getSettings(Integer index){
        for(ClusterParam clusterParam : ClusterParam.values()){
            if(Objects.equal(index , clusterParam.index)){
                return clusterParam.settings;
            }
        }

        return null;
    }
}
