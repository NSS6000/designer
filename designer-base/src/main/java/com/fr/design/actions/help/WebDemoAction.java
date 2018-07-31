package com.fr.design.actions.help;

import com.fr.base.BaseUtils;
import com.fr.design.DesignerEnvManager;
import com.fr.design.actions.UpdateAction;
import com.fr.design.menu.MenuKeySet;

import com.fr.start.ServerStarter;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class WebDemoAction extends UpdateAction {
    public WebDemoAction() {
        this.setMenuKeySet(PRODUCT_DEMO);
        this.setName(getMenuKeySet().getMenuName());
        this.setMnemonic(getMenuKeySet().getMnemonic());
        this.setSmallIcon(BaseUtils.readIcon("/com/fr/design/images/m_help/demo.png"));
    }

    /**
     * 动作
     * @param evt 事件
     */
    public void actionPerformed(ActionEvent evt) {
        DesignerEnvManager.getEnvManager().setCurrentEnv2Default();
        ServerStarter.browserDemoURL();
    }

    public static final MenuKeySet PRODUCT_DEMO = new MenuKeySet() {
        @Override
        public char getMnemonic() {
            return 'D';
        }

        @Override
        public String getMenuName() {
            return com.fr.design.i18n.Toolkit.i18nText("FR-Product_Demo");
        }

        @Override
        public KeyStroke getKeyStroke() {
            return null;
        }
    };

}