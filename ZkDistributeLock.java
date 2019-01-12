package com.example.zk.Demo;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: wjc
 * @Description: zookeeper方式实现分布式锁
 * @Date: created in 2019/1/11 19:22
 */
public class ZkDistributeLock implements Lock {

    //监听节点路径
    private String lockPath;

    private ZkClient client;

    public ZkDistributeLock(String lockPath) {
        super();
        this.lockPath = lockPath;
        client=new ZkClient("localhost:2181");
        client.setZkSerializer(new MyZkSerializer());
    }

    @Override
    public void lock() {//获锁成功即刻返回,未获取锁,原地阻塞
        if(!tryLock()){
            waitForLock();
            lock();
        }
    }

    private void waitForLock() {
        //只有1个人能获取锁
        CountDownLatch countDownLatch=new CountDownLatch(1);

        IZkDataListener listener=new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {

            }
            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println(dataPath+"节点被删除");
                countDownLatch.countDown();
            }
        };
        client.subscribeDataChanges(lockPath, listener);
        //节点若存在.阻塞自己
        if(this.client.exists(lockPath)){
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //取消注册
        client.unsubscribeDataChanges(lockPath, listener);
    }

    /**
     *  尝试获取锁,如果能获取锁.即创建节点,返回true
     * @return
     */
    @Override
    public boolean tryLock() {
       try {
           client.createEphemeral(lockPath);
       }catch (ZkNodeExistsException e){
           return false;
       }
        return true;
    }

    /**
     * 释放锁:删除节点
     */
    @Override
    public void unlock() {
        client.delete(lockPath);
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }
}
