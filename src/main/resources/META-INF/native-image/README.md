# GraalVM Native Image Configuration

This directory contains configuration files needed for GraalVM native image
compilation.

## Why This Is Needed

GraalVM's ahead-of-time compilation can only include code paths it can
statically analyze at build time. Since the Airline CLI framework uses
reflection to:

- Discover `@Command` annotated classes
- Instantiate command classes dynamically
- Parse and inject command-line arguments

We need to explicitly tell GraalVM which classes require reflection access.

## Configuration Files

### `reflect-config.json`

Specifies which classes, methods, and fields need to be accessible via
reflection at runtime.

**When adding new commands:**

1. Add your new command class to this file
2. Include the same reflection permissions as existing commands

Example entry:

```json
{
  "name": "com.github.louislef299.blouis.cmd.YourNewCommand",
  "allDeclaredConstructors": true,
  "allPublicConstructors": true,
  "allDeclaredMethods": true,
  "allPublicMethods": true,
  "allDeclaredFields": true,
  "allPublicFields": true
}
```

## Useful Resources

- [GraalVM Native Image Reflection Documentation](https://www.graalvm.org/latest/reference-manual/native-image/dynamic-features/Reflection/)
- [Native Image Configuration Files](https://www.graalvm.org/latest/reference-manual/native-image/overview/BuildConfiguration/)
- [Airline CLI Framework](https://github.com/rvesse/airline)

## Debugging Native Image Issues

If you get runtime errors like "Unable to create instance", the class likely
needs reflection configuration. Add it to `reflect-config.json` and rebuild with
`make native`.
