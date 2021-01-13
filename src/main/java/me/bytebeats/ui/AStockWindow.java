package me.bytebeats.ui;

import me.bytebeats.OnSymbolSelectedListener;
import me.bytebeats.SymbolParser;
import me.bytebeats.handler.AbsStockHandler;
import me.bytebeats.handler.TencentStockHandler;
import me.bytebeats.tool.StringResUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AStockWindow implements SymbolParser {
    private JPanel sh_stock_window;
    private JScrollPane sh_stock_scroll;
    private JTable sh_stock_table;
    private JLabel sh_stock_timestamp;
    private JButton sh_sync;

    private AbsStockHandler handler;

    public AStockWindow() {
        handler = new TencentStockHandler(sh_stock_table, sh_stock_timestamp);
    }

    public void setOnSymbolSelectedListener(OnSymbolSelectedListener listener) {
        handler.setOnSymbolSelectedListener(listener);
    }

    public JPanel getJPanel() {
        return sh_stock_window;
    }

    public void onInit() {
        sh_sync.addActionListener(e -> syncRefresh());
        syncRefresh();
    }

    private void syncRefresh() {
        handler.load(parse());
    }

    @Override
    public String prefix() {
//        return "sh";//实时数据
//        return "s_sh";//简要信息
        return null;
    }

    @Override
    public String raw() {
//        return AppSettingState.getInstance().getShStocks();
        return null;
    }

    @Override
    public List<String> parse() {
        List<String> symbols = new ArrayList<>();
//        String sh = raw();
        String sh = AppSettingState.getInstance().getShStocks();
        assert sh != null;
        if (!sh.isEmpty()) {
            Arrays.stream(sh.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add(StringResUtils.A_S_SH_PREFIX + s));
        }
        String sz = AppSettingState.getInstance().getSzStocks();
        if (!sz.isEmpty()) {
            Arrays.stream(sz.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add(StringResUtils.A_S_SZ_PREFIX + s));
        }
        return symbols;
    }
}
