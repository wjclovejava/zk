package com.example.zk.Demo;

import java.util.concurrent.locks.Lock;

/**
 * @Author: wjc
 * @Description:
 * @Date: created in 2019/1/11 19:30
 */
public class OrderServiceImplWithLock implements OrderService{

    private static OrderCodeGenerator ocg=new  OrderCodeGenerator();

    private Lock lock=new ZkDistributeLock("/order_lock_123");

    @Override
    public void createOrder() {
        String orderCode=null;
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
