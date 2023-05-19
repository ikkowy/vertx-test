package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        Vertx vertx = Vertx.vertx();

        Router router = Router.router(vertx);
        router.get("/").handler(context -> context.response().end("Welcome!"));
        router.get("/hello").handler(context -> {
            String name = context.queryParams().contains("name") ? context.queryParams().get("name") : "unknown";
            context.response().end(String.format("Hello %s!", name));
        });

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8000)
            .onSuccess(server ->
                System.out.printf("HTTP server started on port %d.%n", server.actualPort())
            );
    }

}
