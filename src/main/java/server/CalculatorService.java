package server;


import com.proto.calc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorService extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        System.out.println("Request received");
        int x = request.getX();
        int y = request.getY();
        SumResponse response = SumResponse.newBuilder().setResult(x + y).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public StreamObserver<AverageRequest> average(StreamObserver<AverageResponse> responseObserver) {
        StreamObserver<AverageRequest> requestStreamObserver = new StreamObserver<>() {
            private double sum = 0.0;
            private double count = 0.0;

            @Override
            public void onNext(AverageRequest value) {
                System.out.println("Message recieved!");
                sum += value.getNumber();
                count++;
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());

            }

            @Override
            public void onCompleted() {

                System.out.println("Server completed");
                responseObserver.onNext(AverageResponse.newBuilder()
                        .setAverage(sum / count).build());
                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;


    }

    @Override
    public StreamObserver<FindMaximumRequest> maximum(StreamObserver<FindMaximumResponse> responseObserver) {
        StreamObserver<FindMaximumRequest> requestStreamObserver = new StreamObserver<>() {
            private int maxi = Integer.MIN_VALUE;

            @Override
            public void onNext(FindMaximumRequest value) {
                System.out.println("Message recieved!");
                maxi = Math.max(maxi, value.getNumber());
                FindMaximumResponse response = FindMaximumResponse
                        .newBuilder()
                                .setMaximum(maxi)
                        .build();
                responseObserver.onNext(response);

            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;
    }

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
        if(request.getNumber() < 0){
            responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("Number must be positive")
                            .asRuntimeException());
        }else{
            responseObserver.onNext(SquareRootResponse.newBuilder()
                    .setNumber(Math.sqrt(request.getNumber())).build());
            responseObserver.onCompleted();
        }
    }
}
