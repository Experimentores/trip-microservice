package com.crudjpa.mapping;

public class SetIdValue<Entity extends ISetId<Id>, Id> implements ISetValue<Entity, Id> {

    @Override
    public void call(Entity entity, Id id) {
        entity.setId(id);
    }
}
