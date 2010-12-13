package jdpp;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * A {@link JavadocPreprocessor} that uses <a href="http://daringfireball.net/projects/markdown/">Markdown</a>.
 * <p/>
 * Built on <a href="http://code.google.com/p/markdownj/">MarkdownJ</a>.
 *
 * @author <a href="http://code.google.com/p/jdpp/">Erik Beeson</a>
 */
public class MarkdownPreprocessor extends JavadocPreprocessor {
	private static final MarkdownProcessor markdown = new MarkdownProcessor();

	public String transformClassDoc(String filename, String classDoc) {
		// fix for case-sensitive <A> tag
		classDoc = classDoc.replace("<A HREF", "<a href").replace("</A>", "</a>");
		// fix for inline <code> tags (convert to markdown)
		classDoc = classDoc.replace("<code>", "`").replace("</code>", "` ");
		classDoc = classDoc.replace("<CODE>", "`").replace("</CODE>", "` ");
		classDoc = markdown.markdown(classDoc).trim();
		// fix for single line results getting embedded in <p> tag </p>
		if(classDoc.startsWith("<p>") && classDoc.endsWith("</p>") && (classDoc.indexOf("<p>") == classDoc.lastIndexOf("<p>"))) {
			classDoc = classDoc.substring("<p>".length(), classDoc.length() - "</p>".length());
		}
		return classDoc;
	}
}
