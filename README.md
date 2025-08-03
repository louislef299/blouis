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

For the CLI, I'm planning on using [Airline][].

## Architecture: Builder Pattern

This project uses an explicit Builder pattern instead of relying on framework
"magic" for command instantiation.

### Why This Approach?

Coming from Go's explicit nature, Java's typical dependency injection and
annotation scanning can feel like debugging a black box. This pattern provides:

- **Explicit command registration** - you can see exactly which commands are
  available
- **Type safety** - compile-time validation instead of runtime reflection errors
- **Easier debugging** - clear dependency chains without framework magic
- **Better testing** - ability to create CLI instances with specific command
  subsets

### How It Works

**BlouCliBuilder** (`builder/BlouCliBuilder.java`):

```java
Cli<Runnable> cli = new BlouCliBuilder()
    .withVersionCommand()
    .withHelpCommand()
    .withAllCommands()  // or selectively add commands
    .build();
```

### Adding New Commands

1. Create your command class with `@Command` annotation
2. Add a builder method in `BlouCliBuilder`
3. Update `withAllCommands()` to include it

This keeps the codebase explicit and maintainable while still leveraging Airline's CLI parsing capabilities.

## Building & Running

```bash
mvn clean package && \
java -cp ./target/blou-1.0-SNAPSHOT.jar com.github.louislef299.blouis/App
```

[Airline]: https://rvesse.github.io/airline/
