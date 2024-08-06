package org.practice.grpcmaven.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.practice.grpcmaven.mapper.MapperGrpc;
import org.practice.grpcmaven.models.entity.Cloth;
import org.practice.grpcmaven.repo.ClothesRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import protobuf.clothes.*;

@GrpcService
@Service
public class ClothesService extends ClothesServiceGrpc.ClothesServiceImplBase {

    @Autowired
    private ClothesRepo clothesRepo;

    private Logger loggerFactory = LoggerFactory.getLogger(ClothesService.class);

    @Override
    public void createCloth(CreateClothRequest createClothRequest, StreamObserver<CreateClothResponse> streamObserver) {
        CreateClothResponse createClothResponse = CreateClothResponse.newBuilder()
                .setCloth(MapperGrpc.INSTANCE.clothToProto(clothesRepo.save(Cloth.builder()
                        .quantity(createClothRequest.getQuantity())
                        .name(createClothRequest.getName())
                        .price(createClothRequest.getPrice())
                        .size(createClothRequest.getSize())
                        .paymentMethod(Payment.IN_SHOP)
                        .isbn(createClothRequest.getIsbnOfCloth())
                        .build())))
                .build();

        streamObserver.onNext(createClothResponse);
        streamObserver.onCompleted();
    }

    @Override
    public void updateCloth(UpdateClothRequest updateClothRequest, StreamObserver<UpdateClothResponse> streamObserver) {
        Cloth cloth = clothesRepo.findById(updateClothRequest.getId()).orElseThrow(() -> new RuntimeException("Cloth not found"));

        if (updateClothRequest.getPrice() > 0) cloth.setPrice(updateClothRequest.getPrice());
        if (!updateClothRequest.getName().trim().isEmpty() && !updateClothRequest.getName().trim().isBlank())
            cloth.setName(updateClothRequest.getName());

        if (!updateClothRequest.getSize().trim().isEmpty() && !updateClothRequest.getSize().trim().isBlank())
            cloth.setSize(updateClothRequest.getSize());

        if (updateClothRequest.getQuantity() > 0) cloth.setQuantity(updateClothRequest.getQuantity());
        if (!updateClothRequest.getName().isEmpty() && !updateClothRequest.getName().isBlank())
            cloth.setName(updateClothRequest.getName());

        if (!updateClothRequest.getIsbnOfCloth().trim().isEmpty()) cloth.setIsbn(updateClothRequest.getIsbnOfCloth());
        if (!updateClothRequest.getPayment().equals(Payment.UNRECOGNIZED)) cloth.setPaymentMethod(updateClothRequest.getPayment());

        UpdateClothResponse updateClothResponse = UpdateClothResponse.newBuilder()
                .setCloth(MapperGrpc.INSTANCE.clothToProto(clothesRepo.save(cloth)))
                .build();

        streamObserver.onNext(updateClothResponse);
        streamObserver.onCompleted();
    }

    @Override
    public void findCloth(FindClothRequest findClothRequest, StreamObserver<FindClothResponse> streamObserver){
        Cloth cloth = clothesRepo.findById(findClothRequest.getId()).orElseThrow(() -> new RuntimeException("Cloth not found"));

        FindClothResponse findClothResponse = FindClothResponse.newBuilder()
                .setCloth(MapperGrpc.INSTANCE.clothToProto(cloth))
                .build();

        streamObserver.onNext(findClothResponse);
        streamObserver.onCompleted();
    }

    @Override
    public void deleteCloth(DeleteClothRequest deleteClothRequest, StreamObserver<DeleteClothResponse> streamObserver){
        clothesRepo.delete(clothesRepo.findById(deleteClothRequest.getId()).orElseThrow(()
                -> new RuntimeException("Cloth not found")));

        DeleteClothResponse deleteClothResponse = DeleteClothResponse.newBuilder()
                .setMessage("DELETED Cloth with id: " + deleteClothRequest.getId())
                .build();

        streamObserver.onNext(deleteClothResponse);
        streamObserver.onCompleted();
    }

    @Override
    public void findClothByIsbn(FindClothByIsbnRequest findClothByIsbnRequest, StreamObserver<FindClothByIsbnResponse> streamObserver){
        FindClothByIsbnResponse findClothByIsbnResponse = FindClothByIsbnResponse.newBuilder()
                .setClothes(MapperGrpc.INSTANCE.clothToProto(clothesRepo.findByIsbn(findClothByIsbnRequest.getIsbn())
                        .orElseThrow(()-> new RuntimeException("Cloth not found"))))
                .build();

        streamObserver.onNext(findClothByIsbnResponse);
        streamObserver.onCompleted();
    }
}
