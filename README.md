# GEO_EffectSizeApp
---

A simple `java` GUI which will take a 'Series Matrix File' downloaded directly from [GEO](https://www.ncbi.nlm.nih.gov/gds/) (search for the data set you need), allow user defined sample grouping (ensure you have understood the experimental design implimented in the study) and calculate [Effect size (Cohen's d)](https://en.wikipedia.org/wiki/Effect_size) per gene. Users can then export a `csv` file containing the top 'N' (also user defined) genes and their corresponding Cohen's d values.

A pre-compiled `JAR` file can be found in [Releases]().

---

## Build and Run
### Compile

```bash
javac -d bin src/*.java
```

### Create a runnable `JAR`

```bash
jar cfm GEO_EffectSizeApp.jar MANIFEST.MF -C bin .
```

### Run the App

```bash
java -jar GEO_EffectSizeApp.jar
```
