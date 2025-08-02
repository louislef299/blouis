.PHONY: clean test native package

# GraalVM environment variables
GRAALVM_HOME = $(HOME)/.sdkman/candidates/java/22.0.2-graal
GRAALVM_PATH = $(GRAALVM_HOME)/bin:$(PATH)

bin:
	@echo "Installing GraalVM and building native binary..."
	@./scripts/install-graalvm.sh
	
native: bin
	@echo "Building native binary..."
	@JAVA_HOME="$(GRAALVM_HOME)" PATH="$(GRAALVM_PATH)" \
	  mvn clean package -Pnative

test:
	@mvn test

build:
	@mvn clean package

clean:
	@mvn clean
	@rm -rf target/ bin/