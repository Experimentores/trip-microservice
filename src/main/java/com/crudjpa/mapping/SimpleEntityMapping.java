package com.crudjpa.mapping;

import javax.swing.text.html.parser.Entity;

public class SimpleEntityMapping<Entity> implements IEntityMapper<Entity, Entity, Entity, Entity> {
    @Override
    public Entity fromCreateResourceToModel(Entity createModel) {
        return createModel;
    }

    @Override
    public Entity fromModelToResource(Entity model) {
       return model;
    }

    @Override
    public Entity fromUpdateResourceToModel(Entity updateModel) {
        return updateModel;
    }
}
