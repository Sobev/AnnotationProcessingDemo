# AnnotationProcessingDemo
 usingAnnotationProcessing technique for generating additional source files during compilation.
 (插件化注解处理API(Pluggable Annotation Processing API))
 
 # 使用指导：
  ## 1. mvn clean
  ## 2. mvn compile
  ## 3.检查Core的target文件是否生成了PersonBuilder类  如下
  
 
 使用Processors生成了以下class文件
 ```java
 package com.sobev;

public class PersonBuilder {
  private Person object = new Person();

  public PersonBuilder() {
  }

  public Person build() {
    return this.object;
  }

  public PersonBuilder setName(String value) {
    this.object.setName(value);
    return this;
  }

  public PersonBuilder setAge(Integer value) {
    this.object.setAge(value);
    return this;
  }
}
 ```
 
 ```java
 /*
 *
 *调用Main函数使用PersonBuilder  虽然ide会报红  但是运行没有问题
 *
 */
 Person person  = new PersonBuilder().setAge(25).setName("doge").build();
 System.out.println("person = Age:" + person.getAge()+" "+"Name: "+person.getName());
 ```
