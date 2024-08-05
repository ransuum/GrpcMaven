package org.practice.grpcmaven.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.practice.grpcmaven.models.entity.Cloth;
import protobuf.clothes.ClothesProto;

@Mapper
public interface MapperGrpc {

    MapperGrpc INSTANCE = Mappers.getMapper(MapperGrpc.class);

    @Mapping(source = "isbn", target = "isbnOfCloth", defaultExpression = "java(cloth.getIsbn() == null ? \"\" : cloth.getIsbn())")
    ClothesProto clothToProto(Cloth cloth);

    Cloth protoToCloth(ClothesProto clothesProto);
}
