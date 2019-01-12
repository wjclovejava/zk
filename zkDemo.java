package com.example.zk.Demo;


import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Calendar;

/**
 * @Author: wjc
 * @Description:
 * @Date: created in 2019/1/10 18:54
 */
public class zkDemo {

    public static void main(String[] args) {
        ZkClient client=new ZkClient("localhost:2181");
        //设置自定义序列化
        client.setZkSerializer(new MyZkSerializer());
        //监听 /mike/a 节点 变化
        client.subscribeDataChanges("/mike/a", new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("节点变化"+data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("节点已经删除");
            }
        });


        try {
            //睡觉1小时
            Thread.sleep(1000*60*60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
