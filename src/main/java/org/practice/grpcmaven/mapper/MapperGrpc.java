package org.practice.grpcmaven.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.practice.grpcmaven.models.entity.Clothes;
import org.practice.grpcmaven.models.entity.Orders;
import org.practice.grpcmaven.models.entity.Users;
import protobuf.clothes.ClothesProto;
import protobuf.orders.ClothesProtoInfo;
import protobuf.orders.OrderProto;
import protobuf.orders.UsersProtoInfo;
import protobuf.users.UsersProto;

import java.util.List;

@Mapper
public interface MapperGrpc {

    MapperGrpc INSTANCE = Mappers.getMapper(MapperGrpc.class);

    ClothesProto clothToProto(Clothes clothes);

    @Mapping(source = "email", target = "email")
    UsersProto usersToProto(Users users);

    List<ClothesProto> clothesToProto(List<Clothes> clothes);

    ClothesProtoInfo clothesToProtoInfo(Clothes clothes);

    UsersProtoInfo usersToProtoInfo(Users users);

    @Mapping(source = "user", target = "userInfo")
    @Mapping(source = "cloth", target = "clothes")
    OrderProto ordersToProto(Orders orders);
}
