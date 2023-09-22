package me.panxin.plugin.idea.jumpcontroller.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import me.panxin.plugin.idea.jumpcontroller.util.JavaSourceFileUtil;
import me.panxin.plugin.idea.jumpcontroller.util.MyCacheManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author PanXin
 * @version $ Id: ControllerPathCollector, v 0.1 2023/05/16 9:42 PanXin Exp $
 */
public class RefreshCacheAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        JavaSourceFileUtil.clear();
        // 扫描项目中的Java源文件
       JavaSourceFileUtil.scanAllProjectControllerInfo();
    }

    public void refresh(){
        JavaSourceFileUtil.clear();
        // 扫描项目中的Java源文件
        JavaSourceFileUtil.scanAllProjectControllerInfo();
    }
}