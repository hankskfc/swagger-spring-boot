package com.chinac.doc.sdk.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import java.util.concurrent.atomic.AtomicBoolean;

import io.swagger.models.Swagger;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

public class SwaggerJsonBootstrap implements SmartLifecycle, ApplicationContextAware {
    private AtomicBoolean initialized = new AtomicBoolean(false);
    private ApplicationContext applicationContext;

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        callback.run();
    }

    @Override
    public void start() {
        if (initialized.compareAndSet(false, true)) {

            final DocumentationCache documentationCache = applicationContext.getBean(DocumentationCache.class);
            final ServiceModelToSwagger2Mapper mapper = applicationContext.getBean(ServiceModelToSwagger2Mapper.class);
            final JsonSerializer jsonSerializer = applicationContext.getBean(JsonSerializer.class);
            final DocumentationPluginsBootstrapper bootstrap = applicationContext.getBean(DocumentationPluginsBootstrapper.class);

            final SwaggerJsonHandler jsonHandler = applicationContext.getBean(SwaggerJsonHandler.class);

            if (documentationCache == null || mapper == null || jsonHandler == null || jsonSerializer == null || bootstrap == null) {
                return;
            }

            if (!jsonHandler.isRemoteEnable()) {
                return;
            }
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean running = bootstrap.isRunning();

                    while (!running) {
                        try {
                            Thread.sleep(1000);

                            running = bootstrap.isRunning();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Documentation documentation = documentationCache.documentationByGroup(Docket.DEFAULT_GROUP_NAME);

                    Swagger swagger = mapper.mapDocumentation(documentation);
                    swagger.setHost(String.format("%s:%s", jsonHandler.getServiceHost(), jsonHandler.getServicePort()));

                    Json json = jsonSerializer.toJson(swagger);

                    jsonHandler.setJson(json.value());
                    jsonHandler.run();
                }
            });
            t.start();
        }
    }

    @Override
    public void stop() {
        initialized.getAndSet(false);
    }

    @Override
    public boolean isRunning() {
        return initialized.get();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
    }
}
