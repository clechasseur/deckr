package io.github.clechasseur.deckr.controller;

import io.github.clechasseur.deckr.model.Game;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GameModelAssembler implements RepresentationModelAssembler<Game, EntityModel<Game>> {
    @Override
    public EntityModel<Game> toModel(Game game) {
        return EntityModel.of(game,
                linkTo(methodOn(GameController.class).getGame(game.getId())).withSelfRel(),
                linkTo(methodOn(GameController.class).getShoe(game.getId())).withRel("shoe"),
                linkTo(methodOn(GameController.class).getPlayersAndValues(game.getId())).withRel("players"));
    }
}
