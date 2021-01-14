package me.bytebeats.handler;

import me.bytebeats.HttpClientPool;
import me.bytebeats.LogUtil;
import me.bytebeats.meta.Stock;
import me.bytebeats.tool.StringResUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TencentStockHandler extends AbsStockHandler {
    private static final long REFRESH_INTERVAL = 3L * 1000L;

    public TencentStockHandler(JTable table, JLabel label) {
        super(table, label);
    }

    @Override
    public String[] getColumnNames() {
        return handleColumnNames(stockColumnNames);
    }

    @Override
    public void load(List<String> symbols) {
        stocks.clear();
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fetch(symbols);
            }
        }, 0, REFRESH_INTERVAL);
        LogUtil.info("starts updating " + jTable.getToolTipText() + " data");
    }

    private void fetch(List<String> symbols) {
        if (symbols.isEmpty()) {
            return;
        }
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < symbols.size(); i++) {
            if (params.length() != 0) {
                params.append(',');

            }
            params.append(getCode(symbols.get(i)));
        }
        try {
            String entity = HttpClientPool.getInstance().get(appendParams(params.toString()));
            parse(symbols, entity);
            updateView();
        } catch (Exception e) {
            LogUtil.info(e.getMessage());
            timer.cancel();
            timer = null;
            LogUtil.info("stops updating " + jTable.getToolTipText() + " data because of " + e.getMessage());
        }
    }

    private void parse(List<String> symbols, String entity) {
        String[] raws = entity.split("\n");
        if (symbols.size() != raws.length) {
            return;
        }
        for (int i = 0; i < symbols.size(); i++) {
            String symbol = symbols.get(i);
            String code = getCode(symbol);
            String raw = raws[i];
            String assertion = String.format("(?<=v_%s=\").*?(?=\";)", code);
            Pattern pattern = Pattern.compile(assertion);
            Matcher matcher = pattern.matcher(raw);
            while (matcher.find()) {
                String[] metas = matcher.group().split("~");
                Stock stock = new Stock();
//                stock.setSymbol(symbol);
//                stock.setName(metas[1]);
//                stock.setLatestPrice(Double.parseDouble(metas[3]));
//                stock.setChange(Double.parseDouble(metas[31]));
//                stock.setChangeRatio(Double.parseDouble(metas[32]));
//                stock.setVolume(Double.parseDouble(metas[36]));
//                stock.setTurnover(Double.parseDouble(metas[37]));
//                stock.setMarketValue(Double.parseDouble(metas[45]));

                String chPrefix = prefixTransform(code);
                //简要信息
                if(StringUtils.isNotBlank(chPrefix)){
                    stock.setName(chPrefix+metas[1]);
                } else {
                    stock.setName(metas[1]);
                }
                stock.setSymbol(code);
                stock.setLatestPrice(Double.parseDouble(metas[3]));
                stock.setChange(Double.parseDouble(metas[4]));
                stock.setChangeRatio(Double.parseDouble(metas[5]));
                stock.setCostPrice(getCostPrice(symbol));
                stock.setStockNum(getStockNum(symbol));
                stock.setProfit(getProfit(stock.getCostPrice(),stock.getLatestPrice(),stock.getStockNum()));
                updateStock(stock);
            }
        }
    }

    /**
     * 前缀替换成中文
     * @param symbol
     * @return
     */
    private String prefixTransform(String symbol){
        if(StringUtils.isNotBlank(symbol)){
            if(symbol.indexOf(StringResUtils.A_S_SH_PREFIX) > -1){
                return "[上证]";
            }
            if(symbol.indexOf(StringResUtils.A_S_SZ_PREFIX) > -1){
                return "[深证]";
            }
        }
        return null;
    }

    /**
     * 获取代码
     * @param symbol
     * @return
     */
    private String getCode(String symbol){
        // 没设置成本和持股数，则直接使用代码拼接
        if(symbol.contains(":")){
            String[] arr = symbol.split(":");
            return arr[0];
        }
        return symbol;
    }

    /**
     * 成本价
     * @param symbol
     * @return
     */
    private double getCostPrice(String symbol){
        // 没设置成本和持股数，则直接使用代码拼接
        if(symbol.contains(":")){
            String[] arr = symbol.split(":");
            return Double.valueOf(arr[1]);
        }
        return 0.0;
    }

    /**
     * 持股数
     * @param symbol
     * @return
     */
    private int getStockNum(String symbol){
        // 没设置成本和持股数，则直接使用代码拼接
        if(symbol.contains(":")){
            String[] arr = symbol.split(":");
            return Integer.valueOf(arr[2]);
        }
        return 0;
    }

    /**
     * 盈利
     * @param costPrice
     * @param currentPrice
     * @return
     */
    private double getProfit(double costPrice,double currentPrice,int stockNum){
        if(costPrice==0.0||stockNum==0){
            return 0.0;
        }
        double price = currentPrice-costPrice;
        return price*stockNum;
    }
}
