package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.JWTAuthHandler;

import java.util.Base64;

public class MainVerticle extends AbstractVerticle {

    static class BasicAuthenticationCredentials {

        public String username;
        public String password;

        static BasicAuthenticationCredentials extract(HttpServerRequest request) {
            String authorization = request.headers().get("authorization");
            if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
                String[] result = new String(Base64.getDecoder().decode(
                    authorization.substring("basic".length()).trim()
                )).split(":");
                if (result.length == 2) {
                    BasicAuthenticationCredentials credentials = new BasicAuthenticationCredentials();
                    credentials.username = result[0];
                    credentials.password = result[1];
                    return credentials;
                }
            }
            return null;
        }

    }

    @Override
    public void start() {

        JWTAuth jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                .setAlgorithm("HS256")
                .setBuffer("my secret")));

        JWTAuthHandler jwtAuthHandler = JWTAuthHandler.create(jwtAuth);

        Vertx vertx = Vertx.vertx();

        Router router = Router.router(vertx);

        router.get("/").handler(context -> context.response().end("Welcome!"));

        router.get("/hello").handler(context -> {
            String name = context.queryParams().contains("name") ? context.queryParams().get("name") : "unknown";
            context.response().end(String.format("Hello %s!", name));
        });

        router.get("/secret")
            .handler(jwtAuthHandler)
            .handler(context -> {
                context.response().end("Some secret stuff ...");
            });

        router.post("/tokens").handler(context -> {
            BasicAuthenticationCredentials credentials = BasicAuthenticationCredentials.extract(context.request());
            if (credentials != null && credentials.username.equals("hans") && credentials.password.equals("froggo")) {
                JsonObject claims = new JsonObject();
                JWTOptions jwtOptions = new JWTOptions()
                    .setAlgorithm("HS256")
                    .setExpiresInMinutes(10_080) // 7 days
                    .setSubject(credentials.username);
                String token = jwtAuth.generateToken(claims, jwtOptions);
                context.response().putHeader("Content-Type", "application/jwt").end(token);
            }
            context.fail(401);
        });

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8000)
            .onSuccess(server ->
                System.out.printf("HTTP server started on port %d.%n", server.actualPort())
            );

    }

}
