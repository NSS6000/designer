package com.fr.design.widget.component;

import com.fr.design.dialog.BasicPane;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.widget.accessibles.AccessibleBackgroundEditor;
import com.fr.form.ui.Widget;
import com.fr.general.Inter;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ibm on 2017/8/6.
 */
public abstract class BackgroundCompPane<T extends Widget> extends BasicPane {
    protected UIButtonGroup backgroundHead;
    protected AccessibleBackgroundEditor initalBackgroundEditor;
    protected AccessibleBackgroundEditor overBackgroundEditor;
    protected AccessibleBackgroundEditor clickBackgroundEditor;
    private JPanel panel;

    public BackgroundCompPane() {
        initComponent();
    }

    public void initComponent() {
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
        initalBackgroundEditor = new AccessibleBackgroundEditor();
        overBackgroundEditor = new AccessibleBackgroundEditor();
        clickBackgroundEditor = new AccessibleBackgroundEditor();
        String [] titles = new String[]{Inter.getLocText("FR-Designer_DEFAULT"), Inter.getLocText("FR-Designer_Custom")};

        double f = TableLayout.FILL;
        final double p = TableLayout.PREFERRED;
        double[] rowSize = {p, p, p};
        double[] columnSize = {p, f};
        int[][] rowCount = {{1, 1},{1, 1},{1, 1}};
        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Background-Initial")), initalBackgroundEditor},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Background-Over")), overBackgroundEditor},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Background-Click")), clickBackgroundEditor},
        };
        panel = TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, rowCount, 7, 7);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        backgroundHead = new UIButtonGroup(titles);
        this.add(backgroundHead, BorderLayout.NORTH);
        this.add(panel, BorderLayout.CENTER);

    }


    public void update(T e){
    }

    public void populate(T e){
    }

    public void switchCard(){
        panel.setVisible(backgroundHead.getSelectedIndex() == 1);
    }

}
