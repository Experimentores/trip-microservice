package com.crudjpa.mapping;

public interface IEntityMapper<Entity, EntityResource, CreateEntity, UpdateEntity> {
    Entity fromCreateResourceToModel(CreateEntity createModel);
    EntityResource fromModelToResource(Entity model);
    Entity fromUpdateResourceToModel(UpdateEntity updateModel);

}
