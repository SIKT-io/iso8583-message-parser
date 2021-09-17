![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

# ISO-8583

> A super-lightweight framework for working with iso-8583 messages in Java

## Usage

```xml
<dependency>
    <groupId>io.sikt</groupId>
    <artifactId>iso-8583</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Packager
Example (json) packager can be found in [IFSF-ASCII-1993.json](./src/main/resources/packagers/IFSF-ASCII-1993.json)

Alternatively you can create your own Java packager like this:

````java
public class DummyPackager extends BasePackager {

    public DummyPackager(Charset messageEncoding) {
        super(buildPackagerConfiguration(messageEncoding));
    }

    static PackagerConfiguration buildPackagerConfiguration(Charset messageEncoding) {
        Map<Integer, PackagerField> packagerInfo = Stream.of(new Object[][]{
            {0, new NUMERIC(4, "MTI")},
            {1, new BINARY(8, "BITMAP")},
            {7, new ALPHA(10, "DATE")},
            {11, new NUMERIC(6, "Systems trace audit number")},
            {12, new NUMERIC(12, "Date and time, Local transaction")},
            {24, new NUMERIC(3, "Function code")},
            {25, new NUMERIC(4, "Message reason code")},
            {32, new LL_VAR(11, "Acquirer institution identification code")},
            {53, new LL_VAR(48, "Security related control information")},
            {64, new BINARY(8, "MAC")}
        }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (PackagerField) data[1]));

        PackagerConfiguration configuration = new PackagerConfiguration();
        configuration.setPackagerInfo(packagerInfo);
        configuration.setMessageTypeParserGuide(Collections.singletonMap("1820", new ArrayList<>(packagerInfo.keySet())));
        configuration.setEncoding(messageEncoding);
        return configuration;
    }
}
````

## Message parsing

#### Java-packager
```java
public byte[] packIsoMsg() {
    IsoMsg msg = new IsoMsg();
    msg.setPackager(new DummyPackager(StandardCharsets.ISO_8859_1));
    msg.setMTI("1820");

    msg.setField(7, "1024103600");
    msg.setField(11, "010001");
    msg.setField(12, "171024103600");
    msg.setField(24, "831");
    msg.setField(32, "10524");

    return msg.pack();
}

public IsoMsg unpackMsg(byte[] what) {
    return new DummyPackager(StandardCharsets.ISO_8859_1)
        .unpack(expected);
}   
```

#### JSON-packager
```java
public byte[] packIsoMsg() {
    IsoMsg msg = new IsoMsg();
    msg.setPackager(new GenericPackager("src/main/resources/packagers/IFSF-ASCII-1993.json"));
    msg.setMTI("1820");

    msg.setField(7, "1024103600");
    msg.setField(11, "010001");
    msg.setField(12, "171024103600");
    msg.setField(24, "831");
    msg.setField(32, "10524");

    return msg.pack();
}

public IsoMsg unpackMsg(byte[] what) {
    return new GenericPackager("src/main/resources/packagers/IFSF-ASCII-1993.json")
        .unpack(expected);
}    
    
```
