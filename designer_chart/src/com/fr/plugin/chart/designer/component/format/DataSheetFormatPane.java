package com.fr.plugin.chart.designer.component.format;

import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.style.FormatPane;
import com.fr.design.layout.TableLayout;
import com.fr.general.Inter;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Created by mengao on 2017/8/14.
 */
public class DataSheetFormatPane extends FormatPane {
    protected Component[][] getComponent(JPanel fontPane, JPanel centerPane, JPanel typePane) {
        return new Component[][]{
                new Component[]{null, centerPane},
        };
    }

    protected void setTypeComboBoxPane(UIComboBox typeComboBox) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        double[] rowSize = {p, p};
        double e = TableLayout4VanChartHelper.EDIT_AREA_WIDTH;
        double[] columnSize = {f, e};
        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(getTypeComboBoxComponent(typeComboBox), rowSize, columnSize);
        this.add(panel, BorderLayout.NORTH);
    }

    protected Component[][] getTypeComboBoxComponent (UIComboBox typeComboBox) {
        return new Component[][]{
                new Component[]{null, null},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_DataType"), SwingConstants.LEFT), typeComboBox},

        };
    }
}