package me.bytebeats.handler;

import com.intellij.ui.JBColor;
import me.bytebeats.UISettingProvider;
import me.bytebeats.tool.PinyinUtils;
import me.bytebeats.tool.StringResUtils;
import me.bytebeats.ui.AppSettingState;
import org.apache.commons.lang3.ObjectUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;

/**
 * @author bytebeats
 * @version 1.0
 * @email <happychinapc@gmail.com>
 * @github https://github.com/bytebeats
 * @created on 2020/8/29 16:21
 * @description AbstractHandler defines common fields and methods to ui and data operation.
 */

public abstract class AbstractHandler implements UISettingProvider {
    protected Timer timer = null;
    protected final JTable jTable;
    private final JLabel jLabel;

    //哪些列需要渲染时处理
    private final int[] numColumnIdx = {3, 4, 5, 6, 7};

    //哪些列需要渲染时处理，修改字体颜色
    private final int[] numColumnColorIdx = {3, 4, 7};

    public AbstractHandler(JTable table, JLabel label) {
        this.jTable = table;
        this.jLabel = label;
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        FontMetrics metrics = jTable.getFontMetrics(jTable.getFont());
        jTable.setRowHeight(Math.max(jTable.getRowHeight(), metrics.getHeight()));
    }

    protected void updateView() {
        SwingUtilities.invokeLater(() -> {
            restoreTabSizes();
            DefaultTableModel model = new DefaultTableModel(convert2Data(), getColumnNames());
            jTable.setModel(model);
            resetTabSize();
            updateRowTextColors();
            updateTimestamp();
        });
    }

    public abstract void load(List<String> symbols);

    public abstract Object[][] convert2Data();

    public abstract String[] getColumnNames();

    public abstract void restoreTabSizes();

    public abstract void resetTabSize();

    protected void updateRowTextColors() {
        for (int idx : numColumnIdx) {
            int columnCount = jTable.getColumnCount();
            if(idx>columnCount-1){
                continue;
            }
            jTable.getColumn(jTable.getColumnName(idx)).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    double chg = 0.0;
                    try {
//                        if(Double.parseDouble(value.toString())==0.0){
//                            value = "0.0";
//                        }
                        String chgRaw = value.toString();
                        // 涨跌列去掉百分比符号
                        if (column == 4) {
                            chgRaw = chgRaw.substring(0, chgRaw.length() - 1);
                        }
                        chg = Double.parseDouble(chgRaw);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        chg = 0.0;
                    }
                    // 改变颜色
                    boolean changeColor = false;
                    for(int colorIdx: numColumnColorIdx){
                        if(colorIdx == column){
                            changeColor = true;
                        }
                    }
                    if(changeColor) {
                        setForeground(getTextColor(chg));
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });
        }
    }

    protected void updateTimestamp() {
        jLabel.setText(String.format(StringResUtils.REFRESH_TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ofPattern(StringResUtils.TIMESTAMP_FORMATTER))));
        if (isInHiddenMode()) {
            jLabel.setForeground(JBColor.DARK_GRAY);
        } else {
            jLabel.setForeground(JBColor.RED);
        }
    }

    protected String[] handleColumnNames(String[] columnNames) {
        if (isInHiddenMode()) {
            String[] columns = new String[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                columns[i] = PinyinUtils.toPinyin(columnNames[i]);
            }
            return columns;
        } else {
            return columnNames.clone();
        }
    }

    @Override
    public boolean isInHiddenMode() {
        return AppSettingState.getInstance().isHiddenMode();
    }

    @Override
    public boolean isRedRise() {
        return AppSettingState.getInstance().isRedRise();
    }
}
