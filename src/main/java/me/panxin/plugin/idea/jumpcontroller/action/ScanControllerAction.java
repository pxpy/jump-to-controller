package me.panxin.plugin.idea.jumpcontroller.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import me.panxin.plugin.idea.jumpcontroller.ControllerInfo;
import me.panxin.plugin.idea.jumpcontroller.util.CustomDialog;
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
        List<ControllerInfo> controllerInfos = JavaSourceFileUtil.scanAllProjectControllerInfo();
        showControllerInfo(controllerInfos);

    }
    private void showControllerInfo(List<ControllerInfo> controllerInfos) {
        CustomDialog dialog = new CustomDialog(controllerInfos);
        dialog.show();
    }
}