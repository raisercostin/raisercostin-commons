# raisercostin-commons

![raisercostin-commons version](https://img.shields.io/badge/raisercostin--commons-2.29-blue)

## Usage
- See last version at [https://github.com/raisercostin/maven-repo > raisercostin-commons](https://github.com/raisercostin/maven-repo/tree/master/org/raisercostin/raisercostin-commons)
  ```
  <dependency>
    <groupId>org.raisercostin</groupId>
    <artifactId>jedio</artifactId>
    <version>0.1</version>
  </dependency>
  ```
- Repository
  ```
  <repository>
    <id>raisercostin-github</id>
    <url>https://raw.githubusercontent.com/raisercostin/maven-repo/master/</url>
    <snapshots><enabled>false</enabled></snapshots>
  </repository>
  ```

## Development
- To release to git https://github.com/raisercostin/maven-repo
  ```
  npm run release-prepare
  npm run release-perform-local -- --releaseVersion 2.29
  ```
