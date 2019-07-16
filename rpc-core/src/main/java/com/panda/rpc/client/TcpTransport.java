/*
 * *****************************************************
 * *****************************************************
 * Copyright (C), 2018-2020, panda-fa.com
 * FileName: com.panda.rpc.client.TcpTransport
 * Author:   丁许
 * Date:     2019/7/15 18:56
 * *****************************************************
 * *****************************************************
 */
package com.panda.rpc.client;

import com.panda.rpc.common.RpcRequest;
import com.panda.rpc.common.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * socket封装类
 *
 * @author 丁许
 * @date 2019/7/15 18:56
 */
@Slf4j
public class TcpTransport {

	private String host;

	private int port;

	public TcpTransport(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * 创建一个socket连接，每次请求都创建新的
	 *
	 * @return socket连接
	 */
	private Socket newSocket() throws IOException {
		return new Socket(host, port);
	}

	/**
	 * 发起远程调用
	 *
	 * @param request 请求参数
	 *
	 * @return 服务处理结果
	 *
	 * @throws Exception 远程调用失败
	 */
	public RpcResponse send(RpcRequest request) throws Exception {
		Socket socket = this.newSocket();
		try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
			outputStream.writeObject(request);
			outputStream.flush();
			return (RpcResponse)inputStream.readObject();
		} catch (Exception e) {
			log.error("发起远程调用失败,e:{}", e.getMessage());
			throw e;
		}
	}
}
