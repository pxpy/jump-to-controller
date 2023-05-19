package me.panxin.plugin.idea.jumpcontroller.util;

import com.intellij.openapi.project.Project;
import me.panxin.plugin.idea.jumpcontroller.ControllerInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author PanXin
 * @version $ Id: MyCacheManager, v 0.1 2023/05/17 14:21 PanXin Exp $
 */
public class MyCacheManager {

    private MyCacheManager(){}

    // 缓存数据使用
    private static Map<String, List<Pair<String, ControllerInfo>>> projectCacheMap = new HashMap<>();

    public static List<Pair<String, ControllerInfo>> getCacheData(Project project) {
        String projectId = project.getBasePath(); // 以项目路径作为唯一标识符
        return projectCacheMap.get(projectId);
    }

    public static void setCacheData(Project project, List<Pair<String, ControllerInfo>> cacheData) {
        String projectId = project.getBasePath(); // 以项目路径作为唯一标识符
        projectCacheMap.put(projectId, cacheData);
    }
}