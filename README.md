# swagger-spring-boot
swagger java springboot client。
1、无缝接入springboot 
2、支持集中式文档管理 
3、支持selfhost管理

## 1、java 接入
    pom.xml
    <dependency>
        <groupId>com.chinac.doc.sdk</groupId>
        <artifactId>doc-spring-boot-starter</artifactId>
        <version>1.0.0.0-SNAPSHOT</version>
    </dependency>

## 2、application.properties配置

    service.name=hermes #服务名
    service.version=1.0.0.0 #服务版本
    service.metaUrl=http://127.0.0.1:8080/api/add #文档存储中心地址
    swagger.basePackage=com.chinac.hermes.webhost.controller #controller的package
    swagger.description=haahahaha启发式 #该服务版本的描述（可以省略）
    swagger.author=chenhaibin #该服务作者（可以省略）
    swagger.selfhostEnable=true #是否要开启本地寄宿模式
    swagger.remoteEnable=false #是否要上传文档的JSON，默认是true：上传

## 3、注意项

    扫包需要添加"com.chinac.doc"
    @ComponentScan({"com.chinac.doc"})
