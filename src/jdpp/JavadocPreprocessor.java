package jdpp;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A generic Javadoc preprocessor ant task that takes a set of java source files, performs a
 * transformation to the class docs, then writes the result to the specified destdir.
 * <p/>
 * Subclasses determine the behavior of the transformation by implementing {@link #transformClassDoc(String, String)}.
 *
 * @author <a href="http://code.google.com/p/jdpp/">Erik Beeson</a>
 */
public abstract class JavadocPreprocessor extends Task {
	private static final Pattern COMMENT_PARAMETER = Pattern.compile("^\\s*\\* @", Pattern.MULTILINE);
	private static final Pattern COMMENT_LEADING = Pattern.compile("^\\s*\\* ?", Pattern.MULTILINE);
	private static final Pattern LINE_START = Pattern.compile("^", Pattern.MULTILINE);

	private static final String SEPARATOR = System.getProperty("line.separator");

	private final List<FileSet> filesets = new ArrayList<FileSet>();

	private String destdir;

	public void setDestdir(String destdir) {
		this.destdir = destdir;
	}

	public void addFileSet(FileSet fileset) {
		if(!filesets.contains(fileset)) {
			filesets.add(fileset);
		}
	}

	public void execute() {
		try {
			for(FileSet fileset : filesets) {
				DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
				File dir = ds.getBasedir();
				for(final String filename : ds.getIncludedFiles()) {
					CompilationUnit cu = JavaParser.parse(new File(dir, filename));
					//noinspection unchecked
					new VoidVisitorAdapter() {
						public void visit(JavadocComment n, Object arg) {
							String[] parts = COMMENT_PARAMETER.split(n.getContent(), 2);
							if(parts.length > 0) {
								String comment = COMMENT_LEADING.matcher(parts[0]).replaceAll("").trim();
								if(comment.length() > 0) {
									String md = transformClassDoc(filename, comment);
									String out = SEPARATOR + LINE_START.matcher(md).replaceAll(" * ") + SEPARATOR;
									if(parts.length > 1) {
										out += (" * @" + parts[1]);
									}
									n.setContent(out);
								}
							}
						}
					}.visit(cu, null);
					File file = new File(destdir, filename);
					//noinspection ResultOfMethodCallIgnored
					file.getParentFile().mkdirs();
					FileWriter writer = new FileWriter(file);
					writer.write(cu.toString());
					writer.flush();
					writer.close();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Transform the given Class doc. The result will replace the original Class doc in the output file.
	 * <p/>
	 * The Class doc will never be empty or null.
	 *
	 * @param filename The path of the file being transformed.
	 * @param classDoc The Class doc to transform, stripped of the standard javadoc style (leading " * ", etc).
	 * @return The transformed Class doc.
	 */
	public abstract String transformClassDoc(String filename, String classDoc);
}