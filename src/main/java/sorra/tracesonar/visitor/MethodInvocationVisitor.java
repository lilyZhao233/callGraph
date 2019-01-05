package sorra.tracesonar.visitor;


import org.eclipse.jdt.core.dom.*;
import sorra.tracesonar.model.ExceptionBean;
import sorra.tracesonar.model.Method;
import sorra.tracesonar.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodInvocationVisitor extends ASTVisitor{
    MethodDeclaration methodDeclaration;
    Method method;

    public MethodInvocationVisitor( MethodDeclaration node,Method method){
        this.methodDeclaration=node;
        this.method = method;
    }

    /**
     * 遍历方法调用
     * @param node
     */
    @Override
    public void endVisit(MethodInvocation node) {
        getExceptions(node);
        super.endVisit(node);
    }

    /**
     * 遍历改方法里面所有的new 对象的语句
     *  @param node
     */
    @Override
    public void endVisit(ClassInstanceCreation node){
        getExceptions(node);
        super.endVisit(node);
    }

    /**
     * 对node进行解析获得解析后我们所要的相关属性
     * @param node
     */
    private void getExceptions(ASTNode node){
        IMethodBinding m=null;
        if(node instanceof MethodDeclaration){
            m = ((MethodDeclaration)node).resolveBinding();
        }
        else if (node instanceof MethodInvocation) {
            m = ((MethodInvocation) node).resolveMethodBinding();
        }
        else if(node instanceof ClassInstanceCreation){
            m=((ClassInstanceCreation) node).resolveConstructorBinding();
        }
        if(m!=null){
            ITypeBinding exceptionTypes[]= m.getExceptionTypes();
            Map<String,String> temp = new HashMap<>();
            if(exceptionTypes.length>0){
                for(ITypeBinding iTypeBinding:exceptionTypes){

                    String thrown=iTypeBinding.getPackage().getName()+"."+iTypeBinding.getName();
                    boolean hasType=false;
//                    method.setrMethod(m.getDeclaringClass().getPackage().getName()+"/"+m.getDeclaringClass().getTypeDeclaration().getName()+'#'+m.getName());
//                    method.setrException(thrown);
                    String keys = m.getDeclaringClass().getPackage().getName().replace('.','/')+"/"+m.getDeclaringClass().getTypeDeclaration().getName()+'#'+m.getName();
//                    String keys = thrown;
                    ASTNode node1=node.getParent();
                    List<String> eStrings = new ArrayList<String>();
                    ITypeBinding iTypeBinding1 = iTypeBinding;
                    while (//!iTypeBinding.getName().toString().equals("Exception")&&
                            !iTypeBinding1.getName().toString().equals("Throwable")) {
                        if ( iTypeBinding1.getSuperclass()!=null) {
                            iTypeBinding1 = iTypeBinding1.getSuperclass();
                            eStrings.add(iTypeBinding1.getName().toString());
                            continue;
                        }
                        break;
                    }
//                    exceptionBean.setParents(eStrings);
                    //判断离这个语句最近的语句的类型
                    while (!(node1 instanceof TryStatement ||node1 instanceof MethodDeclaration)){
                        node1=node1.getParent();
                    }
                    //如果是包含在try catch块中则统计最近的catch块里的内容
                    if(node1 instanceof TryStatement){
                        List catchClauses=((TryStatement) node1).catchClauses();
                        for (Object catchClause : catchClauses) {
                            CatchClause catchClause1= (CatchClause) catchClause;
                            if (((CatchClause) catchClause).getException().getType().toString().equals(iTypeBinding.getName().toString()) ||
                                    eStrings.contains(((CatchClause) catchClause).getException().getType().toString())) {//已修改
                                hasType = true;
                                if (catchClause1.getBody().toString().contains("throw new")) {
//                                    method.seteType("Rethrow");
                                    temp.put(keys ," Rethrow");
                                } else if (catchClause1.getBody()==null
                                        || catchClause1.getBody().toString().contains("log")
                                        || catchClause1.getBody().toString().contains("LOG")
                                        || catchClause.toString().contains("Log")
                                        || StringUtil.replaceBlank(catchClause1.getBody().toString()).equals("{}")) {
//                                    method.seteType("Ignore_Log");
                                    temp.put(keys," Ignore_Log");
                                } else {
//                                    method.seteType("Recover");
                                    temp.put(keys," Recover");
                                }
                            }
                        }
                        node1=node1.getParent();
                        while (!(node1 instanceof TryStatement
                                ||node1 instanceof ForStatement
                                ||node1 instanceof WhileStatement
                                ||node1 instanceof DoStatement
                                ||node1 instanceof SwitchStatement
                                ||node1 instanceof ThrowStatement
                                ||node1 instanceof IfStatement
                                ||node1 instanceof MethodDeclaration)){
                            node1=node1.getParent();
                        }
                    }
                    if(!hasType){
//                        method.seteType("only_throws");
                        temp.put(keys," only_throws");
                        node1=node.getParent();
                        while (!(node1 instanceof TryStatement
                                ||node1 instanceof ForStatement
                                ||node1 instanceof WhileStatement
                                ||node1 instanceof DoStatement
                                ||node1 instanceof SwitchStatement
                                ||node1 instanceof ThrowStatement
                                ||node1 instanceof IfStatement
                                ||node1 instanceof MethodDeclaration)){
                            node1=node1.getParent();
                        }
                    }

                }
                method.setInfo(temp);
                method.setHasParser(true);
            }

        }
    }
}
