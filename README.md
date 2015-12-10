## Overview

This project contains an extension feature for the injection of arbitrary properties
into Jersey 2.x resources and providers. Properties can be obtained from
properties files or any other arbitrary source of key/value pairs.

This module has been built with Jersey 2.8, and tested with the latest (as
of this writing) 2.22.1. This means that it will not work for 2.6
(as it uses features not introduced until 2.8), for those needing to stick with Java 6.

## Maven Dependency 

```xml
<dependency>
    <groupId>com.github.psamsotha</groupId>
    <artifactId>jersey-properties</artifactId>
    <version>0.1.0</version>
<dependency>
```

## Usage

### Basic Configuration

To enable this feature, you need to register the [`JerseyPropertiesFeature`][1] with the 
Jersey application. You have the option to register it in a web.xml or in your 
`Application` subclass (generally in a Jersey application, it would be a 
`ResourceConfig`).

**ResourceConfig**

```java
public class AppConfig extends ResourceConfig {
    
    public AppConfig() {
        register(JerseyPropertiesFeature.class);
    }
}
```

**web.xml**

```xml
<init-param>
    <param-name>jersey.config.server.provider.classnames</param-name>
    <param-value>com.github.psamsotha.jersey.properties.JerseyPropertiesFeature</param-value>
<init-param>
```

This is the most basic configuration, which also requires the setting of the 
location of the properties file. The file will be located as a classpath resource.
So for instance if the resource is at the root of the classpath and is 
named `app.properties`, you just need to do

```java
public AppConfig() {
    register(JerseyPropertiesFeature.class);
    property(JerseyPropertiesFeature.RESOURCE_PATH, "app.properties");
}
```

or in a web.xml

```xml
<init-param>
    <param-name>com.github.psamsotha.jersey.properties.resourcePath</param-name>
    <param-value>app.properties</param-value>
<init-param>
```

With this basic configuration, if your `app.properties` contained the following

    some.prop=Some value.

you would be able to do any of the following injections

```java
@Path("test")
public class SomeResource {

    @Prop("some.prop")
    private String someFieldProp;

    private String someConstructorProp;

    public SomeResource(@Prop("some.prop") String someConstructorProp) {
        this.someConstructorProp = someConstructorProp;
    }

    @GET
    public String get(@Prop("some.prop") String someParamProp) {
        return someParamProp;
    }
}
```

One interesting thing to note is that this feature makes use of the functionality 
provided for the injection of parameters like `@PathParam` and `@QueryParam`.
As such, the string property values have the ability to be converted automatically
to other types. For example if you have

    int.prop=12345

then you can inject the property value as an `int` or `Integer`

```java
@GET
public String get(@Prop("int.prop") int someParamProp) {
    ...
}
```

### Custom `PropertiesProvider`

Aside from using the basic properties configuration, you can provide an 
implementation of a `PropertiesProvider`, where you will need to implement
the `getProperties()` method. This allows for properties to be added 
in any number of arbitrary ways. For example

```java
public class MorePropertiesProvider implements PropertiesProvider {

    private final Map<String, String> moreProps = new HashMap<String, String>();

    public MorePropertiesProvider() {
        moreProps.put(PROP_ONE_KEY, PROP_ONE_VALUE);
    }

    @Override
    public Map<String, String> getProperties() {
        return moreProps;
    }
}
```

Then you will need to register the feature by constructing it with the new 
properties provider (the constructor takes a varargs).

```java
public AppConfig() {
    register(JerseyPropertiesFeature(new MorePropertiesProvider());
    property(JerseyPropertiesFeature.RESOURCE_PATH, "app.properties");
}
```

With the above example, the default provider is still used, which will still 
look for the path for a properties file. If you want to disable this provider, 
just add the property to disable it.

```java
public AppConfig() {
    register(JerseyPropertiesFeature(new MorePropertiesProvider());
    property(JerseyPropertiesFeature.DISABLE_DEFAULT_PROPERTIES_PROVIDER, true);
}
```

If you fail to add the property, it will not be a problem, you are just left 
with a `WARNING`. This is just to assure that this is what you really want.

For web.xml users, it is a bit more tricky, as you can't register the properties
provider in the web.xml. My suggestion would be just to create a `Feature`, and
do all the configurations in there. For example

```java
@Provider
public class PropertiesFeature implements Feature {

    @Override
    public boolean conifgure(FeatureContext ctx) {
        ctx.register(JerseyPropertiesFeature(new MorePropertiesProvider());
        ctx.property(JerseyPropertiesFeature.DISABLE_DEFAULT_PROPERTIES_PROVIDER, true);
        return true;
    }
}
```

If you have package scanning enabled, then the class should automatically be
picked up and registered through the `@Provider` annotation. 

### Internationalization (i18n) support

This feature is purely experimental. It is turned off by default. 
I don't see many use cases for it. It works with the i18n resource bundle
feature provided from Java. You can have for instance the following bundles

    Messages.properties
    Messages_fr_FR.properties
    Messages.de_DE.properties

The feature will pick these up and create an internal map of locales and bundles.
For the different languages to take effect, the client should send an
`Accept-Languages` header. The provider for this feature will look for the locale
in the internal map and will return the associated value. 

The internal map is loaded by simply iterating through `Locale.getAvailableLocales()`
and crate a `ResourceBundle` for each `Locale`. This means that if the locale
is not supported by an associated properties file, it will use the default bundle.
Likewise, if the client requests a language for which there was no properties
file, the client will be returned the default message.

To register this feature, you should enable through the following property

```java
public AppConfig() {
    register(JerseyPropertiesFeature.class);
    property(JerseyPropertiesFeature.ENABLE_I18N, true);
    property(JerseyPropertiesFeature.RESOURCE_BUNDLE, "Messages");
}
```

With this feature enabled, all other configurations will be disabled. So you 
cannot use a the default properties provider that will look for a specified
properties file. Also you cannot register other `PropertiesProvider`s. 
You can check out the [`JerseyPropertiesFeatureI18NTest`][2] for a complete
runnable test using this feature.

One of the limitations you will find is that you cannot inject the properties
into singletons, that means singleton resource classes, and providers like
`ContainerRequestFilter`. The reason is that the locale is determined per request.

As mentioned previously, the i18n feature is purely experimental and disabled by default.
If you have use for it, and would like to see any improvements, feel free to 
contact me.

[1]: https://github.com/psamsotha/jersey-properties/blob/master/src/main/java/com/github/psamsotha/jersey/properties/JerseyPropertiesFeature.java

[2]: https://github.com/psamsotha/jersey-properties/blob/master/src/test/java/com/github/psamsotha/jersey/properties/JerseyPropertiesFeatureI18NTest.java