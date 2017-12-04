## Exercise 1: prepare your project for Kotlin

In this exercise we will modify the setup of an existing Spring Boot Java project to be able to start using Kotlin. 

This project uses maven for building the application but the same concepts apply when using Gradle.

### Add Kotlin to your maven project

Prepare the maven pom.xml for Kotlin, add a maven property that defines the Kotlin version to the existing properties:
```xml
<properties>
    ...
    <kotlin.version>1.2.0</kotlin.version>
</properties>
```

Add the necessary Kotlin dependencies, we will use the Java 8+ version of the [https://kotlinlang.org/api/latest/jvm/stdlib/index.html](kotlin stdlib) 
```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib-jre8</artifactId>
    <version>${kotlin.version}</version>
</dependency>
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-reflect</artifactId>
    <version>${kotlin.version}</version>
</dependency>
```

### Add the Kotlin maven plugin

Just like with Java, you need to configure a kotlin maven (compiler) plugin for the compilation of Kotlin files. 
```xml
<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <version>${kotlin.version}</version>
    <executions>
        <execution>
            <id>compile</id>
            <phase>process-sources</phase>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
        <execution>
            <id>test-compile</id>
            <phase>test-compile</phase>
            <goals>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-allopen</artifactId>
            <version>${kotlin.version}</version>
        </dependency>
    </dependencies>
</plugin>
```
Your project is now ready for some kotlin code! Rebuild the project using maven by executing the following command:

```xml
./mvnw clean verify
```

### Convert Java to Kotlin

Lets convert some Java code to Kotlin, we will start with the BootiqueApplication.java file. You can try to do this manually but it can easily be done using IntelliJ via the menu option Code > Convert Java File to Kotlin File.

Build the project with maven (./mvnw clean verify).

You should see an error in the tests. The BootiqueApplicationTests try to bootstrap the Spring Boot application but fails with the following error:
```
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: @Configuration class 'BootiqueApplication' may not be final. 
Remove the final modifier to continue.
Offending resource: com.bootique.bootique.BootiqueApplication
```

What happened? In Kotlin all the classes are final by default, this causes an issue when using Spring (Boot). Spring needs to be able to subclass (Proxy) you configuration classes and components. In kotlin we can mark a class open so it can be inherited by other classes. 

Add the open keyword to the class definition.

```kotlin
open class BootiqueApplication
```

Build the project with maven (./mvnw clean verify), is it working?

```
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: @Bean method 'api' must not be private or final; 
change the method's modifiers to continue
Offending resource: com.bootique.bootique.BootiqueApplication
```

What happened? In Kotlin all the methods are also final by default. Since Spring wants to proxy the methods you need to declare them open as well.

This might be fine in our case where there is just one method, but consider an application with multiple configuration classes and/or bean definitions.

We can use a kotlin compiler plugin for spring application to ensure all Spring related classes and methods are defined open by default.

Add the following configuration to the kotlin-maven-plugin, just after: _&lt;version&gt;${kotlin.version}&lt;/version&gt;_

```xml
...
<version>${kotlin.version}</version>
<configuration>
    <compilerPlugins>
        <plugin>spring</plugin>
    </compilerPlugins>
    <jvmTarget>1.8</jvmTarget>
</configuration>
<executions>
...
```

Build the project with maven (./mvnw clean verify), is it working? Should be fine now! 

You can now also remove the open keyword from the BootiqueApplication class definition if it bothers you.

### Next steps

Continue with exercise-2:

```
git checkout exercise-2
```