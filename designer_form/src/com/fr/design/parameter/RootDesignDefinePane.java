package com.fr.design.parameter;

import com.fr.base.BaseUtils;
import com.fr.design.data.DataCreatorUI;
import com.fr.design.designer.creator.XCreator;
import com.fr.design.designer.creator.XWParameterLayout;
import com.fr.design.foldablepane.UIExpandablePane;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.ispinner.UISpinner;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.widget.accessibles.AccessibleBackgroundEditor;
import com.fr.design.widget.ui.designer.AbstractDataModify;
import com.fr.form.ui.container.WParameterLayout;
import com.fr.general.Background;
import com.fr.general.Inter;
import com.fr.stable.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ibm on 2017/8/2.
 */
public class RootDesignDefinePane extends AbstractDataModify<WParameterLayout> {
    private XWParameterLayout root;
    private UISpinner designerWidth;
    private UICheckBox displayReport;
    private AccessibleBackgroundEditor background;
    private UIButtonGroup hAlignmentPane;

    public RootDesignDefinePane(XCreator xCreator) {
        super(xCreator);
        this.root = (XWParameterLayout) xCreator;
        initComponent();
    }


    public void initComponent() {
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
        designerWidth = new UISpinner(1, 1000, 1);
        JPanel advancePane = createAdvancePane();
        UIExpandablePane advanceExpandablePane = new UIExpandablePane(Inter.getLocText("FR-Designer_Advanced"), 280, 20, advancePane);
        this.add(advanceExpandablePane, BorderLayout.NORTH);
        JPanel layoutPane = createBoundsPane();
        UIExpandablePane layoutExpandablePane = new UIExpandablePane(Inter.getLocText("Size"), 280, 20, layoutPane);
        this.add(layoutExpandablePane, BorderLayout.CENTER);

    }

    public JPanel createBoundsPane() {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        double[] rowSize = {p};
        double[] columnSize = {p, f};
        int[][] rowCount = {{1, 1}};
        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("Form-Desin_Width")), designerWidth},
        };
        JPanel panel = TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, rowCount, 20, 10);
        JPanel jPanel = FRGUIPaneFactory.createBorderLayout_S_Pane();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        jPanel.add(panel);
        return jPanel;
    }

    public JPanel createAdvancePane() {
        JPanel jPanel = FRGUIPaneFactory.createBorderLayout_S_Pane();
        displayReport = new UICheckBox(Inter.getLocText("FR-Designer_DisplayNothingBeforeQuery"));
        background = new AccessibleBackgroundEditor();
        Icon[] hAlignmentIconArray = {BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_left_normal.png"),
                BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_center_normal.png"),
                BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_right_normal.png"),};
        Integer[] hAlignment = new Integer[]{Constants.LEFT, Constants.CENTER, Constants.RIGHT};
        hAlignmentPane = new UIButtonGroup<Integer>(hAlignmentIconArray, hAlignment);
        hAlignmentPane.setAllToolTips(new String[]{Inter.getLocText("FR-Designer-StyleAlignment_Left")
                , Inter.getLocText("FR-Designer-StyleAlignment_Center"), Inter.getLocText("FR-Designer-StyleAlignment_Right")});
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        double[] rowSize = {p, p, p};
        double[] columnSize = {p, f};
        int[][] rowCount = {{1, 1}, {1, 1}, {1, 1}};
        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Background")), background},
                new Component[]{displayReport, null},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_WidgetDisplyPosition")), hAlignmentPane}
        };
        JPanel panel = TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, rowCount, 20, 10);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        jPanel.add(panel);
        return jPanel;
    }

    @Override
    public String title4PopupWindow() {
        return "parameter";
    }

    @Override
    public void populateBean(WParameterLayout ob) {
        background.setValue(ob.getBackground());
        displayReport.setSelected(ob.isDelayDisplayContent());
        designerWidth.setValue(ob.getDesignWidth());
        hAlignmentPane.setSelectedIndex(ob.getPosition());
    }


    @Override
    public WParameterLayout updateBean() {
        WParameterLayout wParameterLayout = (WParameterLayout) creator.toData();
        wParameterLayout.setDesignWidth((int) designerWidth.getValue());
        wParameterLayout.setDelayDisplayContent(displayReport.isSelected());
        wParameterLayout.setBackground((Background) background.getValue());
        wParameterLayout.setPosition(hAlignmentPane.getSelectedIndex());
        return wParameterLayout;
    }

    @Override
    public DataCreatorUI dataUI() {
        return null;
    }

}