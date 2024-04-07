# Spring javacv, opencv
A program used java-spring, javacv, opencv...

- Get some photo to train: colorImage -> greyImage -> vector in Matrix (xml file)
## Require
- Java version 21.0.2
- JDK [Corretto 21](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
- Best face detection plugin I've tried: [haarcascade_frontalface_alt2.xml](https://github.com/mitre/biqt-face/blob/master/config/haarcascades/haarcascade_frontalface_alt2.xml)
- Dependencies(contain all libraries for java cv)
```xml
<dependency>
    <groupId>org.bytedeco</groupId>
    <artifactId>javacv-platform</artifactId>
    <version>1.5.7</version>
</dependency>
<dependency>
    <groupId>org.bytedeco</groupId>
    <artifactId>opencv-platform-gpu</artifactId>
    <version>4.5.5-1.5.7</version>
</dependency>
<dependency>
    <groupId>org.bytedeco</groupId>
    <artifactId>ffmpeg-platform-gpl</artifactId>
    <version>5.0-1.5.7</version>
</dependency>
```