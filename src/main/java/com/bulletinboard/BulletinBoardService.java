package com.bulletinboard;

import java.util.HashMap;

import io.grpc.stub.StreamObserver;

public class BulletinBoardService extends BoardGrpc.BoardImplBase {
	
	private final HashMap<String, String> posts;
	
	BulletinBoardService() {
		posts = new HashMap<String, String>();
	}
	
	BulletinBoardService(HashMap<String, String> posts) {
		this.posts = posts;
	}
	
	@Override
	public void post(postRequest request, StreamObserver<postResponse> responseObserver) {
		responseObserver.onNext(postPost(request));
		responseObserver.onCompleted();
	}
	
	private postResponse postPost(postRequest request) {
		if (posts.containsKey(request.getTitle())) {
			return postResponse.newBuilder().setResponse("Title for post " + request.getTitle() + " already exists.").build();
		}
		posts.put(request.getTitle(), request.getBody());
		postResponse resp = postResponse.newBuilder().setResponse(request.getTitle() + " post completed successfully.").build();
		return resp;
	}
	
	@Override
	public void get(getRequest request, StreamObserver<getResponse> responseObserver) {
		if (!posts.containsKey(request.getTitle())) {
			responseObserver.onNext(getResponse.newBuilder().setBody("Title for post " + request.getTitle() + " already exists.").build());
		}
		else {
			responseObserver.onNext(getResponse.newBuilder().setBody(request.getTitle() + ": " + posts.get(request.getTitle())).build());
		}
		responseObserver.onCompleted();
	}

	@Override
	public void delete(deleteRequest request, StreamObserver<deleteResponse> responseObserver) {
		if (posts.containsKey(request.getTitle())) {
			posts.remove(request.getTitle());
			responseObserver.onNext(deleteResponse.newBuilder().setResponse("Deleted " + request.getTitle() + " post successfuly.").build());
		}
		else {
			responseObserver.onNext(deleteResponse.newBuilder().setResponse(request.getTitle() + " post does not exist.").build());
		}
		responseObserver.onCompleted();
	}
	
	@Override
	public void list(listRequest request, StreamObserver<listResponse> responseObserver) {
		responseObserver.onNext(listResponse.newBuilder().addAllTitles(posts.keySet()).build());
		responseObserver.onCompleted();
	}
}