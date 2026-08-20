# Sistema Distribuido de Gestión de Organizaciones Solidarias

Sistema distribuido desarrollado como proyecto académico para la gestión de
organizaciones solidarias, usuarios, inventario de donaciones, solicitudes,
ofertas, transferencias y eventos solidarios.

El sistema implementa comunicación mediante gRPC y mensajería asíncrona con
Apache Kafka, integrando un backend Java con una interfaz web desarrollada
en React.

## Arquitectura

El proyecto está organizado en distintos módulos que permiten separar la
lógica de negocio, la comunicación entre servicios y el procesamiento de
mensajes.

- **Backend:** lógica de negocio y acceso a datos.
- **Server RPC:** exposición de servicios mediante gRPC.
- **Kafka:** comunicación asíncrona y gestión de eventos.
- **Frontend:** interfaz web para la interacción con el sistema.

## Tecnologías

- Java
- Spring Boot
- gRPC
- Apache Kafka
- React
- MySQL
- Docker
- Maven

## Funcionalidades principales

- Gestión segura de credenciales de usuarios y roles.
- Gestión de inventario de donaciones.
- Solicitudes y ofertas de donaciones.
- Transferencias de donaciones entre organizaciones.
- Gestión de eventos solidarios.
- Asignación y baja de participantes.
- Registro de donaciones asociadas a eventos.
- Publicación y baja de eventos mediante Kafka.
- Adhesión de voluntarios a eventos externos.
- Comunicación entre servicios mediante gRPC.

## Ejecución del proyecto

### Requisitos

- Java 17 o superior
- Maven
- Node.js
- Docker
- MySQL

### Crear en Mysql una Base de datos de nombre "proyectogrpc" 

### Backend Java

En la ruta ...\trabajo rpc\ que contiene el .pom "Padre" de ServerRpc y modulo Kafka, se ejecuta: 
mvn clean compile

### Docker compose

En la ruta ...\trabajo rpc\kafka-module\src\main\resources\ definimos y levantamos los contenedores necesarios para Kafka y Zookeeper, para eso ejecutamos:
docker-compose up -d

### Generación de archivos protobuf

En la ruta ...\trabajo rpc\web_app\ se ejecuta:

Para crear archivos .pb usuario:
protoc --proto_path="src/servicios/protos" \
--plugin=protoc-gen-js="./node_modules/protoc-gen-js/bin/protoc-gen-js.exe" \
--js_out="import_style=commonjs:src/servicios" \
src/servicios/protos/usuario.proto

protoc --proto_path="src/servicios/protos" \
--plugin=protoc-gen-grpc-web="./node_modules/protoc-gen-grpc-web/bin/protoc-gen-grpc-web.exe" \
--grpc-web_out="import_style=commonjs,mode=grpcwebtext:src/servicios" \
src/servicios/protos/usuario.proto

Para crear archivos .pb donaciones:
protoc --proto_path="src/servicios/protos" \
--plugin=protoc-gen-js="./node_modules/protoc-gen-js/bin/protoc-gen-js.exe" \
--js_out="import_style=commonjs:src/servicios" \
src/servicios/protos/donaciones.proto

protoc --proto_path="src/servicios/protos" \
--plugin=protoc-gen-grpc-web="./node_modules/protoc-gen-grpc-web/bin/protoc-gen-grpc-web.exe" \
--grpc-web_out="import_style=commonjs,mode=grpcwebtext:src/servicios" \
src/servicios/protos/donaciones.proto

Los archivos generados estarán en la ruta web_app/src/servicios/

### Instalar dependencias del Front

Desde la ruta \web_app\ ejecutar:
npm install

### Archivo de configuración Proxy envoy

El frontend utiliza Envoy como proxy para comunicarse con los servicios
gRPC mediante gRPC-Web.

El archivo envoy.yaml debe encontrarse en la raíz de ...\web_app\. Ahi dentro ejecutamos:
envoy:
docker run -d \
--name envoy \
-p 8081:8081 \
-v "$(pwd)/envoy.yaml:/etc/envoy/envoy.yaml" \
envoyproxy/envoy:v1.30-latest \
-c /etc/envoy/envoy.yaml

### Acceder a Kafka por Locahost (Opcional)

Para visualizar los topics y mensajes de Kafka desde una interfaz web, crear el contenedor:
docker run -d \
  --name kafka-ui \
  -p 8082:8080 \
  -e KAFKA_CLUSTERS_0_NAME=local \
  -e KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=host.docker.internal:9092 \
  provectuslabs/kafka-ui

#### Orden de ejecución

Una vez completados los pasos anteriores, iniciar los componentes en el siguiente orden:

1. Levantar los cuatro contenedores de Docker necesarios para la infraestructura.
2. Iniciar el servidor desde la clase `ProyectoGrpcApplication.java`, ubicada en el módulo `ServerRpc`.
3. Desde la carpeta raíz de \web_app\, ejecutar:
npm start

La aplicación web estará disponible en localhost:3000
Al iniciar el proyecto se puede utilizar el siguiente usuario de prueba:
Usuario: presidente
Contraseña: Presidente123

Si la interfaz de Kafka UI fue configurada estará disponible en localhost:8082
