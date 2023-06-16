package org.nsu.fit.golenko_dmitriy.tdc.model;

public record GameData(Entity mainTower, Road road, int DefeatedEnemy) {}

// CR: dto
//record GameObjects(RoadObject road) {}
//
//record RoadObject(int length, List<EntityObject> entityObjects) {}
//
//record EntityObject(int pos, EntityType type) {}
//
//enum EntityType {
//    MAIN,
//    ENEMY,
//    ALLY
//}