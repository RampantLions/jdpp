A Javadoc Preprocessor that allows writing Javadoc Class comments in Markdown syntax, then renders the Markdown into HTML before passing the source on to javadoc.

# Usage #
jdpp works as an [Apache Ant](http://ant.apache.org/) Task. It expects a `destdir` property, and nested `FileSet`s. The files in the fileset will be processed into the specified `destdir`.

To use, first define the task with [taskdef](http://ant.apache.org/manual/Tasks/taskdef.html). Make sure to update the `classpath` property to point to your jdpp jar.

```
<taskdef name="jdpp" classname="jdpp.MarkdownPreprocessor" classpath="jdpp-....jar"/>
```

To process source files, provide one or more [FileSet](http://ant.apache.org/manual/Types/fileset.html)s for input files, and a `destdir` property to output the processed files to.

The following will process all `.java` files in the `src` directory into `src-temp`:

```
<target name="jdpp">
	<jdpp destdir="src-temp">
		<fileset dir="src" includes="**/*.java"/>
	</jdpp>
</target>
```

The processed source can now be fed into the standard Javadoc tool:

```
<target name="javadoc" depends="jdpp">
	<javadoc destdir="javadoc" sourcepath="src-temp" windowtitle="Test"/>
</target>
```

Or alternative Javadoc processors such as [Doclava](http://code.google.com/p/doclava/):

```
<target name="doclava" depends="jdpp">
	<javadoc destdir="doclava" sourcepath="src-temp">
		<doclet name="com.google.doclava.Doclava">
			<param name="-hdf"/>
			<param name="project.name"/>
			<param name="Test"/>
		</doclet>
	</javadoc>
</target>
```

# Dependencies #
jdpp uses [javaparser](http://code.google.com/p/javaparser/) for parsing source files, and [MarkdownJ](http://code.google.com/p/markdownj/) for processing. Both are included in the default build. There's also a `nodeps` build that does not bundle the dependencies.