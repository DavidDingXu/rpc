/*
 * *****************************************************
 * *****************************************************
 * Copyright (C), 2018-2020, panda-fa.com
 * FileName: com.panda.rpc.server.RpcServer
 * Author:   丁许
 * Date:     2019/7/14 19:03
 * *****************************************************
 * *****************************************************
 */
package com.panda.rpc.server;

import com.panda.rpc.annotation.RpcService;
import com.panda.rpc.register.IregisterCenter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 开启一个rpc远程服务
 *
 * @author 丁许
 * @date 2019/7/14 19:03
 */
@Slf4j
public class RpcServer {

	private static ExecutorService executorService = Executors.newCachedThreadPool();

	/**
	 * 注册中心
	 */
	private IregisterCenter registerCenter;

	/**
	 * 服务发布的ip地址
	 * 这边自定义因为 InetAddress.getLocalHost().getHostAddress()可能获得是127.0.0.1
	 */
	private String serviceIp;

	/**
	 * 服务发布端口
	 */
	private int servicePort;

	/**
	 * 服务名称和服务对象的关系
	 */
	private Map<String, Object> handlerMap = new HashMap<>();

	public RpcServer(IregisterCenter iregisterCenter, String ip, int servicePort) {
		this.registerCenter = iregisterCenter;
		this.serviceIp = ip;
		this.servicePort = servicePort;
	}

	/**
	 * 绑定服务名以及服务对象
	 *
	 * @param services 服务列表
	 */
	public void bindService(List<Object> services) {
		for (Object service : services) {
			RpcService anno = service.getClass().getAnnotation(RpcService.class);
			if (null == anno) {
				//注解为空的情况，version就是空，serviceName就是
				throw new RuntimeException("服务并没有注解，请检查。" + service.getClass().getName());
			}
			String serviceName = anno.value().getName();
			String version = anno.version();
			if (!"".equals(version)) {
				serviceName += "-" + version;
			}
			handlerMap.put(serviceName, service);
		}
	}

	/**
	 * 发布服务
	 */
	public void publish() {
		//启动一个socket服务
		try (ServerSocket serverSocket = new ServerSocket(servicePort)) {
			//服务注册
			handlerMap.keySet().forEach(serviceName -> {
				try {
					registerCenter.register(serviceName, serviceIp + ":" + servicePort);
				} catch (Exception e) {

					log.error("服务注册失败,e:{}", e.getMessage());
					throw new RuntimeException("服务注册失败");
				}
				log.info("成功注册服务，服务名称：{},服务地址：{}", serviceName, serviceIp + ":" + servicePort);
			});
			//循环监听
			while (true) {
				//使用BIO方案
				Socket socket = serverSocket.accept();
				//异步处理请求
				executorService.execute(new ProcessRequest(socket, handlerMap));
			}

		} catch (IOException e) {
			log.error("socket异常,e:{}", e.getMessage());
		}
	}
}
