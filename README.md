# lambda-proxy project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `lambda-proxy-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.profile=dev
```
or
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/lambda-proxy-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/lambda-proxy-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

# Terraform (TODO: policies)
```shell script
TF_LOG=DEBUG terraform import aws_lambda_function.proxyzTest arn:aws:lambda:us-east-1:161142984839:function:proxyzTest &&\
TF_LOG=DEBUG terraform import aws_api_gateway_rest_api.ProxyzTest lds80zaxhd  &&\
TF_LOG=DEBUG terraform import aws_dynamodb_table.ContactDynamo ContactMarianoLopez &&\
TF_LOG=DEBUG terraform plan -out=dev.tfplan 
```