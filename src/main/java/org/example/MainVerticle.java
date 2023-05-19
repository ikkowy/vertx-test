package org.example;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.createHttpServer().requestHandler(
            req -> req.response().end("Hello world!")
        ).listen(8000);
    }

}
