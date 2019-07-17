/*
 * *****************************************************
 * *****************************************************
 * Copyright (C), 2018-2020, panda-fa.com
 * FileName: com.panda.rpc.demo.provider.ServerDemo1
 * Author:   丁许
 * Date:     2019/7/15 18:46
 * *****************************************************
 * *****************************************************
 */
package com.panda.rpc.demo.provider;

import com.panda.rpc.demo.api.Ihello;
import com.panda.rpc.register.IregisterCenter;
import com.panda.rpc.register.ZkRegisterCenter;
import com.panda.rpc.server.RpcServer;

import java.util.Arrays;

/**
 * <Description>
 *
 * @author 丁许
 * @date 2019/7/15 18:46
 */
public class ServerDemo1 {

	public static void main(String[] args) throws InterruptedException {
		Ihello helloService = new HelloImpl1();
		Ihello helloService2=new HelloImpl1Version();
		IregisterCenter registerCenter = new ZkRegisterCenter("192.168.40.14:2181");
		RpcServer rpcServer = new RpcServer(registerCenter, "127.0.0.1", 8888);
		rpcServer.bindService(Arrays.asList(helloService,helloService2));
		rpcServer.publish();
	}
}
