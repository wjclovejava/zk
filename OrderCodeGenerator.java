package com.example.zk.Demo;


import sun.applet.Main;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: wjc
 * @Description:
 * @Date: created in 2019/1/11 19:32
 */
public class OrderCodeGenerator {
    //自增长序号
    private int i =0;

    //生成订单号
    public String getOrderCode(){
        Date now=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-");
        return sdf.format(now)+ ++i;
    }


    public static void main(String[] args) {
        OrderCodeGenerator ocg=new OrderCodeGenerator();
        for(int i=0;i<10;i++){
            System.out.println(   ocg.getOrderCode());
        }

    }
}
