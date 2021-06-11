package com.sobev;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;


//@SupportedAnnotationTypes(value = {"com.sobev.Anno"})
//@SupportedSourceVersion(value = SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class AnnotationProcessors extends AbstractProcessor {
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    System.out.println("Log in AnnotationProcessor.process");
    for (TypeElement typeElement : annotations) {
      System.out.println("typeElement: "+typeElement);
    }
    System.out.println("roundEnv: "+roundEnv);
    return true;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> set = new HashSet<>();
    set.add("com.sobev.Anno");
    return set;
  }
}
