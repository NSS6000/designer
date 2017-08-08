package com.fr.quickeditor.cellquick;

import com.fr.design.actions.core.ActionFactory;
import com.fr.design.actions.insert.cell.SubReportCellAction;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.general.Inter;
import com.fr.quickeditor.CellQuickEditor;

import javax.swing.*;
import java.awt.*;

/**
 * 单元格元素子报表编辑器
 *
 * @author yaoh.wu
 * @version 2017年8月7日10点53分
 */
public class CellSubReportEditor extends CellQuickEditor {
    private UIButton subReportButton;


    private CellSubReportEditor() {
        super();
    }

    @Override
    public JComponent createCenterBody() {
        JPanel content = new JPanel(new BorderLayout());
        subReportButton = new UIButton();
        subReportButton.setOpaque(false);
        content.add(TableLayoutHelper.createTableLayoutPane(new Component[][]{
                        new Component[]{emptyLabel, subReportButton}},
                new double[]{TableLayout.PREFERRED},
                new double[]{TableLayout.PREFERRED, TableLayout.FILL}), BorderLayout.CENTER);
        return content;
    }

    @Override
    protected void refreshDetails() {
        SubReportCellAction subReportCellAction = new SubReportCellAction(tc);
        subReportCellAction.setName(Inter.getLocText("Edit"));
        subReportCellAction.setSmallIcon(null);
        subReportButton.setAction(subReportCellAction);
    }

    @Override
    public Object getComboBoxSelected() {
        return ActionFactory.createAction(SubReportCellAction.class);
    }

}