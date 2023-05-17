package me.panxin.plugin.idea.jumpcontroller.provider;

import com.intellij.ide.IconProvider;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;

public class FeignIconProvider extends IconProvider {
    private static final Icon FEIGN_ICON = IconLoader.getIcon("/icons/feign_icon.png");

    @Override
    public @NotNull Icon getIcon(@NotNull PsiElement element, int flags) {
        Project project = element.getProject();

        // 检查是否在 Dumb 模式下，以避免在项目构建期间执行代码
        if (DumbService.isDumb(project)) {
            return null;
        }

        // 判断元素是否为Feign接口的方法
        if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            PsiClass containingClass = method.getContainingClass();

            // 判断是否为Feign接口类
            if (containingClass != null && isFeignInterface(containingClass)) {
                return FEIGN_ICON;
            }
        }

        return null;
    }

    private boolean isFeignInterface(PsiClass psiClass) {
        // 这里可以根据自己的项目结构和约定来判断是否为Feign接口类
        // 例如，可以判断类是否带有Feign注解或命名规则等
        // 返回 true 表示是Feign接口类，返回 false 表示不是
        return true;
    }
}
