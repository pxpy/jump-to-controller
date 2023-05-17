package me.panxin.plugin.idea.jumpcontroller.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiUtilBase;
import me.panxin.plugin.idea.jumpcontroller.ControllerInfo;
import me.panxin.plugin.idea.jumpcontroller.util.JavaSourceFileUtil;
import me.panxin.plugin.idea.jumpcontroller.util.MyCacheManager;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author PanXin
 * @version $ Id: ControllerPathCollector, v 0.1 2023/05/16 9:42 PanXin Exp $
 */
public class SearchControllerAction extends AnAction {

    private Project project;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // 获取当前项目
        project = event.getProject();
        if (project == null) {
            return;
        }
        // 扫描项目中的Java源文件
        List<ControllerInfo> controllerInfos = JavaSourceFileUtil.scanControllerPaths(project);
        // 执行搜索
        startSearch(controllerInfos);
    }
    private void startSearch(List<ControllerInfo> controllerInfos) {
        JFrame searchFrame = new JFrame("搜索");
        searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        searchFrame.setSize(1000, 400);

        JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);

        JTextField searchField = new JTextField();
        searchField.setEditable(true); // 启用编辑功能
        searchField.setTransferHandler(new TextFieldTransferHandler()); // 设置默认的传输处理程序
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performSearch();
            }

            private void performSearch() {
                String searchText = searchField.getText().strip();
                List<ControllerInfo> searchResults = searchControllerInfos(controllerInfos, searchText);
                showControllerInfo(searchResults, resultTextArea);
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    navigateToFirstControllerCode(controllerInfos, searchField.getText());
                }
            }
        });

        JPanel contentPane = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        contentPane.add(searchPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        searchFrame.setContentPane(contentPane);
        searchFrame.setVisible(true);
    }
    static class TextFieldTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            Transferable transferable = support.getTransferable();
            try {
                String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                JTextField textField = (JTextField) support.getComponent();
                textField.setText(data);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private void showControllerInfo(List<ControllerInfo> controllerInfos, JTextArea resultTextArea) {
        resultTextArea.setText(JavaSourceFileUtil.showResult(controllerInfos));
    }


    private List<ControllerInfo> searchControllerInfos(List<ControllerInfo> controllerInfos, String searchText) {
        List<Pair<String, ControllerInfo>> cachedControllerInfos = MyCacheManager.getCacheData(project);
        if (cachedControllerInfos.isEmpty()) {
            return controllerInfos.stream()
                    .filter(info -> isMatched(info, searchText))
                    .collect(Collectors.toList());
        }

        return cachedControllerInfos.stream()
                .filter(pair -> isMatched(pair.getRight(), searchText))
                .map(Pair::getRight)
                .collect(Collectors.toList());
    }
    private void navigateToFirstControllerCode(List<ControllerInfo> controllerInfos, String searchText) {
        List<ControllerInfo> searchResults = searchControllerInfos(controllerInfos, searchText);
        if (!searchResults.isEmpty()) {
            ControllerInfo firstResult = searchResults.get(0);
            navigateToControllerCode(firstResult);
        }
    }
    private void navigateToControllerCode(ControllerInfo controllerInfo) {
        PsiFile file = controllerInfo.getMethod().getContainingFile();
        if (file instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) file;
            PsiClass[] classes = javaFile.getClasses();
            if (classes.length > 0) {
                PsiClass psiClass = classes[0];
                psiClass.navigate(true);
                // 定位到对应的方法
                PsiMethod targetMethod = controllerInfo.getMethod();
                if (targetMethod != null) {
                    int offset = targetMethod.getTextOffset();
                    Editor editor = PsiUtilBase.findEditor(file);
                    if (editor != null) {
                        editor.getCaretModel().moveToOffset(offset);
                        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
                    }
                }
            }
        }
    }
    // 添加辅助方法isMatched：
    private boolean isMatched(ControllerInfo controllerInfo, String searchText) {
        return controllerInfo.getPath().contains(searchText) ||
                (controllerInfo.getSwaggerInfo() != null && controllerInfo.getSwaggerInfo().contains(searchText)) ||
                (controllerInfo.getSwaggerNotes() != null && controllerInfo.getSwaggerNotes().contains(searchText));
    }

}