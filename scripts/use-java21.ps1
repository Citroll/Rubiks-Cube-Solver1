param(
    [Parameter(Mandatory=$true)]
    [string]$Jdk21Path,
    [string[]]$JavaArgs = @()
)

if (-not (Test-Path $Jdk21Path)) {
    Write-Error "Provided JDK path '$Jdk21Path' does not exist."
    exit 1
}

$env:JAVA_HOME = $Jdk21Path
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

Write-Host "Using JAVA_HOME=$env:JAVA_HOME"
Write-Host "Java version: $(java --version)"

# Compile
$classesDir = Join-Path $PSScriptRoot "..\build\classes"
New-Item -ItemType Directory -Force -Path $classesDir | Out-Null

Write-Host "Compiling sources..."
javac -d $classesDir --release 21 "src\rubikscube\*.java"
if ($LASTEXITCODE -ne 0) { Write-Error "Compilation failed"; exit $LASTEXITCODE }

Write-Host "Running Solver with provided arguments..."
# Default args if none supplied
if ($JavaArgs.Length -eq 0) { $JavaArgs = @("testcases\\base.txt","output.txt") }
java -cp $classesDir rubikscube.Solver @JavaArgs

exit $LASTEXITCODE
