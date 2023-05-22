package me.panxin.plugin.idea.jumpcontroller.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import me.panxin.plugin.idea.jumpcontroller.ControllerInfo;
import me.panxin.plugin.idea.jumpcontroller.util.JavaSourceFileUtil;
import me.panxin.plugin.idea.jumpcontroller.util.MyIcons;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 行标记和跳转符号
 */
public class Feign2ControllerLineMarkerProvider extends RelatedItemLineMarkerProvider {


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (element instanceof PsiMethod && isElementWithinInterface(element)) {
            PsiMethod psiMethod = (PsiMethod) element;
            PsiClass psiClass = psiMethod.getContainingClass();
            if (psiClass != null) {
                List<PsiElement> resultList = process(psiMethod);
                if (!resultList.isEmpty()) {
                    NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
                            .create(MyIcons.STATEMENT_LINE_FEIGN_ICON)
                            .setAlignment(GutterIconRenderer.Alignment.CENTER)
                            .setTargets(resultList)
                            .setTooltipTitle("Navigation to target in Controller");
                    result.add(builder.createLineMarkerInfo(Objects.requireNonNull(psiMethod.getNameIdentifier())));
                }
            }
        }
    }

    /**
     * 目前只能跳转到当前项目下的文件否则会报Element from alien project错误
     *
     * @param psiMethod psi方法
     * @return {@link List}<{@link PsiElement}>
     */
    private List<PsiElement> process(PsiMethod psiMethod) {
        List<PsiElement> elementList = new ArrayList<>();

        // 获取当前项目
        Project project = psiMethod.getProject();

        List<ControllerInfo> controllerInfos = JavaSourceFileUtil.scanControllerPaths(project);

        if (controllerInfos != null) {
            // 遍历 Controller 类的所有方法
            for (ControllerInfo controllerInfo : controllerInfos) {
                if (isMethodMatch(controllerInfo, psiMethod)) {
                    elementList.add(controllerInfo.getMethod());
                }
            }
        }

        return elementList;
    }

    private boolean isMethodMatch(ControllerInfo controllerInfo, PsiMethod feignMethod) {
        ControllerInfo feignInfo = JavaSourceFileUtil.extractControllerInfo("", feignMethod);
        if(feignInfo != null){
            String path = feignInfo.getPath();
            if(StringUtils.isNotBlank(path)){
                return path.equals(controllerInfo.getPath());
            }
        }
        return false;
    }

    /**
     * 元素是否为FeignClient下的方法
     *
     * @param element 元素
     * @return boolean
     */
    private boolean isElementWithinInterface(PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            PsiClass psiClass = (PsiClass) element;

            // 检查类上是否存在 FeignClient 注解
            PsiAnnotation feignAnnotation = psiClass.getAnnotation("org.springframework.cloud.openfeign.FeignClient");
            if (feignAnnotation != null) {
                return true;
            }
        }
        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return type != null && isElementWithinInterface(type);
    }
}

