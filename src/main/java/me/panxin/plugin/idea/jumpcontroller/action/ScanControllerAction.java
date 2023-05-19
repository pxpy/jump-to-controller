package me.panxin.plugin.idea.jumpcontroller.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import me.panxin.plugin.idea.jumpcontroller.ControllerInfo;
import me.panxin.plugin.idea.jumpcontroller.util.JavaSourceFileUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author PanXin
 * @version $ Id: ControllerPathCollector, v 0.1 2023/05/16 9:42 PanXin Exp $
 */
public class ScanControllerAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // 获取当前项目
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        // 扫描项目中的Java源文件
        List<ControllerInfo> controllerInfos = JavaSourceFileUtil.scanControllerPaths(project);

        // 在插件界面展示接口路径和Swagger注解信息
        showControllerInfo(controllerInfos);
    }
    private void showControllerInfo(List<ControllerInfo> controllerInfos) {

        Messages.showMessageDialog(JavaSourceFileUtil.showResult(controllerInfos), "接口信息 "+ controllerInfos.size()+"条", Messages.getInformationIcon());
    }
}