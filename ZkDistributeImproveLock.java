package com.example.zk.Demo;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: wjc
 * @Description: zookeeper方式实现分布式锁 改良版本
 * @Date: created in 2019/1/11 19:22
 */
public class ZkDistributeImproveLock implements Lock {

    //监听节点路径
    private String lockPath;

    private ZkClient client;

    //当前节点
    private String currentPath;
    //上一个节点
    private String beforePath;

    public ZkDistributeImproveLock(String lockPath) {
        super();
        System.out.println(Thread.currentThread().getName()+"初始化锁");
        this.lockPath = lockPath;
        client=new ZkClient("localhost:2181");
        client.setZkSerializer(new MyZkSerializer());
        //尝试创建lockPath,如果没有父节点,无法创建子节点
        if(!this.client.exists(lockPath)){
            try {
                this.client.createPersistent(lockPath);
            }catch (ZkNodeExistsException e){
               e.printStackTrace();
            }
        }
    }

    @Override
    public void lock() {//获锁成功即刻返回,未获取锁,原地阻塞
        if(!tryLock()){
            waitForLock();
            lock();
        }
        System.out.println(Thread.currentThread().getName()+"获取到锁");
    }
    /**
     *   监听前一个节点
     */
    private void waitForLock() {
        System.out.println(Thread.currentThread().getName()+"阻塞");
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
        client.subscribeDataChanges(this.beforePath, listener);
        //节点若存在.阻塞自己
        if(this.client.exists(this.beforePath)){
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //取消注册
        client.unsubscribeDataChanges(this.beforePath, listener);
    }

    /**
     *  尝试获取锁,如果能获取锁.即创建节点,返回true
     * @return
     */
    @Override
    public boolean tryLock() {
        System.out.println(Thread.currentThread().getName()+"尝试创建锁");
        if(this.currentPath == null){
            currentPath=this.client.createEphemeralSequential(lockPath + "/","aaa" );
        }
        //获取所有子节点
        List<String> children = this.client.getChildren(lockPath);
        //排序
        Collections.sort(children);
        //当前节点是否是最小的那个
        if(currentPath.equals(lockPath+ "/" +children.get(0))){
            return true;
        }else {
            //如果不是  取前一个节点
            int curIndex=children.indexOf(currentPath.substring(lockPath.length()+1));
            beforePath= lockPath+ "/" +children.get(curIndex-1);
        }
        return false;
    }

    /**
     * 释放锁:删除节点(删除自己)
     */
    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName()+"释放锁");
        client.delete(this.currentPath);
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
