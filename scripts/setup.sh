#!/usr/bin/env bash

set -euo pipefail

BOLD='\033[1m'
CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
RESET='\033[0m'

info()    { echo -e "${CYAN}[INFO]${RESET}  $*"; }
success() { echo -e "${GREEN}[OK]${RESET}    $*"; }
warn()    { echo -e "${YELLOW}[WARN]${RESET}  $*"; }
error()   { echo -e "${RED}[ERROR]${RESET} $*" >&2; exit 1; }

PROJECT_ROOT="${1:-$(pwd)/humanizer-java}"

if [[ "${1:-}" == "--project-root" && -n "${2:-}" ]]; then
    PROJECT_ROOT="$2"
fi

echo ""
echo -e "${BOLD}╔══════════════════════════════════════════════════════════╗${RESET}"
echo -e "${BOLD}║     HumanizerDemo Java — CI/CD Test Project Setup        ║${RESET}"
echo -e "${BOLD}╚══════════════════════════════════════════════════════════╝${RESET}"
echo ""
info "Project root: ${PROJECT_ROOT}"
echo ""

info "Checking prerequisites..."

check_command() {
    local commandName="$1"
    local installHint="$2"
    if ! command -v "$commandName" &>/dev/null; then
        error "'$commandName' not found. Install it: $installHint"
    fi
    success "$commandName found at: $(command -v "$commandName")"
}

check_command "java"   "sudo yum install java-21-amazon-corretto-devel  OR  sudo apt-get install openjdk-21-jdk"
check_command "mvn"    "sudo yum install maven  OR  sudo apt-get install maven"
check_command "git"    "sudo yum install git  OR  sudo apt-get install git"
check_command "zip"    "sudo yum install zip  OR  sudo apt-get install zip"

detectedJavaMajorVersion=$(java -version 2>&1 | head -1 | grep -oP '(?<=version ")([0-9]+)' | head -1)
if [[ "$detectedJavaMajorVersion" -lt 8 ]]; then
    error "Java 8 or higher is required. Detected major version: $detectedJavaMajorVersion"
fi
success "Java major version detected: $detectedJavaMajorVersion"

echo ""
info "Creating project directory structure..."

declare -a requiredDirectories=(
    "${PROJECT_ROOT}/src/main/java/com/humanizerdemo/service"
    "${PROJECT_ROOT}/src/test/java/com/humanizerdemo/unit"
    "${PROJECT_ROOT}/src/test/java/com/humanizerdemo/integration"
    "${PROJECT_ROOT}/jenkins"
    "${PROJECT_ROOT}/scripts"
    "${PROJECT_ROOT}/docs"
)

for directoryPath in "${requiredDirectories[@]}"; do
    mkdir -p "$directoryPath"
    echo "  created: $directoryPath"
done

success "Directory structure created."
echo ""

info "Generating Maven Wrapper (mvnw)..."
cd "${PROJECT_ROOT}"

if [[ ! -f "mvnw" ]]; then
    if command -v mvn &>/dev/null; then
        mvn wrapper:wrapper -Dmaven=3.9.6 -q 2>/dev/null || {
            warn "mvn wrapper:wrapper failed — creating mvnw fallback script..."
            cat > mvnw << 'MVNW'
#!/usr/bin/env bash
exec mvn "$@"
MVNW
            chmod +x mvnw
        }
    else
        cat > mvnw << 'MVNW'
#!/usr/bin/env bash
exec mvn "$@"
MVNW
        chmod +x mvnw
    fi
    success "Maven wrapper ready."
else
    warn "mvnw already exists — skipping."
fi

echo ""
info "Writing .gitignore..."
cat > "${PROJECT_ROOT}/.gitignore" << 'GITIGNORE'
target/
*.class
*.jar
*.war
.idea/
.vscode/
*.iml
.DS_Storex
Thumbs.db
.mvn/wrapper/maven-wrapper.jar
GITIGNORE
success ".gitignore written."

echo ""
info "Writing README.md..."
cat > "${PROJECT_ROOT}/README.md" << 'README'
# HumanizerDemo Java — CI/CD Testing Project

Combines **Continuous Delivery (Jenkins)** with:
- Unit Testing (JUnit 5 + AssertJ)
- Integration Testing (ServiceRegistry composition)
- Test Suite Optimisation (changeset-gated integration stage)
- Code Coverage (JaCoCo + HTML report)

## Quick Start

```bash
chmod +x scripts/setup.sh && ./scripts/setup.sh

./mvnw clean test -P unit-tests

./mvnw clean test -P integration-tests

./mvnw clean verify -P all-tests
```

## Java Version Compatibility

Compiled with source/target = 8.
Tested on:
- java-21-amazon-corretto
- OpenJDK Temurin 1.8.0_482

## Jenkins

Point Jenkins at this repo, set Script Path to `Jenkinsfile`.
See `docs/test-process.md` for the full test process.
README
success "README.md written."

echo ""
info "Restoring dependencies and compiling..."
./mvnw clean compile -q
success "Compile succeeded."

echo ""
info "Running unit tests..."
./mvnw test -P unit-tests --no-transfer-progress
success "Unit tests passed."

echo ""
info "Running integration tests..."
./mvnw test -P integration-tests --no-transfer-progress
success "Integration tests passed."

echo ""
info "Generating JaCoCo coverage report..."
./mvnw verify -P all-tests --no-transfer-progress -q
success "Coverage report generated at: target/coverage-report/index.html"

echo ""
if [[ ! -d ".git" ]]; then
    info "Initialising git repository..."
    git init -q
    git add .
    git commit -m "chore: initial java project scaffold" -q
    success "Git repo initialised."
else
    warn "Git repo already exists — skipping git init."
fi

echo ""
echo -e "${BOLD}╔══════════════════════════════════════════════════════════╗${RESET}"
echo -e "${BOLD}║  Setup complete! Next steps for Jenkins on EC2:          ║${RESET}"
echo -e "${BOLD}╚══════════════════════════════════════════════════════════╝${RESET}"
echo ""
echo -e "  ${CYAN}1.${RESET} Push to GitHub:"
echo -e "       git remote add origin https://github.com/YOUR_ORG/humanizer-java.git"
echo -e "       git push -u origin main"
echo ""
echo -e "  ${CYAN}2.${RESET} In Jenkins: New Item → Pipeline → Pipeline from SCM"
echo -e "       SCM: Git | Branch: */main | Script Path: Jenkinsfile"
echo ""
echo -e "  ${CYAN}3.${RESET} Required Jenkins plugins:"
echo -e "       JUnit Plugin | HTML Publisher | GitHub Plugin"
echo ""
echo -e "  ${CYAN}4.${RESET} GitHub webhook:"
echo -e "       Payload URL: http://YOUR_EC2_IP:8080/github-webhook/"
echo -e "       Events: Push, Pull request"
echo ""
echo -e "  ${CYAN}5.${RESET} See ${BOLD}docs/test-process.md${RESET} for the full test process."
echo ""
