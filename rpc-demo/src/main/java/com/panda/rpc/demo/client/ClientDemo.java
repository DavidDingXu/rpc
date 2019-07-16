/*
 * *****************************************************
 * *****************************************************
 * Copyright (C), 2018-2020, panda-fa.com
 * FileName: com.panda.rpc.demo.client.ClientDemo
 * Author:   丁许
 * Date:     2019/7/15 20:21
 * *****************************************************
 * *****************************************************
 */
package com.panda.rpc.demo.client;

import com.panda.rpc.client.RpcClientProxy;
import com.panda.rpc.demo.api.Ihello;
import com.panda.rpc.discover.IServerDiscover;
import com.panda.rpc.discover.ZkServerDiscover;
import lombok.extern.slf4j.Slf4j;

/**
 * <Description>
 *
 * @author 丁许
 * @date 2019/7/15 20:21
 */
@Slf4j
public class ClientDemo {

	public static void main(String[] args) throws InterruptedException {
		IServerDiscover serverDiscover = new ZkServerDiscover("192.168.40.14:2181");
		RpcClientProxy rpcClientProxy = new RpcClientProxy(serverDiscover);

		//测试版本
		Ihello ihello = null;
		try {
			ihello = rpcClientProxy.clientProxy(Ihello.class, "1.0");
			System.out.println(ihello.sayHello("dd"));
		} catch (Exception e) {
			log.error("调用失败：e:{}",e.getMessage());
		}
		//测试集群
		for (int i = 0; i < 10; i++) {
			Ihello helloService = rpcClientProxy.clientProxy(Ihello.class);
			try {
				String result=helloService.sayHello("xxx");
				System.out.println(result);
			} catch (Exception e) {
				log.error("调用失败：e:{}",e.getMessage());
			}

			Thread.sleep(2000);
		}
	}

}
