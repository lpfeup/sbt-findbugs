# sbt-findbugs [![Linux Build Status](https://travis-ci.org/josephearl/sbt-findbugs.svg?branch=master)](https://travis-ci.org/josephearl/sbt-findbugs) [![Windows Build Status](https://ci.appveyor.com/api/projects/status/9teh7sgp3yt000iw?svg=true)](https://ci.appveyor.com/project/JosephEarl/sbt-findbugs)

An sbt 1.x and 0.13.x plugin for running FindBugs on Java classes. For more information about FindBugs, see <http://findbugs.sourceforge.net>.

This plugin currently uses FindBugs version 3.0.1.

## Getting started

Add sbt-findbugs as a plugin in your projects `project/plugins.sbt`:

```scala
addSbtPlugin("uk.co.josephearl" % "sbt-findbugs" % "2.5.0")
```

| sbt version | sbt-findbugs version |
|-------------|----------------------|
| 1.x         | 2.5.0                |
| 0.13.x      | 2.4.3                |

sbt-findbugs is an AutoPlugin, so there is no need to modify the `build.sbt` file to enable it.

## Usage

You can run FindBugs over your Java classes with the `findbugs` task. You can run FindBugs over your Java test classes with the `test:findbugs` task.

The FindBugs report is output to `target/findbugs-report.xml` by default. This can be changed by setting the value of `findbugsReportPath`. By default `test:findbugs` outputs to `target/findbugs-test-report.xml`, this can be changed by setting the value of `findbugsReportPath in Test`.

You can define include/exclude filters either inline in the `build.sbt` or in an external XML file.

### Defining filters inline

You can include or exclude bug detection for particular classes and methods using [filters](http://findbugs.sourceforge.net/manual/filter.html) with the settings `findbugsIncludeFilters` and `findbugsExcludeFilters`.

Just use Scala inline XML for the setting, for example:

```scala
findbugsIncludeFilters := Some(<FindBugsFilter>
  <Match>
    <Class name="uk.co.josephearl.example.Example" />
  </Match>
</FindBugsFilter>)
```

### Defining filters using filter files

You can also read the filter settings from files in a more conventional way:

```scala
findbugsIncludeFilters := Some(scala.xml.XML.loadFile(baseDirectory.value / "findbugs-include-filters.xml"))
```

### Plugins

To use FindBugs plugins such as [fb-contrib](http://fb-contrib.sourceforge.net) or [find-sec-bugs](http://find-sec-bugs.github.io) use the `findbugsPluginList` setting:

```scala
libraryDependencies += "com.mebigfatguy.fb-contrib" % "fb-contrib" % "6.6.0"

findbugsPluginList += s"${ivyPaths.value.ivyHome.get.absolutePath}/cache/com.mebigfatguy.fb-contrib/fb-contrib/jars/fb-contrib-6.6.0.jar"
```

Or download the plugins to your projects `lib` directory:

```scala
findbugsPluginList += file("lib/fb-contrib-6.6.0.jar").absolutePath
```

### Running FindBugs automatically

To run FindBugs automatically after compilation add the following to your `build.sbt`:

```scala
(findbugs in Compile) := ((findbugs in Compile) triggeredBy (compile in Compile)).value
```

To run FindBugs automatically after test compilation:

```scala
(findbugs in Test) := ((findbugs in Test) triggeredBy (compile in Test)).value
```

### Failing the build

You can set FindBugs to fail the build if any bugs are found by setting `findbugsFailOnError` in your your `build.sbt`:

```scala
findbugsFailOnError := true
```

**This setting is only compatible with `findbugsReportType := Some(FindBugsReportType.Xml)` (the default) or `Some(FindBugsReportType.XmlWithMessages)`.**

### Generating an HTML report and failing the build

Although you cannot currently use `findbugsFailOnError := true` in combination with `findbugsReportType := Some(FindBugsReportType.Html)`, you can use the XSLT transformations functionality to achieve the same result:

```
findbugsReportType := Some(FindBugsReportType.XmlWithMessages)
findbugsXsltTransformations := Some(Set(FindBugsXSLTTransformation(baseDirectory(_ / "xsl" / "default.xsl").value, target(_ / "findbugs-report.html").value)))
findbugsFailOnError := true
```

### XSLT transformations

The `findbugsXsltTransformations` setting allows applying XSLT transformations to the XML report generated by FindBugs. For instance, this could be used to generate a more readable HTML report. This setting takes values of `Option[Set[FindBugsXSLTTransformation]]`, so multiple transformations can be applied.

You can set `findbugsXsltTransformations` in your `build.sbt`, for example to generate an HTML report:

```
findbugsXsltTransformations := Some(Set(FindBugsXSLTTransformation(baseDirectory(_ / "xsl" / "default.xsl").value, target(_ / "findbugs-report.html").value)))

```

**This setting is only compatible with `findbugsReportType := Some(FindBugsReportType.Xml)` (the default) or `Some(FindBugsReportType.XmlWithMessages)`.**

FindBugs comes with a number of default XSL files which you can use, these are found in [`findbugs/src/xsl`](https://github.com/JosephEarl/findbugs/tree/master/findbugs/src/xsl).

### Integration tests

If you want to run FindBugs on your integration tests add the following to your `build.sbt`:
```scala
lazy val root = (project in file(".")).configs(IntegrationTest)

Defaults.itSettings

findbugs in IntegrationTest := findbugsTask(IntegrationTest).value,
findbugsReportPath in IntegrationTest := Some(target(_ / "findbugs-integration-test-report.xml").value)
findbugsAnalyzedPath in IntegrationTest := Seq((classDirectory in IntegrationTest).value)
findbugsAuxiliaryPath in IntegrationTest := (dependencyClasspath in IntegrationTest).value.files
```

## Settings

### `findbugsReportType`
* *Description:* Optionally selects the output format for the FindBugs report.
* *Accepts:* `Some(FindBugsReportType.{Xml, XmlWithMessages, Html, PlainHtml, FancyHtml, FancyHistHtml, Emacs, Xdoc})`
* *Default:* `Some(FindBugsReportType.Xml)`

### `findbugsReportPath`
* *Description:* Target path of the report file to generate (optional).
* *Accepts:* any legal file path
* *Default:* `Some(target.value / "findbugs-report.xml")`

### `findbugsPriority`
* *Description:* Suppress reporting of bugs based on priority.
* *Accepts:* `FindBugsPriority.{Relaxed, Low, Medium, High}`
* *Default:* `FindBugsPriority.Medium`

### `findbugsEffort`
* *Description:* Decide how much effort to put into analysis.
* *Accepts:* `FindBugsEffort.{Minimum, Default, Maximum}`
* *Default:* `FindBugsEffort.Default`

### `findbugsOnlyAnalyze`
* *Description:* Optionally, define which packages/classes should be analyzed.
* *Accepts:* An option containing a `List[String]` of packages and classes.
* *Default:* `None` (meaning: analyze everything).

### `findbugsMaxMemory`
* *Description:* Maximum amount of memory to allow for FindBugs (in MB).
* *Accepts:* any reasonable amount of memory as an integer value
* *Default:* `1024`

### `findbugsAnalyzeNestedArchives`
* *Description:* Whether FindBugs should analyze nested archives or not.
* *Accepts:* `true` and `false`
* *Default:* `true`

### `findbugsSortReportByClassNames`
* *Description:* Whether the reported bug instances should be sorted by class name or not.
* *Accepts:* `true` and `false`
* *Default:* `false`

### `findbugsFailOnError`
* *Description:* Whether the build should be failed if there are any reported bug instances. **Only compatible with `findbugsReportType := Some(FindBugsReportType.Xml)` or `Some(FindBugsReportType.XmlWithMessages)`.**
* *Accepts:* `true` and `false`
* *Default:* `false`

### `findbugsIncludeFilters`
* *Description:* Optional filter file XML content defining which bug instances to include in the static analysis.
* *Accepts:* `None` and `Option[Node]`
* *Default:* `None` (no include filters).

### `findbugsExcludeFilters`
* *Description:* Optional filter file XML content defining which bug instances to exclude in the static analysis.
* *Accepts:* `None` and `Some[Node]`
* *Default:* `None` (no exclude filters).

### `findbugsAnalyzedPath`
* *Description:* The path to the classes to be analyzed.
* *Accepts:* any `sbt.Path`
* *Default:* `Seq(classDirectory in Compile value)`

### `findbugsPluginList`
* *Description:* A list of FindBugs plugins to enable, can be an absolute path to a plugin or the name of a plugin in the FindBugs optional plugins directory `~/.findbugs/optionalPlugin`.
* *Accepts:* any `Seq[String]`
* *Default:* `Seq()`

### `findbugsXsltTransformations`
* *Description:* A set of XSLT transformations to apply to the report. **Only compatible with `findbugsReportType := Some(FindBugsReportType.Xml)` or `Some(FindBugsReportType.XmlWithMessages)`.**
* *Accepts:* any `Option[Set[FindBugsXSLTTransformation]]`
* *Default:* `None`
