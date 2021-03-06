package me.bytebeats.handler;

import me.bytebeats.OnSymbolSelectedListener;
import me.bytebeats.UISettingProvider;
import me.bytebeats.meta.Stock;
import me.bytebeats.tool.PinyinUtils;
import me.bytebeats.tool.StringResUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bytebeats
 * @version 1.0
 * @email <happychinapc@gmail.com>
 * @github https://github.com/bytebeats
 * @created on 2020/8/29 16:21
 * @description AbsStockHandler defines common fields and methods to ui and data operation of stocks.
 */

public abstract class AbsStockHandler extends AbstractHandler implements UISettingProvider {

    protected final List<Stock> stocks = new ArrayList<>();

    protected final int[] stockTabWidths = {0, 0, 0, 0, 0, 0, 0, 0};
    protected final String[] stockColumnNames = {
            StringResUtils.STOCK_NAME,
            StringResUtils.SYMBOL,
            StringResUtils.STOCK_LATEST_PRICE,
            StringResUtils.RISE_AND_FALL,
            StringResUtils.RISE_AND_FALL_RATIO,
            StringResUtils.COST_PRICE,
            StringResUtils.STOCK_NUM,
            StringResUtils.PROFIT
    };

    private OnSymbolSelectedListener listener;

    public AbsStockHandler(JTable table, JLabel label) {
        super(table, label);
    }

    public OnSymbolSelectedListener getListener() {
        return listener;
    }

    public void setOnSymbolSelectedListener(OnSymbolSelectedListener listener) {
        this.listener = listener;
        jTable.setCellSelectionEnabled(true);
        ListSelectionModel model = jTable.getSelectionModel();
        model.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model.addListSelectionListener(e -> {
            int selectedRowIdx = jTable.getSelectedRow();
            if (selectedRowIdx > -1 && listener != null) {
                listener.onSelected(stocks.get(selectedRowIdx).getSymbol());
            }
        });
    }

    @Override
    public void restoreTabSizes() {
        if (jTable.getColumnModel().getColumnCount() == 0) {
            return;
        }
        for (int i = 0; i < stockColumnNames.length; i++) {
            stockTabWidths[i] = jTable.getColumnModel().getColumn(i).getWidth();
        }
    }

    @Override
    public void resetTabSize() {
        for (int i = 0; i < stockColumnNames.length; i++) {
            if (stockTabWidths[i] > 0) {
                jTable.getColumnModel().getColumn(i).setWidth(stockTabWidths[i]);
                jTable.getColumnModel().getColumn(i).setPreferredWidth(stockTabWidths[i]);
            }
        }
    }

    @Override
    public Object[][] convert2Data() {
        Object[][] data = new Object[stocks.size()][stockColumnNames.length];
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            String name = stock.getName();
            if (isInHiddenMode()) {
                name = PinyinUtils.toPinyin(name);
            }
            data[i] = new Object[]{name, stock.getSymbol(), stock.getLatestPrice(), stock.getChange(),
                    stock.getChangeRatioString(),stock.getCostPrice(),stock.getStockNum(),stock.getProfit()};
        }
        return data;
    }

    protected void updateStock(Stock stock) {
        int idx = stocks.indexOf(stock);
        if (idx > -1 && idx < stocks.size()) {
            stocks.set(idx, stock);
        } else {
            stocks.add(stock);
        }
    }

    public String appendParams(String params) {
        return StringResUtils.QT_STOCK_URL + params;
    }
}
