package name.jurgenei.parsers;

import org.antlr.v4.runtime.*;
import java.io.Writer;
import java.util.List;

/**
 * Shared parser helper methods referenced by semantic predicates in Java grammar.
 */
public abstract class JavaParserBase extends Parser {

    /**
     * Creates parser base bound to active token stream.
     *
     * @param input token stream consumed by parser
     */
    public JavaParserBase(TokenStream input){
	super(input);
    }

    /**
     * Validates that record component with ellipsis appears only as last component.
     *
     * @return true when placement valid or context not record components, false otherwise
     */
    public boolean DoLastRecordComponent() {
	ParserRuleContext ctx = this.getContext();
	if (!(ctx instanceof JavaParser.RecordComponentListContext)) {
	    return true; // or throw if this is an unexpected state
	}

	JavaParser.RecordComponentListContext tctx = (JavaParser.RecordComponentListContext) ctx;
	List<JavaParser.RecordComponentContext> rcs = tctx.recordComponent();
	if (rcs.isEmpty()) return true;

	int count = rcs.size();
	for (int c = 0; c < count; ++c) {
	    JavaParser.RecordComponentContext rc = rcs.get(c);
	    if (rc.ELLIPSIS() != null && c + 1 < count)
		return false;
	}
	return true;
    }

    /**
     * Checks whether next tokens are not in form {@code identifier =}.
     *
     * @return true when lookahead is not assignment to identifier-like token
     */
    public boolean IsNotIdentifierAssign()
    {
	var la = this._input.LA(1);
	    // If not identifier, return true because it can't be
	    // "identifier = ..."
	switch (la) {
	    case JavaParser.IDENTIFIER:
	    case JavaParser.MODULE:
	    case JavaParser.OPEN:
	    case JavaParser.REQUIRES:
	    case JavaParser.EXPORTS:
	    case JavaParser.OPENS:
	    case JavaParser.TO:
	    case JavaParser.USES:
	    case JavaParser.PROVIDES:
	    case JavaParser.WHEN:
	    case JavaParser.WITH:
	    case JavaParser.TRANSITIVE:
	    case JavaParser.YIELD:
	    case JavaParser.SEALED:
	    case JavaParser.PERMITS:
	    case JavaParser.RECORD:
	    case JavaParser.VAR:
		break;
	    default:
		return true;
	}
	var la2 = this._input.LA(2);
	if (la2 != JavaParser.ASSIGN) return true;
	return false;
    }
}
