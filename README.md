**Imperative versus Declarative Collection Processing:  
An RCT on the Understandability of Traditional Loops versus the Stream API in Java**

GUI application for conduction of the RCT presented in the [ICSE 2022 paper](https://conf.researchr.org/details/icse-2022/icse-2022-papers/54/Imperative-versus-Declarative-Collection-Processing-An-RCT-on-the-Understandability-)


## Build & Run

The project is setup with Maven. Run the following in the base directory to generate an executable:

```bash
mvn clean package
```

Afterwards you can launch the application with either batch a or b via:

```bash
./target/jlink-image/bin/launcher {a|b}
```

Results will be written to a CSV file in the execution directory.