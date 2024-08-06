package org.practice.grpcmaven.service;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.practice.grpcmaven.mapper.MapperGrpc;
import org.practice.grpcmaven.models.entity.Clothes;
import org.practice.grpcmaven.models.entity.Users;
import org.practice.grpcmaven.repo.ClothesRepo;
import org.practice.grpcmaven.repo.OrdersRepo;
import org.practice.grpcmaven.repo.UsersRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import protobuf.clothes.*;

import java.util.List;

@GrpcService
@Service
@RequiredArgsConstructor
public class ClothesService extends ClothesServiceGrpc.ClothesServiceImplBase {

    private final ClothesRepo clothesRepo;
    private final UsersRepo usersRepo;
    private final OrdersRepo ordersRepo;

    private Logger loggerFactory = LoggerFactory.getLogger(ClothesService.class);

    @Override
    public void createCloth(CreateClothRequest createClothRequest, StreamObserver<CreateClothResponse> streamObserver) {
        CreateClothResponse createClothResponse = CreateClothResponse.newBuilder()
                .setCloth(MapperGrpc.INSTANCE.clothToProto(clothesRepo.save(Clothes.builder()
                        .quantity(createClothRequest.getQuantity())
                        .name(createClothRequest.getName())
                        .price(createClothRequest.getPrice())
                        .size(createClothRequest.getSize())
                        .paymentMethod(Payment.IN_SHOP)
                        .isbn(createClothRequest.getIsbn())
                        .build())))
                .build();

        streamObserver.onNext(createClothResponse);
        streamObserver.onCompleted();
    }

    @Override
    public void updateCloth(UpdateClothRequest updateClothRequest, StreamObserver<UpdateClothResponse> streamObserver) {
        Clothes clothes = clothesRepo.findById(updateClothRequest.getId()).orElseThrow(() -> new RuntimeException("Clothes not found"));

        if (updateClothRequest.getPrice() > 0) clothes.setPrice(updateClothRequest.getPrice());
        if (!updateClothRequest.getName().trim().isEmpty() && !updateClothRequest.getName().trim().isBlank())
            clothes.setName(updateClothRequest.getName());

        if (!updateClothRequest.getSize().trim().isEmpty() && !updateClothRequest.getSize().trim().isBlank())
            clothes.setSize(updateClothRequest.getSize());

        if (updateClothRequest.getQuantity() > 0) clothes.setQuantity(updateClothRequest.getQuantity());
        if (!updateClothRequest.getName().isEmpty() && !updateClothRequest.getName().isBlank())
            clothes.setName(updateClothRequest.getName());

        if (!updateClothRequest.getIsbn().trim().isEmpty()) clothes.setIsbn(updateClothRequest.getIsbn());
        if (!updateClothRequest.getPayment().equals(Payment.UNRECOGNIZED))
            clothes.setPaymentMethod(updateClothRequest.getPayment());

        UpdateClothResponse updateClothResponse = UpdateClothResponse.newBuilder()
                .setCloth(MapperGrpc.INSTANCE.clothToProto(clothesRepo.save(clothes)))
                .build();

        streamObserver.onNext(updateClothResponse);
        streamObserver.onCompleted();
    }

    @Override
    public void findCloth(FindClothRequest findClothRequest, StreamObserver<FindClothResponse> streamObserver) {
        Clothes clothes = clothesRepo.findById(findClothRequest.getId()).orElseThrow(() -> new RuntimeException("Clothes not found"));

        FindClothResponse findClothResponse = FindClothResponse.newBuilder()
                .setCloth(MapperGrpc.INSTANCE.clothToProto(clothes))
                .build();

        streamObserver.onNext(findClothResponse);
        streamObserver.onCompleted();
    }

    @Override
    public void deleteCloth(DeleteClothRequest deleteClothRequest, StreamObserver<DeleteClothResponse> streamObserver) {
        clothesRepo.delete(clothesRepo.findById(deleteClothRequest.getId()).orElseThrow(()
                -> new RuntimeException("Clothes not found")));

        DeleteClothResponse deleteClothResponse = DeleteClothResponse.newBuilder()
                .setMessage("DELETED Clothes with id: " + deleteClothRequest.getId())
                .build();

        streamObserver.onNext(deleteClothResponse);
        streamObserver.onCompleted();
    }

    @Override
    public void findClothByIsbn(FindClothByIsbnRequest findClothByIsbnRequest, StreamObserver<FindClothByIsbnResponse> streamObserver) {
        FindClothByIsbnResponse findClothByIsbnResponse = FindClothByIsbnResponse.newBuilder()
                .setClothes(MapperGrpc.INSTANCE.clothToProto(clothesRepo.findByIsbn(findClothByIsbnRequest.getIsbn())
                        .orElseThrow(() -> new RuntimeException("Clothes not found"))))
                .build();

        streamObserver.onNext(findClothByIsbnResponse);
        streamObserver.onCompleted();
    }

    @Override
    public void findClothByFilter(FindClothByFilterRequest findClothByFilterRequest, StreamObserver<FindClothByFilterResponse> streamObserver) {
        List<Clothes> clothes = clothesRepo.findAll();

        if (!findClothByFilterRequest.getIsbn().isBlank() && !findClothByFilterRequest.getIsbn().isEmpty())
            clothes = clothes.stream().filter(cloth -> cloth.getIsbn().equals(findClothByFilterRequest.getIsbn())).toList();
        if (!findClothByFilterRequest.getName().isBlank() && !findClothByFilterRequest.getName().isEmpty())
            clothes = clothes.stream().filter(cloth -> cloth.getName().equals(findClothByFilterRequest.getName())).toList();
        if (!findClothByFilterRequest.getSize().isBlank() && !findClothByFilterRequest.getSize().isEmpty())
            clothes = clothes.stream().filter(cloth -> cloth.getSize().equals(findClothByFilterRequest.getSize())).toList();
        if (findClothByFilterRequest.getQuantity() > 0)
            clothes = clothes.stream().filter(cloth -> cloth.getQuantity() >= findClothByFilterRequest.getQuantity()
                    || cloth.getQuantity() <= findClothByFilterRequest.getQuantity()).toList();
        if (findClothByFilterRequest.getPrice() > 0)
            clothes.stream().filter(cloth -> cloth.getPrice() <= findClothByFilterRequest.getPrice()).toList();

        FindClothByFilterResponse findClothByFilterResponse = FindClothByFilterResponse.newBuilder()
                .addAllList(MapperGrpc.INSTANCE.clothesToProto(clothes))
                .build();

        streamObserver.onNext(findClothByFilterResponse);
        streamObserver.onCompleted();
    }

}
