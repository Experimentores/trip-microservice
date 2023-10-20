package com.crudjpa.controller;

import com.crudjpa.mapping.IEntityMapper;
import com.crudjpa.mapping.ISetValue;
import com.crudjpa.service.ICrudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public class CrudController<Entity, Id, EntityResource, EntityCreate, EntityUpdate> {
    protected final ICrudService<Entity, Id> simpleCrudService;
    protected final IEntityMapper<Entity, EntityResource, EntityCreate, EntityUpdate> mapper;
    protected CrudController(ICrudService<Entity, Id> crudService, IEntityMapper<Entity, EntityResource, EntityCreate, EntityUpdate> mapper) {
        this.simpleCrudService = crudService;
        this.mapper = mapper;
    }

    protected ResponseEntity<List<EntityResource>> getAll() {
        try {
            List<Entity> entities = this.simpleCrudService.getAll();
            if(!entities.isEmpty())
                return ResponseEntity.ok(entities.stream().map(mapper::fromModelToResource).toList());
            return new ResponseEntity<>(List.of(), HttpStatus.NO_CONTENT);
        } catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    protected ResponseEntity<EntityResource> getById(Id id) {
        try {
            Optional<Entity> entity = this.simpleCrudService.getById(id);
            return entity.map(value -> ResponseEntity.ok(mapper.fromModelToResource(value)))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    protected ResponseEntity<EntityResource> insert(EntityCreate entity){
        try {
            Entity entityNew = this.simpleCrudService.save(mapper.fromCreateResourceToModel(entity));
            return new ResponseEntity<>(mapper.fromModelToResource(entityNew), HttpStatus.CREATED);
        } catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    protected ResponseEntity<EntityResource> update(Id id, EntityUpdate resource, ISetValue<Entity, Id> setValue) {
        try {
            Optional<Entity> entityOld = simpleCrudService.getById(id);
            if(entityOld.isEmpty())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            Entity entity = mapper.fromUpdateResourceToModel(resource);
            setValue.call(entity, id);
            simpleCrudService.save(entity);
            return ResponseEntity.ok(mapper.fromModelToResource(entity));

        } catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    protected ResponseEntity<EntityResource> delete(Id id) {
        try {
            Optional<Entity> entity = this.simpleCrudService.getById(id);
            if(entity.isEmpty())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            this.simpleCrudService.delete(id);
            return ResponseEntity.ok(mapper.fromModelToResource(entity.get()));
        } catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
