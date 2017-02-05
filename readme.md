Dynamics
========
Dynamics is a Java library for handling nested weakly-typed data in a fluent and null-safe way.

[![Build Status](https://travis-ci.org/alexheretic/dynamics.svg?branch=master)](https://travis-ci.org/alexheretic/dynamics)
[![Coverage Status](https://coveralls.io/repos/alexheretic/dynamics/badge.svg?branch=master&service=github)](https://coveralls.io/github/alexheretic/dynamics?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.alexheretic/dynamics.svg)](http://mvnrepository.com/artifact/com.github.alexheretic/dynamics)

Initially developed to help handle JSON and XML messages in a direct way without tedious and repetitive null checking, type conversion & casting. It has become a powerful & productive way of directly dealing with dynamic data without the horribleness that can typically involve.

## Weakly-Typed Nested Data Structure Handling
```java
Map nestedMap = ...
// {product={investment={investment-1=12345.33, investment-2=43213.44}, effective=2015-03-07T00:35:11}}

Dynamic.from(nestedMap)
    .get("product")
    .get("effective")
    .convert().intoLocalDateTime(); // java.time.LocalDateTime 2015-03-07T00:35:11
```

#### In Detail

An `alexh.weak.Dynamic` object is a weakly-typed, possible nested structure that allows null-safe child selection, creating the Dynamic wrapper is easy

```java
Dynamic message = Dynamic.from(nestedMap);
```

Each 'get' call returns a non-null Dynamic instance that represents the child with that key, or the key's absence. Finally the wrapped object target can be accessed with `asObject()`, or `as(Class)` to cast.

```java
Dynamic investment1 = message.get("product").get("investment").get("investment-1"); // null & exception safe
investment1.isPresent(); // true
investment1.as(BigDecimal.class); // 12345.33, assuming it is a BigDecimal
```

This allows a single call to `isPresent()` to indicate if the required value exists.

```java
message.get("product").get("one").get("two").get("three").get("four").isPresent(); // false

```

With String keys we can fetch deeply in a single get call by splitting the string into multiple gets and supplying a splitter string. In addition to `isPresent()` method a Dynamic my be unpacked into an Optional with `asOptional()` or can be wrapped into an `OptionalWeak` and handled fluently with `maybe()`

```java
message.get("product.investment.investment-2", ".").maybe().as(BigDecimal.class);
// Optional[43213.44]
```

For '.' character splitting `Dynamic#dget(String)` method is supplied for convenience
```java
message.dget("product.investment.investment-2").maybe().as(BigDecimal.class);
// Optional[43213.44]
```

#### Error Describing

Dynamic instances throw descriptive exceptions when data is missing, or not as selected.
```java
// exception message:
// "'holdings' key is missing in path root->product->*holdings*->foo->bar,
//   available root->product: Map[effective, investment]"
message.dget("product.holdings.foo.bar").asObject(); // throws
```
#### Type Conversion

Aiding permissive reading (desired in servers consuming external messages) Dynamics provides common usage type runtime conversions with the `alexh.weak.Converter` class.
```java
Converter.convert("1234.4321").intoDecimal(); // BigDecimal 1234.4321
Converter.convert(1234.4321d).intoDecimal(); // BigDecimal 1234.4321 (approx)
```
Usage is also built into Dynamic instances.
```java
message.get("product").get("effective").convert().intoLocalDateTime();
// LocalDateTime of 2015-03-07T00:35:11
message.get("product").get("effective").maybe().convert().intoLocalDateTime();
// Optional<LocalDateTime>[2015-03-07T00:35:11]
```

### XML Dynamics

XML documents can be wrapped in an `XmlDynamic` which acts like a normal dynamic with string keys & values but with some special features
```xml
<product>
  <investment id="inv-1">
    <info>
      <current>
        <name>some name</name>
      </current>
    </info>
  </investment>
  <investment id="inv-2" />
</product>
```
We can select the nested 'name' element value just like a normal `Dynamic`
```java
Dynamic xml = new XmlDynamic(xmlMessage); // 'xmlMessage' is a string of the above xml
xml.get("product.investment.info.current.name", ".").asString(); // "some name"
```
Also since XML has certain key name restrictions the pipe character '|' can be used as a splitter without declaration
```java
xml.get("product|investment|info|current|name").asString(); // "some name"
```
Attributes can be accessed in exactly the same way as elements, or explicitly
```java
xml.get("product|investment|id").asString(); // "inv-1"
xml.get("product|investment|@id").asString(); // "inv-1"
```
Multiple child elements with the same local-name effectively have [i] appended to them where i is their index
```java
xml.get("product|investment|id").asString(); // "inv-1"
xml.get("product|investment[0]|id").asString(); // "inv-1"
xml.get("product|investment[1]|id").asString(); // "inv-2"
```
Namespaces are ignored by default, but can be used explicitly using the "::" key separator
```xml
<ex:product xmlns:ex="http://example.com/example">
  <message>hello</message>
</ex:product>
```
```java
xmlWithNamespaces.get("product|message").asString();
xmlWithNamespaces.get("http://example.com/example::product|none::message").asString();
// both return "hello"
```
<br/>

Dynamics is licensed under the [Apache 2.0 licence](http://www.apache.org/licenses/LICENSE-2.0.html).

### Releases

4.0 is the latest release, available at maven central. Requiring JDK 1.8 or later.

```xml
<dependency>
  <groupId>com.github.alexheretic</groupId>
  <artifactId>dynamics</artifactId>
  <version>4.0</version>
</dependency>
```

### Changelog
Release 4.x
* Add enhanced ClassCastException messages

Release 3.x
* Add Dynamic#allChildren(), #allChildrenDepthFirst(), #allChildrenBreadthFirst() deep child streaming
* Add XmlDynamic.hasElementName(String) namespace support
* 3.1
 *  Prevent rare XmlDynamic thread safety issues

Release 2.x
* `java.util.Collection` instance support, ie Sets now have children
* Minor format change to error messages
* 2.1
 - Fix conversions of some zoned iso strings into `ZonedDateTime` instances
* 2.2
 - Improve `java.sql.Timestamp` style toString date times to properly convert with nanosecond accuracy
* 2.3
 - Fix thread-safety issue with XmlDynamic#children() traversal
