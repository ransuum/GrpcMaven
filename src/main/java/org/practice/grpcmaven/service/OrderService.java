package org.practice.grpcmaven.service;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.practice.grpcmaven.mapper.MapperGrpc;
import org.practice.grpcmaven.models.entity.Clothes;
import org.practice.grpcmaven.models.entity.Orders;
import org.practice.grpcmaven.models.entity.Users;
import org.practice.grpcmaven.repo.ClothesRepo;
import org.practice.grpcmaven.repo.OrdersRepo;
import org.practice.grpcmaven.repo.UsersRepo;
import org.springframework.stereotype.Service;
import protobuf.orders.*;

@GrpcService
@Service
@RequiredArgsConstructor
public class OrderService extends OrderServiceGrpc.OrderServiceImplBase {
    private final OrdersRepo ordersRepo;
    private final UsersRepo usersRepo;
    private final ClothesRepo clothesRepo;

    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<CreateOrderResponse> responseObserver) {
        Users byEmail = usersRepo.findByEmail(request.getUserEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        Clothes clothes = clothesRepo.findById(request.getIdCloth()).orElseThrow(() -> new RuntimeException("Cloth not found"));

        CreateOrderResponse createOrderResponse = CreateOrderResponse.newBuilder()
                .setOrder(MapperGrpc.INSTANCE.ordersToProto(ordersRepo.save(Orders.builder()
                        .user(byEmail)
                        .cloth(clothes)
                        .build())))
                .build();

        responseObserver.onNext(createOrderResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteOrder(DeleteOrderRequest request, StreamObserver<DeleteOrderResponse> responseObserver) {
        Orders orders = ordersRepo.findById(request.getId()).orElseThrow(() -> new RuntimeException("Order not found"));
        ordersRepo.delete(orders);

        responseObserver.onNext(DeleteOrderResponse.newBuilder()
                .setMessage("Order deleted with id: " + orders.getId())
                .build());
    }

    @Override
    public void findOrderByEmail(FindOrderRequest findOrderRequest, StreamObserver<FindOrderResponse> responseObserver) {
        Users byEmail = usersRepo.findByEmail(findOrderRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

        FindOrderResponse order = FindOrderResponse.newBuilder()
                .setOder(MapperGrpc.INSTANCE.ordersToProto(ordersRepo.findByUser(byEmail)
                        .orElseThrow(() -> new RuntimeException("Order not found"))))
                .build();

        responseObserver.onNext(order);
        responseObserver.onCompleted();
    }
}
