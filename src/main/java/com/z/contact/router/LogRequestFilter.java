package com.z.contact.router;

import io.quarkus.vertx.web.RouteFilter;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Slf4j
public class LogRequestFilter {

    @RouteFilter
    void logRequestFilter(RoutingContext context) {
        log.info("Request {} {}", context.request().rawMethod(), context.request().path());
        log.info("Headers: {}", context.request().headers());
        log.info("Path parameters: {}", context.request().params());
        log.info("Query parameters: {}", context.request().query());
        context.next();
    }
}
