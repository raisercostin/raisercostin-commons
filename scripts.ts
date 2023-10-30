import * as shell from "shelljs";
import yargs from "yargs";

const red = "\x1b[31m";
const green = "\x1b[32m";
const reset = "\x1b[0m";

function releasePrepareAndPerform(): void {
  releasePrepare();
  releasePerformLocal();
}

function releasePrepare(): void {
  shell.exec(
    'mvn -B release:prepare -DskipTests=true -Prelease -Darguments="-DskipTests=true -Prelease"',
  );
}

function releasePerformLocal(args?: any): void {
  const version = args.newVersion || "0.72";
  const repo = args.repo || "d:/home/raiser/work/maven-repo";
  const localMavenRepo =
    args.localMavenRepo || "c:/Users/raiser/.m2/repository";
  const groupPath = args.groupPath || "org/raisercostin";
  const artifactId = args.artifactId || "jedio";

  shell.mkdir("-p", `${repo}/${groupPath}/${artifactId}/${version}`);
  shell.cp(
    `${localMavenRepo}/${groupPath}/${artifactId}/${version}/${artifactId}-${version}*`,
    `${repo}/${groupPath}/${artifactId}/${version}/`,
  );

  // Call createChecksums for each type
  ["", ".pom", ".jar", "-javadoc.jar", "-sources.jar"].forEach((classifier) => {
    createChecksums(
      classifier,
      version,
      repo,
      localMavenRepo,
      groupPath,
      artifactId,
    );
  });

  shell.rm("-rf", `${repo}/${groupPath}/${artifactId}/${version}/*main*`);
  shell.exec(`git -C ${repo} status`);
  shell.exec(`git -C ${repo} add .`);
  shell.exec(
    `git -C ${repo} commit -m "Release ${artifactId}-${version}" || echo "ignore commit failure, proceed"`,
  );
  shell.exec(`git -C ${repo} push`);
  shell.rm("-f", "pom.xml.releaseBackup", "release.properties");
  console.log(`${green}done${reset}`);
}

function normalizePom(): void {
  const cmd =
    'mvn com.github.ekryd.sortpom:sortpom-maven-plugin:sort -Dsort.encoding=UTF-8 -Dsort.sortDependencies=scope,artifactId -Dsort.sortPlugins=artifactId -Dsort.sortProperties=true -Dsort.sortExecutions=true -Dsort.sortDependencyExclusions=artifactId -Dsort.lineSeparator="\\n" -Dsort.ignoreLineSeparators=false -Dsort.expandEmptyElements=false -Dsort.nrOfIndentSpace=2 -Dsort.indentSchemaLocation=true';
  shell.echo("executing>", cmd);
  shell.exec(cmd);
}

function createChecksums(
  classifier: string,
  version: string,
  repo: string,
  localMavenRepo: string,
  groupPath: string,
  artifactId: string,
): void {
  let file = `${repo}/${groupPath}/${artifactId}/${version}/${artifactId}-${version}${classifier}`;
  shell.rm("-f", `${file}.sha1`);
  shell.exec(`sha1sum.exe ${file} | cut -d ' ' -f 1 > ${file}.sha1`);
  shell.rm("-f", `${file}.md5`);
  shell.exec(`md5sum.exe ${file} | cut -d ' ' -f 1 > ${file}.md5`);
}

function runTest(test: string = "LocationsTest"): void {
  shell.exec(`mvn -Dtest=${test} test`);
}

const argv = yargs
  .scriptName("scripts")
  .command(
    "releasePrepareAndPerform",
    "Executes releasePrepare and releasePerformLocal",
    {},
    releasePrepareAndPerform,
  )
  .command("normalizePom", "Normalizes the POM file", {}, normalizePom)
  .command("releasePrepare", "Prepares the release", {}, releasePrepare)
  .command(
    "releasePerformLocal",
    "Performs the release locally",
    {
      newVersion: { type: "string" },
      repo: { type: "string" },
      localMavenRepo: { type: "string" },
      groupPath: { type: "string" },
      artifactId: { type: "string" },
    },
    releasePerformLocal,
  )
  .demandCommand()
  .help().argv;
