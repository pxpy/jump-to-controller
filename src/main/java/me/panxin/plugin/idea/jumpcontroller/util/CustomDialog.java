package me.panxin.plugin.idea.jumpcontroller.util;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import me.panxin.plugin.idea.jumpcontroller.ControllerInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CustomDialog extends DialogWrapper {
    private final int dialogWidth = 600;  // 自定义对话框的宽度
    private final int dialogHeight = 400; // 自定义对话框的高度
    private List<ControllerInfo> controllerInfos;

    @Override
    protected void init() {
        super.init();
        setSize(dialogWidth, dialogHeight);
    }

    public CustomDialog(List<ControllerInfo> controllerInfos) {
        super(true);
        this.controllerInfos = controllerInfos;
        init();
        setTitle("接口信息");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // 创建内容面板
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setText(JavaSourceFileUtil.showResult(controllerInfos));

        // 将文本区域放入滚动面板中
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

}
