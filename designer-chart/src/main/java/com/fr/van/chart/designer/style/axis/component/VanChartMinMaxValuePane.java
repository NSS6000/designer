package com.fr.van.chart.designer.style.axis.component;

import com.fr.design.chart.ChartSwingUtils;
import com.fr.design.chart.axis.MinMaxValuePane;

import com.fr.van.chart.designer.TableLayout4VanChartHelper;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by mengao on 2017/4/4.
 */
public class VanChartMinMaxValuePane extends MinMaxValuePane {
    public static final int COMPONENT_INTERVAL =12;

    private JPanel minPane;
    private JPanel maxPane;
    private JPanel mainPane;
    private JPanel secPane;

    @Override
    protected double[] getRowSize(double p) {
        return new double[]{p, p, p, p};
    }

    @Override
    protected void addComponentListener(Component[][] components) {

        ChartSwingUtils.addListener(minCheckBox, minValueField);
        ChartSwingUtils.addListener(maxCheckBox, maxValueField);
        ChartSwingUtils.addListener(isCustomMainUnitBox, mainUnitField);
        ChartSwingUtils.addListener(isCustomSecUnitBox, secUnitField);
        minCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkBoxUse();
            }
        });
        maxCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkBoxUse();
            }
        });
        isCustomMainUnitBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkBoxUse();
            }
        });
        isCustomSecUnitBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkBoxUse();
            }
        });
    }

    @Override
    protected Component[][] getPanelComponents() {

        minPane = TableLayout4VanChartHelper.createGapTableLayoutPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Data_Min"),minValueField, TableLayout4VanChartHelper.SECOND_EDIT_AREA_WIDTH);
        maxPane = TableLayout4VanChartHelper.createGapTableLayoutPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Data_Max"),maxValueField, TableLayout4VanChartHelper.SECOND_EDIT_AREA_WIDTH);
        mainPane = TableLayout4VanChartHelper.createGapTableLayoutPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Main_Type"),mainUnitField, TableLayout4VanChartHelper.SECOND_EDIT_AREA_WIDTH);
        secPane = TableLayout4VanChartHelper.createGapTableLayoutPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_SecType"),secUnitField, TableLayout4VanChartHelper.SECOND_EDIT_AREA_WIDTH);

        minPane.setBorder(BorderFactory.createEmptyBorder(0,COMPONENT_INTERVAL,0,0));
        maxPane.setBorder(BorderFactory.createEmptyBorder(0,COMPONENT_INTERVAL,0,0));
        mainPane.setBorder(BorderFactory.createEmptyBorder(0,COMPONENT_INTERVAL,0,0));
        secPane.setBorder(BorderFactory.createEmptyBorder(0,COMPONENT_INTERVAL,0,0));

        JPanel minPaneWithCheckBox = new JPanel(new BorderLayout());
        JPanel maxPaneWithCheckBox = new JPanel(new BorderLayout());
        JPanel mainPaneWithCheckBox = new JPanel(new BorderLayout());
        JPanel secPaneWithCheckBox = new JPanel(new BorderLayout());

        minPaneWithCheckBox.add(minCheckBox, BorderLayout.NORTH);
        minPaneWithCheckBox.add(minPane, BorderLayout.CENTER);
        maxPaneWithCheckBox.add(maxCheckBox, BorderLayout.NORTH);
        maxPaneWithCheckBox.add(maxPane, BorderLayout.CENTER);
        mainPaneWithCheckBox.add(isCustomMainUnitBox, BorderLayout.NORTH);
        mainPaneWithCheckBox.add(mainPane, BorderLayout.CENTER);
        secPaneWithCheckBox.add(isCustomSecUnitBox, BorderLayout.NORTH);
        secPaneWithCheckBox.add(secPane, BorderLayout.CENTER);

        return getShowComponents(minPaneWithCheckBox, maxPaneWithCheckBox, mainPaneWithCheckBox, secPaneWithCheckBox);
    }

    protected Component[][] getShowComponents(JPanel minPaneWithCheckBox, JPanel maxPaneWithCheckBox, JPanel mainPaneWithCheckBox, JPanel secPaneWithCheckBox) {
        return new Component[][] {
                {minPaneWithCheckBox},
                {maxPaneWithCheckBox},
                {mainPaneWithCheckBox},
                {secPaneWithCheckBox},
        };
    }

    protected void checkBoxUse() {
        minPane.setVisible(minCheckBox.isSelected());
        maxPane.setVisible(maxCheckBox.isSelected());

        mainPane.setVisible(isCustomMainUnitBox.isSelected());
        secPane.setVisible(isCustomSecUnitBox.isSelected());
    }

}