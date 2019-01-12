package com.example.zk.Demo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @Author: wjc
 * @Description: zookeeper分布式锁  实现创建订单 改良版本
 * @Date: created in 2019/1/11 19:51
 */
public class Test2 {

    public static void main(String[] args) {
        //并发数量
        int current =30;

        //循环屏障
        CyclicBarrier cyclicBarrier=new CyclicBarrier(current);

        //多线程模拟高并发
        for(int i=0;i<current;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //创建多个订单服务模拟分布式场景
                    OrderService orderService=new OrderServiceImplWithDisLock();

                    System.out.println(Thread.currentThread().getName()+"=========我已经准备好========");
                    try{
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    //调用订单服务
                    orderService.createOrder();
                }
            }).start();
        }
    }
}
