package io.github.clechasseur.deckr.controller;

import io.github.clechasseur.deckr.model.Shoe;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ShoeModelAssembler implements RepresentationModelAssembler<Shoe, EntityModel<Shoe>> {
    @Override
    public EntityModel<Shoe> toModel(Shoe shoe) {
        return EntityModel.of(shoe,
                linkTo(methodOn(GameController.class).getShoe(shoe.getGame().getId())).withSelfRel(),
                linkTo(methodOn(GameController.class).getGame(shoe.getGame().getId())).withRel("game"));
    }
}
