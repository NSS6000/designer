package com.fr.design.gui.style;

/*
 * Copyright(c) 2001-2010, FineReport Inc, All Rights Reserved.
 */

import com.fr.base.BaseUtils;
import com.fr.base.CellBorderStyle;
import com.fr.base.Style;
import com.fr.design.constants.LayoutConstants;
import com.fr.design.event.GlobalNameListener;
import com.fr.design.event.GlobalNameObserver;
import com.fr.design.foldablepane.UIExpandablePane;
import com.fr.design.gui.ibutton.UIToggleButton;
import com.fr.design.gui.icombobox.LineComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.style.color.NewColorSelectBox;
import com.fr.design.utils.gui.GUICoreUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.stable.Constants;
import com.fr.stable.CoreConstants;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * @author zhou
 * @since 2012-5-28下午6:22:04
 */
public class BorderPane extends AbstractBasicStylePane implements GlobalNameObserver {
    private boolean insideMode = false;

    private UIToggleButton topToggleButton;
    private UIToggleButton horizontalToggleButton;
    private UIToggleButton bottomToggleButton;
    private UIToggleButton leftToggleButton;
    private UIToggleButton verticalToggleButton;
    private UIToggleButton rightToggleButton;

    private UIToggleButton innerToggleButton;
    private UIToggleButton outerToggleButton;

    private LineComboBox currentLineCombo;
    private NewColorSelectBox currentLineColorPane;
    private JPanel panel;
    private JPanel borderPanel;
    private JPanel backgroundPanel;
    private BackgroundPane backgroundPane;
    private GlobalNameListener globalNameListener = null;

    public BorderPane() {
        this.initComponents();
    }

    protected void initComponents() {
        initButtonsWithIcon();
        this.setLayout(new BorderLayout(0, 0));
        JPanel externalPane = new JPanel(new GridLayout(0, 4));
        externalPane.add(topToggleButton);
        externalPane.add(leftToggleButton);
        externalPane.add(bottomToggleButton);
        externalPane.add(rightToggleButton);
        JPanel insidePane = new JPanel(new GridLayout(0, 2));
        insidePane.add(horizontalToggleButton);
        insidePane.add(verticalToggleButton);
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        Component[][] components = new Component[][]{
                new Component[]{null, null},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Style") + "    ", SwingConstants.LEFT), currentLineCombo},
                new Component[]{null, null},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Color") + "    ", SwingConstants.LEFT), currentLineColorPane},
                new Component[]{null, null},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_outBorder") + "    ", SwingConstants.LEFT), outerToggleButton = new UIToggleButton(BaseUtils.readIcon("com/fr/design/images/m_format/out.png"))},
                new Component[]{null, externalPane},
                new Component[]{null, null},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_inBorder") + "    ", SwingConstants.LEFT), innerToggleButton = new UIToggleButton(BaseUtils.readIcon("com/fr/design/images/m_format/in.png"))},
                new Component[]{null, insidePane},
                new Component[]{null, null}
        };
        double[] rowSize = {p, p, p, p, p, p, p, p, p, p, p};
        double[] columnSize = {p, f};
        int[][] rowCount = {{1, 1}, {1, 1}, {1, 1}, {1, 1}, {1, 1}, {1, 1}, {1, 1}, {1, 1}, {1, 1}, {1, 1}, {1, 1}};
        panel = TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, rowCount, LayoutConstants.VGAP_SMALL, LayoutConstants.VGAP_MEDIUM);
        borderPanel = new UIExpandablePane(Inter.getLocText("FR-Designer_Border"), 280, 24, panel);
        this.add(borderPanel, BorderLayout.NORTH);

        backgroundPane = new BackgroundPane();
        backgroundPanel = new UIExpandablePane(Inter.getLocText("FR-Designer_Background"), 280, 24, backgroundPane);
        this.add(backgroundPanel, BorderLayout.CENTER);
        initAllNames();
        outerToggleButton.addChangeListener(outerToggleButtonChangeListener);
        innerToggleButton.addChangeListener(innerToggleButtonChangeListener);
    }

    ChangeListener outerToggleButtonChangeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            boolean value = outerToggleButton.isSelected();
            topToggleButton.setSelected(value);
            bottomToggleButton.setSelected(value);
            leftToggleButton.setSelected(value);
            rightToggleButton.setSelected(value);
        }
    };

    ChangeListener innerToggleButtonChangeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            boolean value = innerToggleButton.isSelected();
            horizontalToggleButton.setSelected(value);
            verticalToggleButton.setSelected(value);
        }
    };

    private void initButtonsWithIcon() {
        topToggleButton = new UIToggleButton(BaseUtils.readIcon("/com/fr/base/images/dialog/border/top.png"));
        leftToggleButton = new UIToggleButton(BaseUtils.readIcon("/com/fr/base/images/dialog/border/left.png"));
        bottomToggleButton = new UIToggleButton(BaseUtils.readIcon("/com/fr/base/images/dialog/border/bottom.png"));
        rightToggleButton = new UIToggleButton(BaseUtils.readIcon("/com/fr/base/images/dialog/border/right.png"));
        horizontalToggleButton = new UIToggleButton(BaseUtils.readIcon("/com/fr/base/images/dialog/border/horizontal.png"));
        verticalToggleButton = new UIToggleButton(BaseUtils.readIcon("/com/fr/base/images/dialog/border/vertical.png"));
        this.currentLineCombo = new LineComboBox(CoreConstants.UNDERLINE_STYLE_ARRAY);
        this.currentLineColorPane = new NewColorSelectBox(100);
    }

    private void initAllNames() {
        currentLineCombo.setGlobalName("currentLineCombo");
        currentLineColorPane.setGlobalName("currentLineColorPane");
        outerToggleButton.setGlobalName("outerToggleButton");
        topToggleButton.setGlobalName("topToggleButton");
        leftToggleButton.setGlobalName("leftToggleButton");
        bottomToggleButton.setGlobalName("bottomToggleButton");
        rightToggleButton.setGlobalName("rightToggleButton");
        innerToggleButton.setGlobalName("innerToggleButton");
        horizontalToggleButton.setGlobalName("horizontalToggleButton");
        verticalToggleButton.setGlobalName("verticalToggleButton");
    }

    @Override
    public String title4PopupWindow() {
        return Inter.getLocText("FR-Designer_Cell");
    }

    @Override
    public void populateBean(Style style) {
        if (style == null) {
            style = Style.DEFAULT_STYLE;
        }

        CellBorderStyle cellBorderStyle = new CellBorderStyle();
        cellBorderStyle.setTopStyle(style.getBorderTop());
        cellBorderStyle.setTopColor(style.getBorderTopColor());
        cellBorderStyle.setLeftStyle(style.getBorderLeft());
        cellBorderStyle.setLeftColor(style.getBorderLeftColor());
        cellBorderStyle.setBottomStyle(style.getBorderBottom());
        cellBorderStyle.setBottomColor(style.getBorderBottomColor());
        cellBorderStyle.setRightStyle(style.getBorderRight());
        cellBorderStyle.setRightColor(style.getBorderRightColor());
        this.backgroundPane.populateBean(style.getBackground());
        this.populateBean(cellBorderStyle, false, style.getBorderTop(), style.getBorderTopColor());

    }

    public void populateBean(CellBorderStyle cellBorderStyle, boolean insideMode, int currentStyle, Color currentColor) {
        this.insideMode = insideMode;

        this.currentLineCombo.setSelectedLineStyle(cellBorderStyle.getTopStyle() == Constants.LINE_NONE ? Constants.LINE_THIN : cellBorderStyle.getTopStyle());
        this.currentLineColorPane.setSelectObject(cellBorderStyle.getTopColor());

        this.topToggleButton.setSelected(cellBorderStyle.getTopStyle() != Constants.LINE_NONE);
        this.bottomToggleButton.setSelected(cellBorderStyle.getBottomStyle() != Constants.LINE_NONE);
        this.leftToggleButton.setSelected(cellBorderStyle.getLeftStyle() != Constants.LINE_NONE);
        this.rightToggleButton.setSelected(cellBorderStyle.getRightStyle() != Constants.LINE_NONE);

        this.horizontalToggleButton.setSelected(cellBorderStyle.getHorizontalStyle() != Constants.LINE_NONE);
        this.verticalToggleButton.setSelected(cellBorderStyle.getVerticalStyle() != Constants.LINE_NONE);

        this.innerToggleButton.setSelected(cellBorderStyle.getInnerBorder() != Constants.LINE_NONE);
        this.outerToggleButton.setSelected(cellBorderStyle.getOuterBorderStyle() != Constants.LINE_NONE);

        this.innerToggleButton.setEnabled(this.insideMode);
        this.horizontalToggleButton.setEnabled(this.insideMode);
        this.verticalToggleButton.setEnabled(this.insideMode);
    }

    public Style update(Style style) {

        if (style == null) {
            style = Style.DEFAULT_STYLE;
        }

        CellBorderStyle cellBorderStyle = this.update();

        if (ComparatorUtils.equals(globalNameListener.getGlobalName(), "currentLineCombo") ||
                ComparatorUtils.equals(globalNameListener.getGlobalName(), "currentLineColorPane") ||
                ComparatorUtils.equals(globalNameListener.getGlobalName(), "outerToggleButton") ||
                ComparatorUtils.equals(globalNameListener.getGlobalName(), "topToggleButton") ||
                ComparatorUtils.equals(globalNameListener.getGlobalName(), "leftToggleButton") ||
                ComparatorUtils.equals(globalNameListener.getGlobalName(), "bottomToggleButton") ||
                ComparatorUtils.equals(globalNameListener.getGlobalName(), "rightToggleButton") ||
                ComparatorUtils.equals(globalNameListener.getGlobalName(), "innerToggleButton") ||
                ComparatorUtils.equals(globalNameListener.getGlobalName(), "horizontalToggleButton") ||
                ComparatorUtils.equals(globalNameListener.getGlobalName(), "verticalToggleButton")) {
            style = style.deriveBorder(cellBorderStyle.getTopStyle(), cellBorderStyle.getTopColor(), cellBorderStyle.getBottomStyle(), cellBorderStyle.getBottomColor(),
                    cellBorderStyle.getLeftStyle(), cellBorderStyle.getLeftColor(), cellBorderStyle.getRightStyle(), cellBorderStyle.getRightColor());
        } else {
            style = style.deriveBackground(backgroundPane.update());
        }
        return style;
    }

    public CellBorderStyle update() {
        int lineStyle = currentLineCombo.getSelectedLineStyle();
        Color lineColor = currentLineColorPane.getSelectObject();
        CellBorderStyle cellBorderStyle = new CellBorderStyle();
        cellBorderStyle.setTopColor(lineColor);
        cellBorderStyle.setTopStyle(topToggleButton.isSelected() ? lineStyle : Constants.LINE_NONE);
        cellBorderStyle.setBottomColor(lineColor);
        cellBorderStyle.setBottomStyle(bottomToggleButton.isSelected() ? lineStyle : Constants.LINE_NONE);
        cellBorderStyle.setLeftColor(lineColor);
        cellBorderStyle.setLeftStyle(leftToggleButton.isSelected() ? lineStyle : Constants.LINE_NONE);
        cellBorderStyle.setRightColor(lineColor);
        cellBorderStyle.setRightStyle(rightToggleButton.isSelected() ? lineStyle : Constants.LINE_NONE);
        cellBorderStyle.setVerticalColor(lineColor);
        cellBorderStyle.setVerticalStyle(verticalToggleButton.isSelected() ? lineStyle : Constants.LINE_NONE);
        cellBorderStyle.setHorizontalColor(lineColor);
        cellBorderStyle.setHorizontalStyle(horizontalToggleButton.isSelected() ? lineStyle : Constants.LINE_NONE);
        return cellBorderStyle;
    }

    @Override
    public void registerNameListener(GlobalNameListener listener) {
        globalNameListener = listener;
    }

    @Override
    public boolean shouldResponseNameListener() {
        return false;
    }

    @Override
    public void setGlobalName(String name) {

    }
}