## Annotation Processor - CS474 HW2

Sherryl Mathew George - Iterator Pattern

Annotation processor is written for Iterator Pattern. Verifies if all annotated elements confirm to
rules established for the Iterator Pattern. Annotation are checked only when a clean build is done.

## Getting Started

The project is built using Java with Gradle. Gradle commands will work. The project is compatible with IntelliJ also.

### Prerequisites

Clone this repository to your local desktop. Make sure the cloned folder has file read/write access.

### Build

To build the project from Unix command line

For Linux run the following from project root
```
./gradlew clean && ./gradlew build
```

Running then above command runs the annoation processor and processes the annotations for all the implementations
in the project

## Running tests

For Linux run the following from project root
```
./gradlew clean && ./gradlew test
```

## Running the application

For Linux run the following from project root
```
./gradlew clean && ./gradlew run
```
Running this will run the program (not the annotation processor) and shows output in the console

### Report Link [here](report/report.pdf)

