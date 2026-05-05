# MASCOT

MASCOT (Marginal Approximation of the Structured Coalescent) is a [BEAST 3](https://github.com/CompEvol/beast3) package for efficient phylogeographic inference under the structured coalescent.

A tutorial on how to use MASCOT can be found [here](https://taming-the-beast.org/tutorials/Mascot-Tutorial/).

## Maven coordinates

```xml
<dependency>
    <groupId>io.github.compevol</groupId>
    <artifactId>mascot</artifactId>
    <version>3.1.0-beta1</version>
</dependency>
```

JPMS module name: `mascot`

## Building from source

Requires Java 25 and Maven.

```bash
mvn install -DskipTests
```

## Running BEAST with Mascot

```bash
mvn exec:exec -Dbeast.args="-overwrite examples/Constant.xml"
```

## Citation

Müller NF, Bouckaert RR, Wu C-H, Bedford T. *MASCOT-Skyline integrates population and migration dynamics to enhance phylogeographic reconstructions.* PLOS Computational Biology 21(9):e1013421, 2025. [DOI:10.1371/journal.pcbi.1013421](https://doi.org/10.1371/journal.pcbi.1013421)

Müller NF, Dudas G, Stadler T. *Inferring time-dependent migration and coalescence patterns from genetic sequence and predictor data in structured populations.* Virus Evolution 5(2):vez030, 2019. [DOI:10.1093/ve/vez030](https://doi.org/10.1093/ve/vez030)

Müller NF, Rasmussen DA, Stadler T. *MASCOT: parameter and state inference under the marginal structured coalescent approximation.* Bioinformatics 34(22):3843–3848, 2018. [DOI:10.1093/bioinformatics/bty406](https://doi.org/10.1093/bioinformatics/bty406)

Müller NF, Rasmussen DA, Stadler T. *The Structured Coalescent and Its Approximations.* Molecular Biology and Evolution 34(11):2970–2981, 2017. [DOI:10.1093/molbev/msx186](https://doi.org/10.1093/molbev/msx186)

## License

The java source code is licensed under the [GNU General Public License v3.0](LICENSE).
