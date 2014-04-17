package com.elasticsearch.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.elasticsearch.common.joda.time.DateTime;

import java.util.*;

/**
 * Desc:
 * Mail:v@terminus.io
 * Created by Michael Zhao
 * Date:2014-04-17.
 */
@ToString
public class UserModel {
    @Setter
    @Getter
    private String userId;

    @Setter
    @Getter
    private String userName;

    @Setter
    @Getter
    private Integer age;

    @Setter
    @Getter
    private String description;

    @Setter
    @Getter
    private Date createAt;

    public UserModel(){
        //创建一个唯一的
        this.userId = UUID.randomUUID().toString();

        this.userName = "Testing";

        this.age = new Random(System.currentTimeMillis()).nextInt(0)%100;

        this.description = "This is elasticsearch testing!";

        this.createAt = DateTime.now().toDate();
    }
}
