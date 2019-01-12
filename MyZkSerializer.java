package com.example.zk.Demo;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.io.UnsupportedEncodingException;

/**
 * @Author: wjc
 * @Description:
 * @Date: created in 2019/1/10 19:55
 */
public class MyZkSerializer implements ZkSerializer {

    private String charset="utf-8";
    /**
     * 序列化
     * @param obj
     * @return
     * @throws ZkMarshallingError
     */
    @Override
    public byte[] serialize(Object obj) throws ZkMarshallingError {
        try {
            return String.valueOf(obj).getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw  new ZkMarshallingError(e);
        }
    }

    /**
     * 反序列化
     * @param bytes
     * @return
     * @throws ZkMarshallingError
     */
    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            throw  new ZkMarshallingError(e);
        }
    }
}
