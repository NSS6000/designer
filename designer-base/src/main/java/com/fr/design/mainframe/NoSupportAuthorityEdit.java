package com.fr.design.mainframe;

import com.fr.design.constants.UIConstants;
import com.fr.design.file.HistoryTemplateListPane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.FRGUIPaneFactory;


import javax.swing.*;
import java.awt.*;

/**
 * Author : daisy
 * Date: 13-9-22
 * Time: 上午10:05
 */
public class NoSupportAuthorityEdit extends AuthorityEditPane {

    private static final int TITLE_HEIGHT = 19;

    public NoSupportAuthorityEdit() {
        super(HistoryTemplateListPane.getInstance().getCurrentEditingTemplate());
        this.setLayout(new BorderLayout());
        this.setBorder(null);
        UILabel title = new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Privilege_Preference")) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, TITLE_HEIGHT);
            }
        };
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        JPanel northPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        northPane.add(title, BorderLayout.CENTER);
        northPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.LINE_COLOR));
//        this.add(northPane, BorderLayout.NORTH);
        this.add(createTextPane(), BorderLayout.CENTER);
    }


    private JPanel createTextPane() {
        JPanel panel = new JPanel(new BorderLayout());
        UILabel uiLabel = new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Not_Support_Authority_Edit"));
        uiLabel.setHorizontalAlignment(SwingConstants.CENTER);
        uiLabel.setVerticalAlignment(SwingConstants.CENTER);
        panel.add(uiLabel, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void populateType() {
    }

    @Override
    public void populateName() {
    }

    @Override
    public JPanel populateCheckPane() {
        return null;
    }
}
