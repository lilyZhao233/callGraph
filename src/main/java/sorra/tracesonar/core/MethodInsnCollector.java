package sorra.tracesonar.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.*;
import sorra.tracesonar.model.Method;
import sorra.tracesonar.util.Pair;
import sorra.tracesonar.util.StringUtil;

import static org.objectweb.asm.Opcodes.ASM5;

public class MethodInsnCollector {
  private Collection<String> ignores;


  private String className;
  private Set<String> calledClasses = new HashSet<>();

  private String topClassName;

  public MethodInsnCollector(InputStream classInput, Collection<String> ignores) {
    this.ignores = ignores;

    ClassReader classReader;
    try {
      classReader = new ClassReader(classInput);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    classReader.accept(classVisitor, 0);
  }

  public String getClassName() {
    return className;
  }

  private ClassVisitor classVisitor = new ClassVisitor(ASM5) {
    ClassMap.ClassOutline classOutline;

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      className = name;
      topClassName = StringUtil.substringBefore(className, "$");
      classOutline = new ClassMap.ClassOutline(superName, interfaces);
      ClassMap.INSTANCE.addClassOutline(className, classOutline);
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
      super.visitOuterClass(owner, name, desc);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
      super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      Method caller = new Method(className, name, desc);

      if (//(access & Opcodes.ACC_PRIVATE) == 0 // Non-private &&!isIgnore(className, name, desc)
       isIgnore(className, name, desc)) {
        //sourcePackage
        classOutline.addMethod(caller);
      }

      return new MethodVisitor(ASM5) {
        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
//          if (Strings.substringBefore(owner, "$").equals(topClassName)) { // Ignore self class calls
//            return;
//          }
//          if (isIgnore(owner, name, desc)) return;

          GreatMap.INSTANCE.getCallerCollector(new Method(owner, name, desc)).regCaller(caller);
          calledClasses.add(owner);
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
          Handle handle = findInvokedHandle(bsmArgs);
          if (handle == null) {
            return;
          }
//          if (isIgnore(handle.getOwner(), handle.getName(), handle.getDesc())) {
//            return;
//          }

          GreatMap.INSTANCE.getCallerCollector(new Method(handle.getOwner(), handle.getName(), handle.getDesc()))
              .regCaller(caller);
          calledClasses.add(handle.getOwner());
        }

        private Handle findInvokedHandle(Object[] bsmArgs) {
          for (Object arg : bsmArgs) {
            if (arg instanceof Handle) return (Handle) arg;
          }
          return null;
        }
      };
    }
  };

  private boolean isIgnore(String owner, String name, String desc) {
//    for (String pkg : IGNORE_PACKAGE) { // Ignore basic libraries
//      if (owner.startsWith(pkg)) {
//        return true;
//      }
//    }
//    for (Pair<String, String> meth : IGNORE_METHODS) { // Ignore Object methods
//      if (meth._1.equals(name) && meth._2.equals(desc)) {
//        return true;
//      }
//    }
    //定义源代码里面的包、方法
    for(String pkg: SOURCE_PACKAGE){
      if(owner.startsWith(pkg)) return true;
    }
    return ignores.stream().anyMatch(
        x -> owner.startsWith(x) && (owner.length() == x.length() || owner.charAt(x.length()) == '/')
    );
  }

  private static final Set<String> IGNORE_PACKAGE = new HashSet<>();
  static {
    IGNORE_PACKAGE.add("java/");
    IGNORE_PACKAGE.add("sun/");
    IGNORE_PACKAGE.add("com/sun/");
  }
  private static final Set<Pair<String, String>> IGNORE_METHODS = new HashSet<>();
  static {
    IGNORE_METHODS.add(Pair.of("equals", "(Ljava/lang/Object;)Z"));
    IGNORE_METHODS.add(Pair.of("hashCode", "I"));
    IGNORE_METHODS.add(Pair.of("toString", "Ljava/lang/String;"));
  }

  private static final Set<String> SOURCE_PACKAGE = new HashSet<>();
  static {
    SOURCE_PACKAGE.add("");
    SOURCE_PACKAGE.add("async/");
    SOURCE_PACKAGE.add("cal/");
    SOURCE_PACKAGE.add("chat/");
    SOURCE_PACKAGE.add("checkbox/");
    SOURCE_PACKAGE.add("colors/");
    SOURCE_PACKAGE.add("compressionFilters/");
    SOURCE_PACKAGE.add("dates/");
    SOURCE_PACKAGE.add("error/");
    SOURCE_PACKAGE.add("examples/");
    SOURCE_PACKAGE.add("filters/");
    SOURCE_PACKAGE.add("javax/");
    SOURCE_PACKAGE.add("jsp2/");
    SOURCE_PACKAGE.add("listeners/");
    SOURCE_PACKAGE.add("num/");
    SOURCE_PACKAGE.add("org/");
    SOURCE_PACKAGE.add("sessions/");
    SOURCE_PACKAGE.add("util/");
    SOURCE_PACKAGE.add("validators/");
    SOURCE_PACKAGE.add("websocket/");
  }
}
