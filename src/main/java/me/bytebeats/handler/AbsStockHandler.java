package me.bytebeats.handler;

import me.bytebeats.meta.Stock;
import me.bytebeats.tool.StringResUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsStockHandler {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat(StringResUtils.TIMESTAMP_FORMATTER);
    public static final long REFRESH_INTERVAL = 5L * 1000L;

    protected List<Stock> stocks = new ArrayList<>();
    private boolean isHidden = false;
    private boolean isRedRise = true;
    private JTable jTable;
    private JLabel jLabel;
    private int[] tab_sizes = {0, 0, 0, 0, 0, 0, 0, 0};
    private String[] column_names = {StringResUtils.STOCK_NAME, StringResUtils.STOCK_SYMBOL, StringResUtils.STOCK_LATEST_PRICE,
            StringResUtils.STOCK_RISE_AND_FALL, StringResUtils.STOCK_RISE_AND_FALL_RATIO, StringResUtils.STOCK_VOLUME,
            StringResUtils.STOCK_TURNOVER, StringResUtils.STOCK_MKT_VALUE};

    public AbsStockHandler(JTable table, JLabel label) {
        this.jTable = table;
        this.jLabel = label;
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        FontMetrics metrics = jTable.getFontMetrics(jTable.getFont());
        jTable.setRowHeight(Math.max(jTable.getRowHeight(), metrics.getHeight()));
    }

    public abstract void load(List<String> symbols);

    protected void updateView() {
        SwingUtilities.invokeLater(() -> {
            restoreTabSizes();
            DefaultTableModel model = new DefaultTableModel(convert2Data(), column_names);
            jTable.setModel(model);
            resetTabSize();
            updateTimestamp();
        });
    }

    private void updateTimestamp() {
        jLabel.setText(String.format(StringResUtils.REFRESH_TIMESTAMP, dateFormat.format(LocalDateTime.now())));
    }

    private void restoreTabSizes() {
        if (jTable.getColumnModel().getColumnCount() == 0) {
            return;
        }
        for (int i = 0; i < tab_sizes.length; i++) {
            tab_sizes[i] = jTable.getColumnModel().getColumn(i).getWidth();
        }
    }

    private void resetTabSize() {
        for (int i = 0; i < tab_sizes.length; i++) {
            if (tab_sizes[i] > 0) {
                jTable.getColumnModel().getColumn(i).setWidth(tab_sizes[i]);
                jTable.getColumnModel().getColumn(i).setPreferredWidth(tab_sizes[i]);
            }
        }
    }

    private Object[][] convert2Data() {
        Object[][] data = new Object[stocks.size()][column_names.length];
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            data[i] = new Object[]{stock.getName(), stock.getSymbol(), stock.getLatestPrice(), stock.getChange(),
                    stock.getChangeRatio(), stock.getVolume(), stock.getTurnover(), stock.getMarketValue()};
        }
        return data;
    }

    public String appendParams(String params) {
        return StringResUtils.QT_STOCK_URL + params;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isRedRise() {
        return isRedRise;
    }

    public void setRedRise(boolean redRise) {
        isRedRise = redRise;
    }
}