package com.crudjpa.mapping;

public interface ISetValue<Entity, RelatedEntity> {
    void call(Entity entity, RelatedEntity relatedEntity);
}
