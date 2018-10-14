package com.bulletinboard;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class BulletinBoardServer {
	private final int port;
	private final Server server;
	
	public BulletinBoardServer(int port) throws IOException {
		ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
		this.port = port;
		this.server = serverBuilder.addService(new BulletinBoardService()).build();
	}

	public void start() throws IOException {
		server.start();
		System.out.println("Server started, listening on " + port + ".");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("*** shutting down gRPC server since JVM is shutting down.");
				BulletinBoardServer.this.stop();
				System.err.println("*** server shut down.");
			}
		});
	}

	public void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		BulletinBoardServer server = new BulletinBoardServer(8981);
		server.start();
		server.blockUntilShutdown();
	}
}
