
import com.proto.calc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import server.CalculatorService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientTest {

    public static void testUnary(ManagedChannel channel){
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        SumRequest request = SumRequest.newBuilder()
                .setX(1000)
                .setY(1000).build();
        SumResponse response = stub.sum(request);
        System.out.println(response.getResult());
    }

    public static void testClientStreaming(ManagedChannel channel) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);

        StreamObserver<AverageRequest> request = stub.average(new StreamObserver<>() {
            @Override
            public void onNext(AverageResponse value) {

                System.out.println(value.getAverage());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error received");
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        double[] numbers = {1.0, 2.0, 7.0, 90.0, 6000.0, 10.0, 100.0};
        for (double number : numbers) {
            addToAverage(request, number);
        }
        request.onCompleted();
        latch.await(10L, TimeUnit.SECONDS);
    }
    private static void addToAverage(StreamObserver<AverageRequest> streamObserver, double number){
        streamObserver.onNext(AverageRequest.newBuilder()
                .setNumber(number).build());
    }

    private static void testBiDiStreaming(ManagedChannel channel) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);

        StreamObserver<FindMaximumRequest> request = stub.maximum(new StreamObserver<>() {
            @Override
            public void onNext(FindMaximumResponse value) {
                System.out.println(value.getMaximum());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
                System.out.println("Completed!");

            }
        });

        int[] numbers = {1, 2, 7, 90, 6000, 10, 100};
        for (int number : numbers) {

            streamMaximum(request, number);
        }
        request.onCompleted();
        latch.await(10L, TimeUnit.SECONDS);
    }
    private static void streamMaximum(StreamObserver<FindMaximumRequest> requestStreamObserver, int number){
        requestStreamObserver.onNext(FindMaximumRequest.newBuilder().setNumber(number).build());
    }
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5555)
                .usePlaintext()
                .build();
       testUnary(channel);
       testClientStreaming(channel);
       testBiDiStreaming(channel);
       channel.shutdown();

    }
}
