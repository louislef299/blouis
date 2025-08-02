#!/bin/bash

set -e

GRAALVM_VERSION="22.0.2-graal"
SDKMAN_INIT="$HOME/.sdkman/bin/sdkman-init.sh"

echo "Installing GraalVM for native binary compilation..."

# Check if SDKMAN is installed
if [ ! -f "$SDKMAN_INIT" ]; then
    echo "Installing SDKMAN..."
    curl -s "https://get.sdkman.io" | bash
    source "$SDKMAN_INIT"
else
    echo "SDKMAN already installed"
    source "$SDKMAN_INIT"
fi

# Check if GraalVM is already installed
if sdk list java | grep -q "installed" | grep -q "$GRAALVM_VERSION"; then
    echo "GraalVM $GRAALVM_VERSION already installed"
else
    echo "Installing GraalVM $GRAALVM_VERSION..."
    sdk install java "$GRAALVM_VERSION"
fi

# Use GraalVM for this session
echo "Setting GraalVM as current Java version..."
sdk use java "$GRAALVM_VERSION"

# Verify installation
echo "GraalVM installation complete!"
java -version
echo ""
echo "To use GraalVM in future sessions, run:"
echo "   source ~/.sdkman/bin/sdkman-init.sh"
echo "   sdk use java $GRAALVM_VERSION"