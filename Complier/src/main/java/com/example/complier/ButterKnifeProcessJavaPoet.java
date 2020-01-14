package com.example.complier;

import com.example.annotation.BindView;
import com.example.annotation.OnClick;
import com.example.complier.utils.Constants;
import com.example.complier.utils.EmptyUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
// 允许/支持的注解类型，让注解处理器处理
@SupportedAnnotationTypes({Constants.BINDVIEW_ANNOTATION_TYPES, Constants.ONCLICK_ANNOTATION_TYPES})
// 指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ButterKnifeProcessJavaPoet extends AbstractProcessor {
    private Messager messager; // Messager用来报告错误，警告和其他提示信息
    private Elements elementUtils; // Elements中包含用于操作Element的工具方法
    private Filer filer; // Filter用来创建新的源文件，class文件以及辅助文件
    private Types typeUtils; // Types中包含用于操作TypeMirror的工具方法
    private String activityName;
    Map<TypeElement, List<VariableElement>> bindViewMap = new HashMap<>();
    Map<TypeElement, List<ExecutableElement>> onClickMap = new HashMap<>();

    // 该方法主要用于一些初始化的操作，通过该方法的参数ProcessingEnvironment可以获取一些列有用的工具类
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!EmptyUtils.isEmpty(annotations)) {
            Set<? extends Element> bindViewElements = roundEnv.getElementsAnnotatedWith(BindView.class);
            Set<? extends Element> onClickElements = roundEnv.getElementsAnnotatedWith(OnClick.class);
            if (!EmptyUtils.isEmpty(bindViewElements) || !EmptyUtils.isEmpty(onClickElements)) {
                valueOfMap(bindViewElements, onClickElements);
                try {
                    createJavaFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    private void valueOfMap(Set<? extends Element> bindViewElements, Set<? extends Element> onClickElements) {
        if (!EmptyUtils.isEmpty(bindViewElements)) {
            for (Element element : bindViewElements) {
                messager.printMessage(Diagnostic.Kind.NOTE, "@BindView >>> " + element.getSimpleName());
                if (element.getKind().isField()) {
                    VariableElement variableElement = (VariableElement) element;
                    TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
                    if (bindViewMap.containsKey(typeElement)) {
                        bindViewMap.get(typeElement).add(variableElement);
                    } else {
                        List<VariableElement> list = new ArrayList<>();
                        list.add(variableElement);
                        bindViewMap.put(typeElement, list);
                    }
                }
            }
        }
        if (!EmptyUtils.isEmpty(onClickElements)) {
            for (Element element : bindViewElements) {
                messager.printMessage(Diagnostic.Kind.NOTE, "@OnClick >>> " + element.getSimpleName());
                if (element.getKind() == ElementKind.METHOD) {
                    ExecutableElement executableElement = (ExecutableElement) element;
                    TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
                    if (onClickMap.containsKey(typeElement)) {
                        onClickMap.get(typeElement).add(executableElement);
                    } else {
                        List<ExecutableElement> list = new ArrayList<>();
                        list.add(executableElement);
                        onClickMap.put(typeElement, list);
                    }
                }
            }
        }

    }

    private void createJavaFile() throws IOException {
        TypeElement viewBinderInterface = elementUtils.getTypeElement(Constants.VIEWBINDER);
        TypeElement clickListenerType = elementUtils.getTypeElement(Constants.CLICKLISTENER);
        TypeElement viewType = elementUtils.getTypeElement(Constants.VIEW);
        if (!EmptyUtils.isEmpty(bindViewMap)) {
            // 获取ViewBinder接口类型（生成类文件需要实现的接口）
            for (Map.Entry<TypeElement, List<VariableElement>> entry : bindViewMap.entrySet()) {
                ClassName className = ClassName.get(entry.getKey());
                // 实现接口泛型
                ParameterizedTypeName typeName = ParameterizedTypeName.get(ClassName.get(viewBinderInterface), className);
                // 参数体配置(MainActivity target)
                ParameterSpec parameterSpec = ParameterSpec.builder(className, Constants.TARGET_PARAMETER_NAME)
                        .addModifiers(Modifier.FINAL)
                        .build();
                // 方法配置：public void bind(MainActivity target) {
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.BIND_METHOD_NAME)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(parameterSpec);
                for (VariableElement fieldElement : entry.getValue()) {
                    String fieldName = fieldElement.getSimpleName().toString();
                    int id = fieldElement.getAnnotation(BindView.class).value();
                    methodBuilder.addStatement("$N." + fieldName + " =$N.findViewById($L)", Constants.TARGET_PARAMETER_NAME, Constants.TARGET_PARAMETER_NAME, id);
                }
                if (!EmptyUtils.isEmpty(onClickMap)) {
                    for (Map.Entry<TypeElement, List<ExecutableElement>> methodEntry : onClickMap.entrySet()) {
                        if (className.equals(ClassName.get(methodEntry.getKey()))) {
                            for (ExecutableElement methodElement : methodEntry.getValue()) {
                                /**
                                 * target.findViewById(2131165312).setOnClickListener(new DebouncingOnClickListener() {
                                 *      public void doClick(View view) {
                                 *          target.click(view);
                                 *      }
                                 * });
                                 */
                                String methodName = methodElement.getSimpleName().toString();
                                int id = methodElement.getAnnotation(OnClick.class).value();
                                methodBuilder.beginControlFlow("$N.findViewById($L).setOnClickListener(new $T()",
                                        Constants.TARGET_PARAMETER_NAME, id, ClassName.get(clickListenerType))
                                        .beginControlFlow("public void doClick($T view)", ClassName.get(viewType))
                                        .addStatement("$N." + methodName + "(view)", Constants.TARGET_PARAMETER_NAME)
                                        .endControlFlow()
                                        .endControlFlow(")")
                                        .build();

                            }
                        }
                    }
                }
                // 必须是同包（属性修饰符缺省），MainActivity$$ViewBinder
                JavaFile.builder(className.packageName(), // 包名
                        TypeSpec.classBuilder(className.simpleName() + "$ViewBinder") // 类名
                                .addSuperinterface(typeName) // 实现ViewBinder接口
                                .addModifiers(Modifier.PUBLIC) // public修饰符
                                .addMethod(methodBuilder.build()) // 方法的构建（方法参数 + 方法体）
                                .build()) // 类构建完成
                        .build() // JavaFile构建完成
                        .writeTo(filer); // 文件生成器开始生成类文件
                            }
            }
        }

        /**
         * 等于在类首@SupportedAnnotationTypes({Constants.BINDVIEW_ANNOTATION_TYPES, Constants.ONCLICK_ANNOTATION_TYPES})
         */
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        Set<String> types = new LinkedHashSet<>();
//        // 添加支持BindView注解的类型
//        types.add(BindView.class.getCanonicalName());
//        types.add(OnClick.class.getCanonicalName());
//        return types;
//    }
    }
