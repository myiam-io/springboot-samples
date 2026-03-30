# MyIAM Spring Boot Samples

[MyIAM](https://myiam.io) is a cloud-based identity and access management service. It provides authentication features such as login, signup, and token management for your applications.

This repository contains sample Spring Boot apps that demonstrate how to integrate MyIAM into a Spring Boot project.

| Sample | Description |
| --- | --- |
| [1-quickstart](./1-quickstart) | A minimal example built by following the [Spring Boot Quickstart](https://myiam.io/docs/quickstart/springboot) guide |
| [2-basic](./2-basic) | A more complete example with API proxy, user actions, and API tester dashboard |

## Getting Started

1. Navigate to a sample directory:

```bash
cd 1-quickstart
```

2. Copy `application.example.yml` to `application.yml` and fill in your credentials:

```bash
cp src/main/resources/application.example.yml src/main/resources/application.yml
```

3. Run the app:

```bash
./gradlew bootRun
```
