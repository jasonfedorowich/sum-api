package server;

import com.proto.sum.SumRequest;
import com.proto.sum.SumResponse;
import com.proto.sum.SumServiceGrpc;
import io.grpc.stub.StreamObserver;

public class SumServer extends SumServiceGrpc.SumServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        System.out.println("Request received");
        int x = request.getX();
        int y = request.getY();
        SumResponse response = SumResponse.newBuilder().setResult(x + y).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
