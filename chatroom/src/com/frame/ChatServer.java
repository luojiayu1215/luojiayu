package com.frame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class ChatServer {
		//声明服务器端套接字ServerSocket
	    ServerSocket serverSocket;
	    //输入流列表集合
	    ArrayList<BufferedReader> bReaders = new ArrayList<BufferedReader>();
	    //输入流列表集合
	    ArrayList<PrintWriter> pWriters = new ArrayList<PrintWriter>();
	    //聊天信息链表集合
	    LinkedList<String> msgList =new LinkedList<String>();
	
	    public ChatServer() {
	        try {
	            //创建服务器端套接字ServerSocket,在8888端口监听
	            serverSocket = new ServerSocket(8088);
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        //创建接受客户端Socket读线程实例，并启动
	        new AcceptSocketThread().start();
	        //创建给 客户端发送信息读线程实例，并启动
	        new SendMsgToClient().start();
	        System.out.println("服务器已经启动...");
	    }
	
	    //接收客户端Socket套接字线程
	    class AcceptSocketThread extends Thread{
	        public void run() {
	            while(this.isAlive()) {
	                try {
	                    //接收一个客户端Socket对象
	                    Socket socket = serverSocket.accept();
	                    //建立该客户端读通信管道
	                    if(socket != null) {
	                        //获取Socket对象读输入流
	                        BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	                        //将输入流添加到输入流列表集合中
	                        bReaders.add(bReader);
	                        //开启一个线程接收客户端读聊天信息
	                        new GetMsgFromClient(bReader).start();
	                        //获取Socket对象读输出流，并添加到输入流列表集合中
	                        pWriters.add(new PrintWriter(socket.getOutputStream()));
	                    }
	                } catch (IOException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                } 
	            }
	        }
	    }
	    //接收客户端读聊天信息读线程
	    class GetMsgFromClient extends Thread{
	        BufferedReader bReader;
	
	        public GetMsgFromClient(BufferedReader bReader) {
	            // TODO Auto-generated constructor stub
	            this.bReader = bReader;
	        }
	
	        public void run() {
	            while(this.isAlive()) {
	                String strMsg;
	                try {
	                    strMsg = bReader.readLine();
	                    if(strMsg != null) {
	                        //SimpleDateFormat 日期格式化类，制定日期格式
	                        //"年-月-日 时:分:秒",例如"2017-11-06 23:06:11"
	                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	                        //获取当前系统时间，并使用日期格式化类华为制定格式读字符串
	                        String strTime = dateFormat.format(new Date());
	                        //将时间和信息添加到信息链表集合中
	                        msgList.addFirst("<==" + strTime + "==>\n" + strMsg);
	                    }
	                } catch (IOException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            }
	        }
	
	    }
	    //给所有客户发送聊天信息读线程
	    class SendMsgToClient extends Thread{
	        public void run() {
	            while(this.isAlive()) {
	                try {
	                    //如果信息链表集合不空（还有聊天信息未发送）
	                    if(!msgList.isEmpty()) {
	                        //取信息链表集合中读最后一条，并移除
	                        String msg = msgList.removeLast();
	                        //对输出流列表集合进行遍历，循环发送信息给所有客户端
	                        for (int i = 0; i < pWriters.size(); i++) {
	                            pWriters.get(i).println(msg);
	                            pWriters.get(i).flush();
	                        }
	                    }
	                } catch (Exception e) {
	                    // TODO: handle exception
	                }
	            }
	        }
	    }
	    //主函数调用
	    public static void main(String args[]) {
	        new ChatServer();
	    }
}

