package org.practice.grpcmaven.service;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.practice.grpcmaven.mapper.MapperGrpc;
import org.practice.grpcmaven.models.entity.Users;
import org.practice.grpcmaven.repo.UsersRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import protobuf.users.*;

@GrpcService
@RequiredArgsConstructor
@Service
public class UsersService extends UsersServiceGrpc.UsersServiceImplBase {

    private final UsersRepo usersRepo;
    private Logger loggerFactory = LoggerFactory.getLogger(UsersService.class);

    @Override
    public void registerUser(RegisterUserRequest request, StreamObserver<RegisterUserResponse> responseObserver) {
        RegisterUserResponse registerUserResponse = RegisterUserResponse.newBuilder()
                .setUsers(MapperGrpc.INSTANCE.usersToProto(usersRepo.save(Users.builder()
                        .age(request.getAge())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .firstName(request.getFirstName())
                        .password(request.getPassword())
                        .middleName(request.getMiddleName())
                        .lastName(request.getLastName())
                        .build())))
                .build();

        responseObserver.onNext(registerUserResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<DeleteUserResponse> responseObserver) {
        Users users = usersRepo.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        usersRepo.delete(users);

        DeleteUserResponse deleteUserResponse = DeleteUserResponse.newBuilder()
                .setMessage("User with this email was deleted")
                .setEmail(request.getEmail())
                .build();

        responseObserver.onNext(deleteUserResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UpdateUserResponse> responseObserver) {
        Users users = usersRepo.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getAge() != users.getAge() && request.getAge() > 14) users.setAge(request.getAge());
        if (!request.getPassword().equals(users.getPassword())
                && !request.getPassword().trim().isEmpty() && !request.getPassword().trim().isBlank())
            users.setPassword(request.getPassword());
        if (!request.getPhone().equals(users.getPhone()) && !request.getPhone().trim().isEmpty() && !request.getPhone().trim().isBlank())
            users.setPhone(request.getPhone());

        UpdateUserResponse updateUserResponse = UpdateUserResponse.newBuilder()
                .setUsers(MapperGrpc.INSTANCE.usersToProto(usersRepo.save(users)))
                .build();

        responseObserver.onNext(updateUserResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void findByEmail(FindByEmailRequest request, StreamObserver<FindByEmailResponse> responseObserver) {
        FindByEmailResponse findByEmailResponse = FindByEmailResponse.newBuilder()
                .setUser(MapperGrpc.INSTANCE.usersToProto(usersRepo.findByEmail(request.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found"))))
                .build();
        responseObserver.onNext(findByEmailResponse);
        responseObserver.onCompleted();
    }
}
