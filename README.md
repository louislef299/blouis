# blouis

blouis(pronounced "blue-y") is a Bluetooth CLI tool mostly used for
myself as a learning tool. I have no idea what this will become or if
it will be useful to anyone else, so I'm going to leave this as
generic as possible for now.

This project will start as a simple Java project, then I plan to
migrate as much as I can over to Kotlin. Since I come from a Go/C/Zig
background, I haven't gotten the full Java experience yet, so I'm
going to force myself to start as the industry started and work
towards Kotlin to hopefully gain the expertise I need.

## Building & Running

```bash
mvn clean package && \
java -cp ./target/blou-1.0-SNAPSHOT.jar net.louislefebvre.App
```
