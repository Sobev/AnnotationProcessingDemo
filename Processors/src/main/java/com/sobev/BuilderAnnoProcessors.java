package com.sobev;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.auto.service.AutoService;
import com.sobev.Builder;


/**
 * https://www.baeldung.com/java-annotation-processing-builder
 * https://www.cnblogs.com/throwable/p/9139908.html
 *  we use the RoundEnvironment instance to receive all elements annotated with the @BuilderProperty annotation.
 *  In the case of the Person class, these elements correspond to the setName and setAge methods.
 */

//@SupportedAnnotationTypes(value = "com.sobev.Builder")
//@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class BuilderAnnoProcessors extends AbstractProcessor {
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    System.out.println("BuilderAnnoProcessors");
    for (TypeElement typeElement : annotations) {
      Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(typeElement);
      Map<Boolean, List<Element>> annotatedMethods
              = annotatedElements.stream().collect(Collectors.partitioningBy(
              element -> ((ExecutableType) element.asType()).getParameterTypes().size() == 1
                      && element.getSimpleName().toString().startsWith("set")));
      List<Element> setters = annotatedMethods.get(true);
      List<Element> otherMethods = annotatedMethods.get(false);
      otherMethods.forEach(element ->
              processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                      "@Builder must be applied to a setXxx method "
                              + "with a single argument", element));
      Map<String, String> setterMap = setters.stream().collect(Collectors.toMap(
              setter -> setter.getSimpleName().toString(),
              setter -> ((ExecutableType) setter.asType())
                      .getParameterTypes().get(0).toString()
      ));
      String className = ((TypeElement) setters.get(0)
              .getEnclosingElement()).getQualifiedName().toString();
      try {
        writeBuilderFile(className, setterMap);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return true;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> set = new HashSet<>();
    set.add("com.sobev.Builder");
    return set;
  }

  private void writeBuilderFile(
          String className, Map<String, String> setterMap)
          throws IOException {
    String packageName = null;
    int lastDot = className.lastIndexOf('.');
    if (lastDot > 0) {
      packageName = className.substring(0, lastDot);
    }
    String simpleClassName = className.substring(lastDot + 1);
    String builderClassName = className + "Builder";
    String builderSimpleClassName = builderClassName
            .substring(lastDot + 1);

    JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderClassName);

    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

      if (packageName != null) {
        out.print("package ");
        out.print(packageName);
        out.println(";");
        out.println();
      }
      out.print("public class ");
      out.print(builderSimpleClassName);
      out.println(" {");
      out.println();
      out.print("    private ");
      out.print(simpleClassName);
      out.print(" object = new ");
      out.print(simpleClassName);
      out.println("();");
      out.println();
      out.print("    public ");
      out.print(simpleClassName);
      out.println(" build() {");
      out.println("        return object;");
      out.println("    }");
      out.println();
      setterMap.forEach((methodName, argumentType) -> {
        out.print("    public ");
        out.print(builderSimpleClassName);
        out.print(" ");
        out.print(methodName);

        out.print("(");

        out.print(argumentType);
        out.println(" value) {");
        out.print("        object.");
        out.print(methodName);
        out.println("(value);");
        out.println("        return this;");
        out.println("    }");
        out.println();
      });
      out.println("}");
    }
  }
}
