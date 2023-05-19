package me.panxin.plugin.idea.jumpcontroller.util;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import me.panxin.plugin.idea.jumpcontroller.ControllerInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.panxin.plugin.idea.jumpcontroller.enumclass.SpringRequestMethodAnnotation.REQUEST_MAPPING;

/**
 * @author PanXin
 * @version $ Id: JavaSourceFileUtil, v 0.1 2023/05/17 10:17 PanXin Exp $
 */
public class JavaSourceFileUtil {
    public static List<PsiClass> getAllClasses(PsiPackage rootPackage, GlobalSearchScope searchScope) {
        List<PsiClass> javaFiles = new ArrayList<>();
        processPackage(rootPackage, searchScope, javaFiles);
        return javaFiles;
    }
    private static void processPackage(PsiPackage psiPackage, GlobalSearchScope searchScope, List<PsiClass> classesToCheck) {
        for (PsiClass psiClass : psiPackage.getClasses()) {
            classesToCheck.add(psiClass);
        }

        for (PsiPackage subPackage : psiPackage.getSubPackages(searchScope)) {
            processPackage(subPackage, searchScope, classesToCheck);
        }
    }
    public static String extractControllerPath(PsiClass psiClass) {
        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            String annotationName = annotation.getQualifiedName();
            if (annotationName != null && annotationName.equals("org.springframework.web.bind.annotation.RequestMapping")) {
                PsiAnnotationParameterList parameterList = annotation.getParameterList();
                PsiNameValuePair[] attributes = parameterList.getAttributes();
                for (PsiNameValuePair attribute : attributes) {
                    String attributeName = attribute.getAttributeName();
                    if (attributeName != null && attributeName.equals("value")) {
                        PsiAnnotationMemberValue attributeValue = attribute.getValue();
                        if (attributeValue instanceof PsiLiteralExpression) {
                            Object value = ((PsiLiteralExpression) attributeValue).getValue();
                            if (value instanceof String) {
                                ControllerInfo controllerInfo = new ControllerInfo();
                                return(String) value;
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    public static ControllerInfo getValue(PsiNameValuePair[] attributes,ControllerInfo controllerInfo, String parentPath, PsiMethod method){
        for (PsiNameValuePair attribute : attributes) {
            String attributeName = attribute.getAttributeName();
            if (attributeName != null && attributeName.equals("value")) {
                PsiAnnotationMemberValue attributeValue = attribute.getValue();
                if (attributeValue instanceof PsiLiteralExpression) {
                    Object value = ((PsiLiteralExpression) attributeValue).getValue();
                    if (value instanceof String) {
                        String childName = ((String) value).startsWith("/")?(String) value:"/"+(String) value;
                        controllerInfo.setPath(parentPath+ childName);

                        // 提取Swagger注解信息
                        PsiModifierList methodModifierList = method.getModifierList();
                        PsiAnnotation swaggerAnnotation = methodModifierList.findAnnotation("io.swagger.annotations.ApiOperation");
                        if (swaggerAnnotation != null) {
                            PsiAnnotationMemberValue swaggerValue = swaggerAnnotation.findAttributeValue("value");
                            if (swaggerValue != null && swaggerValue instanceof PsiLiteralExpression) {
                                Object swaggerAnnotationValue = ((PsiLiteralExpression) swaggerValue).getValue();
                                if (swaggerAnnotationValue instanceof String) {
                                    controllerInfo.setSwaggerInfo((String) swaggerAnnotationValue);
                                }
                                PsiAnnotationMemberValue swaggerNotes = swaggerAnnotation.findAttributeValue("notes");
                                if (swaggerNotes != null && swaggerNotes instanceof PsiLiteralExpression) {
                                    Object swaggerNotesValue = ((PsiLiteralExpression) swaggerNotes).getValue();
                                    if (swaggerNotesValue instanceof String) {
                                        controllerInfo.setSwaggerNotes((String) swaggerNotesValue);
                                    }
                                }
                            }
                        }

                        return controllerInfo;
                    }
                }
            }
        }
        return controllerInfo;
    }

    public static String showResult(List<ControllerInfo> controllerInfos){
        StringBuilder message = new StringBuilder();
        // 表头信息
        int i=0;
        int numWidth = 2;  // 列宽度：序号
        int requestColumnWidth = 5;  // 列宽度：请求方法
        int pathColumnWidth = 52;  // 列宽度：Path
        int swaggerInfoColumnWidth = 25;  // 列宽度：Swagger Info
        int swaggerNotesColumnWidth = 25;  // 列宽度：Swagger Notes
        message.append(String.format("%-" + numWidth + "s", "Num")).append("\t");
        message.append(String.format("%-" + requestColumnWidth + "s", "Request")).append("\t");
        message.append(String.format("%-" + pathColumnWidth + "s", "Path")).append("\t");
        message.append(String.format("%-" + swaggerInfoColumnWidth + "s", "Swagger Info")).append("\t");
        message.append(String.format("%-" + swaggerNotesColumnWidth + "s", "Swagger Notes")).append("\n");
        for (ControllerInfo info : controllerInfos) {
            message.append(String.format("%-" + numWidth + "s", ++i)).append("\t");
            message.append(String.format("%-" + requestColumnWidth + "s", info.getRequestMethod())).append("\t");
            // 接口路径
            message.append(String.format("%-" + pathColumnWidth + "s", info.getPath())).append("\t");
            // Swagger Info
            message.append(String.format("%-" + swaggerInfoColumnWidth + "s", info.getSwaggerInfo())).append("\t");
            // Swagger Notes
            message.append(String.format("%-" + swaggerNotesColumnWidth + "s", info.getSwaggerNotes())).append("\n");
        }
        return message.toString();
    }

    public static List<ControllerInfo> scanControllerPaths(Project project) {
        PsiManager psiManager = PsiManager.getInstance(project);
        GlobalSearchScope searchScope = GlobalSearchScope.projectScope(project);
        PsiPackage rootPackage = JavaPsiFacade.getInstance(psiManager.getProject()).findPackage("");
        // 检查是否在 Dumb 模式下，以避免在项目构建期间执行代码
        if (DumbService.isDumb(project)) {
            return Collections.emptyList();
        }
        List<Pair<String, ControllerInfo>> cachedControllerInfos = MyCacheManager.getCacheData(project);
        if (CollectionUtils.isNotEmpty(cachedControllerInfos)) {
            return cachedControllerInfos.stream()
                    .map(Pair::getRight)
                    .collect(Collectors.toList());
        }
        cachedControllerInfos = new ArrayList<>();
        List<ControllerInfo> controllerInfos = new ArrayList<>();

        // 获取项目中的所有Java源文件
        List<PsiClass> javaFiles = JavaSourceFileUtil.getAllClasses(rootPackage, searchScope);

        for (PsiClass psiClass : javaFiles) {
            // 判断类是否带有@Controller或@RestController注解
            if (isControllerClass(psiClass)) {
                String parentPath = JavaSourceFileUtil.extractControllerPath(psiClass);
                // 解析类中的方法，提取接口路径和Swagger注解信息
                PsiMethod[] methods = psiClass.getMethods();
                for (PsiMethod method : methods) {
                    ControllerInfo controllerInfo = extractControllerInfo(parentPath,method);
                    if (controllerInfo != null) {
                        // 设置方法信息
                        controllerInfo.setMethod(method);
                        controllerInfos.add(controllerInfo);
                    }
                }
            }
        }

        // 将结果添加到缓存中
        cachedControllerInfos.addAll(controllerInfos.stream()
                .map(info -> Pair.of(info.getPath(), info))
                .collect(Collectors.toList()));
        MyCacheManager.setCacheData(project,cachedControllerInfos);
        return controllerInfos;
    }

    private static boolean isControllerClass(PsiClass psiClass) {
        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            String annotationName = annotation.getQualifiedName();
            if (annotationName != null && (annotationName.equals("org.springframework.stereotype.Controller")
                    || annotationName.equals("org.springframework.web.bind.annotation.RestController"))) {
                return true;
            }
        }
        return false;
    }
    private static ControllerInfo extractControllerInfo(String parentPath, PsiMethod method) {
        ControllerInfo controllerInfo = new ControllerInfo();
        PsiAnnotation[] annotations = method.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            String annotationName = annotation.getQualifiedName();
            PsiAnnotationParameterList parameterList = annotation.getParameterList();
            PsiNameValuePair[] attributes = parameterList.getAttributes();
            // 处理 @RequestMapping 注解
            if (annotationName != null && annotationName.equals( REQUEST_MAPPING.getQualifiedName())) {
                // 设置请求方法为 GET（默认值）
                controllerInfo.setRequestMethod("REQUEST");
                // 提取 method 属性值
                PsiAnnotationMemberValue methodValue = annotation.findAttributeValue("method");
                if (methodValue instanceof PsiReferenceExpression) {
                    PsiElement resolvedElement = ((PsiReferenceExpression) methodValue).resolve();
                    if (resolvedElement instanceof PsiField) {
                        String methodName = ((PsiField) resolvedElement).getName();
                        if (methodName != null) {
                            // 根据方法名称设置请求方法
                            if (methodName.equals("GET")) {
                                controllerInfo.setRequestMethod("GET");
                            } else if (methodName.equals("POST")) {
                                controllerInfo.setRequestMethod("POST");
                            } else if (methodName.equals("PUT")) {
                                controllerInfo.setRequestMethod("PUT");
                            } else if (methodName.equals("DELETE")) {
                                controllerInfo.setRequestMethod("DELETE");
                            }
                        }
                    }
                }
                return JavaSourceFileUtil.getValue(attributes, controllerInfo, parentPath, method);
            }else if (annotationName != null && (annotationName.equals("org.springframework.web.bind.annotation.GetMapping")
                    || annotationName.equals("org.springframework.web.bind.annotation.PostMapping")
                    || annotationName.equals("org.springframework.web.bind.annotation.PutMapping")
                    || annotationName.equals("org.springframework.web.bind.annotation.DeleteMapping"))) {
                // 处理其他常用注解（例如 @GetMapping、@PostMapping、@PutMapping、@DeleteMapping 等）
                // 根据注解类型设置请求方法
                if (annotationName.equals("org.springframework.web.bind.annotation.GetMapping")) {
                    controllerInfo.setRequestMethod("GET");
                } else if (annotationName.equals("org.springframework.web.bind.annotation.PostMapping")) {
                    controllerInfo.setRequestMethod("POST");
                } else if (annotationName.equals("org.springframework.web.bind.annotation.PutMapping")) {
                    controllerInfo.setRequestMethod("PUT");
                } else if (annotationName.equals("org.springframework.web.bind.annotation.DeleteMapping")) {
                    controllerInfo.setRequestMethod("DELETE");
                }
                return JavaSourceFileUtil.getValue(attributes, controllerInfo, parentPath, method);
            }
        }
        return null;
    }
}