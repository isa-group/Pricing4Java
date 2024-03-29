# Pricing4Java

![Static Badge](https://img.shields.io/badge/Version-3.1.0-blue)

Pricing4Java is a Java-based toolkit designed to enhance the server-side functionality of a pricing-driven SaaS by enabling the seamless integration of pricing plans into the application logic. The package provides a suite of components that are predicated on the Yaml4SaaS syntax, a specification that facilitates the definition of system's pricing and its features alongside their respective evaluation expressions, grouping them within plans and add-ons, as well as establishing usage limits.

Pricing4Java has been designed to be used with [Pricing4React](https://github.com/isa-group/Pricing4React), a frontend library that consumes the generated JWT and toggles on and off features based on the user pricing plan.

For detailed information on how to get started with Pricing4Java, advanced configurations, and integration guides, please visit our [official documentation website](https://pricing4saas-docs.vercel.app).

## Installation

Pricing4Java is built to be used with Maven. To install, simply add the following dependencies to your `pom.xml` file:

```xml
<dependencies>
    ...
    <!-- Pricing4Java -->
    <dependency>
        <groupId>io.github.isa-group</groupId>
        <artifactId>Pricing4Java</artifactId>
        <version>{version}</version>
    </dependency>
    ...
</dependencies>
```

## Contributions

This project is part of the research activities of the [ISA Group](https://www.isa.us.es/3.0/). It is still under development and should be used with caution. We are not responsible for any damage caused by the use of this software. If you find any bugs or have any suggestions, please let us know by opening an issue in the [GitHub repository](https://github.com/isa-group/Pricing4Java/issues).
