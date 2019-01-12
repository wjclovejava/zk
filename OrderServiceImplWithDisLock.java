package com.example.zk.Demo;

import java.util.concurrent.locks.Lock;

/**
 * @Author: wjc
 * @Description:
 * @Date: created in 2019/1/11 19:30
 */
public class OrderServiceImplWithDisLock implements OrderService{

    private static OrderCodeGenerator ocg=new  OrderCodeGenerator();

    @Override
    public void createOrder() {
        String orderCode=null;
        Lock lock=new ZkDistributeImproveLock("/order_112");
        try{
            lock.lock();
            //获取订单号
            orderCode=ocg.getOrderCode();
        }finally {
            lock.unlock();
        }
        System.out.println(Thread.currentThread().getName()+"==========>"+orderCode);
    }
}
