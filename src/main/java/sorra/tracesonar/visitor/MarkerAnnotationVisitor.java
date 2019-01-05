package sorra.tracesonar.visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MarkerAnnotation;

public class MarkerAnnotationVisitor extends ASTVisitor {

    public boolean test =false;
    public boolean visit(MarkerAnnotation node) {

        if (node.getTypeName().getFullyQualifiedName().toString().contains("Test")) {
            test = true;
        }
        return true;
    }
}
