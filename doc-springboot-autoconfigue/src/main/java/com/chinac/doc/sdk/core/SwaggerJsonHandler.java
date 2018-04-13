package com.chinac.doc.sdk.core;

import com.google.gson.Gson;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.rmi.ConnectException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SwaggerJsonHandler {

    private final String serviceName;
    private final String serviceVersion;
    private final String serviceHost;
    private final int servicePort;
    private String json;
    private final String url;
    private final boolean remoteEnable;
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(3000, TimeUnit.MILLISECONDS)
            .build();

    private static Gson g = new Gson();

    public SwaggerJsonHandler(String serviceName, String serviceVersion, String url, String serviceHost, int servicePort,boolean remoteEnable) {

        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.serviceHost = serviceHost;
        this.servicePort = servicePort;
        this.url = url;
        this.remoteEnable = remoteEnable;
    }

    public void run() {
        if (StringUtils.isEmpty(this.json)) {
            return;
        }
        try {
            MetaDTO metaDTO = new MetaDTO();
            metaDTO.setMetaInfo(this.json);
            metaDTO.setServiceName(this.serviceName);
            metaDTO.setServiceVersion(this.serviceVersion);
            metaDTO.setLan("java");
            metaDTO.setSdkVersion("1.0.0.0-SNAPSHOT");

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(mediaType, g.toJson(metaDTO));
            Request request = new Request
                    .Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Response execute = client.newCall(request).execute();
            if (execute.isSuccessful()) {
                System.out.println("publish service metadata success");
            } else {
                System.out.println("publish service metadata fail");
            }

        } catch (ConnectException e) {
            System.out.println(String.format("publish service metadata ConnectException,the message is that %s", e.getLocalizedMessage()));
        } catch (IOException e) {
            System.out.println(String.format("publish service metadata IOException,the message is that %s", e.getLocalizedMessage()));
        } catch (Throwable throwable) {
            System.out.println(String.format("publish service metadata exception,the message is that %s", throwable.getLocalizedMessage()));
        }
    }

    public void setJson(String json) {
        this.json = json;

        System.out.println(String.format("find service metadata,the json is---->\r\n %s", json));
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public int getServicePort() {
        return servicePort;
    }

    public boolean isRemoteEnable() {
        return remoteEnable;
    }

    class MetaDTO {
        private String metaInfo;
        private String serviceName;
        private String serviceVersion;
        private String lan;
        private String sdkVersion;

        public String getMetaInfo() {
            return metaInfo;
        }

        public void setMetaInfo(String metaInfo) {
            this.metaInfo = metaInfo;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getServiceVersion() {
            return serviceVersion;
        }

        public void setServiceVersion(String serviceVersion) {
            this.serviceVersion = serviceVersion;
        }

        public String getLan() {
            return lan;
        }

        public void setLan(String lan) {
            this.lan = lan;
        }

        public String getSdkVersion() {
            return sdkVersion;
        }

        public void setSdkVersion(String sdkVersion) {
            this.sdkVersion = sdkVersion;
        }
    }
}
