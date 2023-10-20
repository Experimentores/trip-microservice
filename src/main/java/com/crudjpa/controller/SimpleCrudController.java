package com.crudjpa.controller;

import com.crudjpa.mapping.SimpleEntityMapping;
import com.crudjpa.service.ICrudService;

public class SimpleCrudController<Entity, Id> extends CrudController<Entity, Id, Entity, Entity, Entity> {
    protected SimpleCrudController(ICrudService<Entity, Id> crudService) {
        super(crudService, new SimpleEntityMapping<>());
    }
}
