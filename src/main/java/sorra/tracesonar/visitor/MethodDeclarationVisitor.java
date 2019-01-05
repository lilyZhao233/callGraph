package sorra.tracesonar.visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import sorra.tracesonar.model.Method;

public class MethodDeclarationVisitor extends ASTVisitor{
    Method method;
    public MethodDeclarationVisitor(Method method){
        this.method = method;
    }

    /**
     * 首先寻找有Exception的所有方法
     * @param node
     * @return
     */
    @Override
    public boolean visit(MethodDeclaration node) {
        if(node.getName().toString().equals(method.methodName)){
            node.resolveBinding().getMethodDeclaration().getName();//函数名
            if (node.toString().contains("Exception")) {
                //判断是不是测试文件
                MarkerAnnotationVisitor anotv = new MarkerAnnotationVisitor();
                node.accept(anotv);
                if (!anotv.test) {
                    //接下来遍历所有的方法调用和新建对象的语句，判断他们是不是抛出异常
                    MethodInvocationVisitor methodInvocationVisitor=new MethodInvocationVisitor(node,method);
                    node.accept(methodInvocationVisitor);
                }

            }
        }
        return super.visit(node);
    }


}
