package org.nsu.fit.golenko_dmitriy.tdc.model.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.dto.LobbyDto;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.model.Lobby;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.dto.EntityDto;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.dto.FieldDto;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities.Entity;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities.Field;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities.Road;

public class Mapper {

    public static Entity toEntity(EntityDto entityDto) {
        return Entity.builder()
                .id(Long.parseLong(entityDto.getId()))
                .health(entityDto.getHealth())
                .name(entityDto.getName())
                .cell(entityDto.getCell())
                .build();
    }

    public static Road toRoad(String roadIdentifier, List<EntityDto> roadEntities) {
        return new Road(roadIdentifier, roadEntities.stream().map(Mapper::toEntity).toList());
    }

    public static Field toField(FieldDto fieldDto, int length) {
        Map<String, Road> map = new HashMap<>();
        fieldDto.getRoads().forEach((identifier, roadDto) -> map.put(identifier, Mapper.toRoad(identifier, roadDto)));
        return Field.builder()
                .length(length)
                .guildhall(Mapper.toEntity(fieldDto.getGuildhall()))
                .roads(map)
                .build();
    }

    public static LobbyDto toLobbyDto(Lobby lobby) {
        return LobbyDto.builder()
                .id(lobby.getId())
                .createdAt(lobby.getCreatedAt())
                .adminSessionId(lobby.getAdminSessionId())
                .members(lobby.getMembers())
                .build();
    }
}

