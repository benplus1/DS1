package com.bulletinboard;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.bulletinboard.BoardGrpc.BoardBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BulletinBoardClient {
	private final ManagedChannel channel;
	private static BoardBlockingStub blockingStub;

	public BulletinBoardClient(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
	}

	public BulletinBoardClient(ManagedChannelBuilder<?> channelBuilder) {
		channel = channelBuilder.build();
		blockingStub = BoardGrpc.newBlockingStub(channel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public static void main(String[] args) throws InterruptedException {
		BulletinBoardClient client = new BulletinBoardClient("localhost", 8981);
        @SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

        while(true) {
        	System.out.print("Input a command: ");
        	String line = scanner.nextLine();
        	if (line.equals("post")) {
        		System.out.print("Input your post title: ");
        		String title = scanner.nextLine();
        		System.out.print("Input your post body: ");
        		String body = scanner.nextLine();
        		postRequest req = postRequest.newBuilder().setTitle(title).setBody(body).build();
        		
        		postResponse resp;
        		try {
        			resp = blockingStub.post(req);
        			System.out.println(resp.getResponse());
        		} catch (StatusRuntimeException e) {
        			System.out.println("RPC failed: " + e.getStatus());
        		}	
        	}
        	else if (line.equals("get")) {
        		System.out.print("Input your post title: ");
        		String title = scanner.nextLine();
        		getRequest req = getRequest.newBuilder().setTitle(title).build();
        		
        		getResponse resp;
        		try {
        			resp = blockingStub.get(req);
        			System.out.println(resp.getBody());
        		} catch (StatusRuntimeException e) {
        			System.out.println("RPC failed: " + e.getStatus());
        		}	 
        	}
        	else if (line.equals("delete")) {
        		System.out.print("Input your post title: ");
        		String title = scanner.nextLine();
        		deleteRequest req = deleteRequest.newBuilder().setTitle(title).build();
        		
        		deleteResponse resp;
        		try {
        			resp = blockingStub.delete(req);
        			System.out.println(resp.getResponse());
        		} catch (StatusRuntimeException e) {
        			System.out.println("RPC failed: " + e.getStatus());
        		}	 
        	}
        	else if (line.equals("list")) {
        		System.out.println("Grabbing post titles.");
        		listRequest req = listRequest.newBuilder().setList(0).build();
        		
        		listResponse resp;
        		try {
        			resp = blockingStub.list(req);
        			for (String title : resp.getTitlesList()) {
        				System.out.print(title + ", ");
        				System.out.println();
        			}
        		} catch (StatusRuntimeException e) {
        			System.out.println("RPC failed: " + e.getStatus());
        		}	
        	}
        	else if (line.equals("exit")) {
        		System.out.println("Exiting.");
    			client.shutdown();
                System.exit(1);
        	}
        	else {
        		System.out.println("Command invalid.");
        	}
        }
	}
}
