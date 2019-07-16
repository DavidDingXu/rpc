/*
 * *****************************************************
 * *****************************************************
 * Copyright (C), 2018-2020, panda-fa.com
 * FileName: com.panda.rpc.server.ProcessRequest
 * Author:   丁许
 * Date:     2019/7/15 17:54
 * *****************************************************
 * *****************************************************
 */
package com.panda.rpc.server;

import com.panda.rpc.common.ResponseCode;
import com.panda.rpc.common.RpcRequest;
import com.panda.rpc.common.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;

/**
 * 处理远程请求
 *
 * @author 丁许
 * @date 2019/7/15 17:54
 */
@Slf4j
public class ProcessRequest implements Runnable {

	/**
	 * socket连接
	 */
	private Socket socket;

	/**
	 * 服务映射
	 */
	private Map<String, Object> handlerMap;

	public ProcessRequest(Socket socket, Map<String, Object> handlerMap) {
		this.socket = socket;
		this.handlerMap = handlerMap;
	}

	@Override
	public void run() {
		try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
			//反序列化RpcRequest
			RpcRequest request = (RpcRequest) inputStream.readObject();
			Object result = this.invoke(request);
			outputStream.writeObject(result);
			outputStream.flush();
		} catch (Exception e) {
			log.error("socket读写数据异常:e:{}", e.getMessage());
		}
	}

	/**
	 * 服务调用返回处理结果
	 *
	 * @param request 服务请求
	 *
	 * @return 处理结果
	 */
	private Object invoke(RpcRequest request)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		//获得服务名称
		String serviceName = request.getClassName();
		//获得版本号
		String version = request.getVersion();
		//获得方法名
		String methodName = request.getMethodName();
		//获得参数数组
		Object[] params = request.getParams();
		//获得参数类型数据
		Class<?>[] argTypes = Arrays.stream(params).map(Object::getClass).toArray(Class<?>[]::new);
		if (version != null && !"".equals(version)) {
			serviceName = serviceName + "-" + version;
		}
		Object service = handlerMap.get(serviceName);
		if (null == service) {
			return RpcResponse.fail(ResponseCode.ERROR404, "未找到服务");
		}
		Method method = service.getClass().getMethod(methodName, argTypes);
		if (null == method) {
			return RpcResponse.fail(ResponseCode.ERROR404, "未找到服务方法");
		}
		return RpcResponse.success(method.invoke(service, params));
	}
}
