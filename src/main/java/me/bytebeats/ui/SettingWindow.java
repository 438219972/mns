package me.bytebeats.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class SettingWindow implements Configurable {
    private JPanel mns_setting;
    private JTextField us_stock_input;
    private JTextField hk_stock_input;
    private JTextField sh_stock_input;
    private JLabel us_stock;
    private JLabel hk_stock;
    private JLabel sh_stock;
    private JRadioButton red_rise_green_fall;
    private JRadioButton red_fall_green_rise;
    private JPanel mkt_setting_radio;
    private JLabel hide_mode_desc;
    private JCheckBox hide_mode_setting;
    private JLabel sz_stock;
    private JTextField sz_stock_input;
    private JLabel idx_label;
    private JLabel idx_input_noneditable;
    private JLabel daily_fund;
    private JTextField daily_fund_input;
    private JLabel mkt_setting_label;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return mns_setting.getToolTipText();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        initSettingUI();
        red_rise_green_fall.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                red_fall_green_rise.setSelected(false);
            }
        });
        red_fall_green_rise.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                red_rise_green_fall.setSelected(false);
            }
        });
        hide_mode_setting.addItemListener(e -> {
            boolean hidden = e.getStateChange() == ItemEvent.SELECTED;
            red_rise_green_fall.setEnabled(!hidden);
            red_fall_green_rise.setEnabled(!hidden);
        });
        return mns_setting;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        AppSettingState settingState = AppSettingState.getInstance();
        settingState.setUsStocks(us_stock_input.getText());
        settingState.setHkStocks(hk_stock_input.getText());
        settingState.setShStocks(sh_stock_input.getText());
        settingState.setSzStocks(sz_stock_input.getText());
        settingState.setDailyFunds(daily_fund_input.getText());
        settingState.setRedRise(red_rise_green_fall.isSelected());
        settingState.setHiddenMode(hide_mode_setting.isSelected());
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mns_setting;
    }

    @Override
    public void reset() {
        initSettingUI();
    }

    private void initSettingUI() {
        AppSettingState settings = AppSettingState.getInstance();
        us_stock_input.setText(settings.getUsStocks());
        hk_stock_input.setText(settings.getHkStocks());
        sh_stock_input.setText(settings.getShStocks());
        sz_stock_input.setText(settings.getSzStocks());
        daily_fund_input.setText(settings.getDailyFunds());
        red_rise_green_fall.setSelected(settings.isRedRise());
        red_fall_green_rise.setSelected(!settings.isRedRise());
        boolean isHidden = settings.isHiddenMode();
        red_rise_green_fall.setEnabled(!isHidden);
        red_fall_green_rise.setEnabled(!isHidden);
        hide_mode_setting.setSelected(isHidden);
    }

    @Override
    public void disposeUIResources() {
        mns_setting = null;
    }

    @Override
    public void cancel() {

    }
}
