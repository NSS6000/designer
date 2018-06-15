package com.fr.design.remote.action;

import com.fr.base.BaseUtils;
import com.fr.design.actions.UpdateAction;
import com.fr.design.dialog.BasicDialog;
import com.fr.design.mainframe.DesignerContext;
import com.fr.design.remote.ui.AuthorityManagerPane;
import com.fr.general.Inter;

import java.awt.event.ActionEvent;

/**
 * @author yaohwu
 */
public class RemoteDesignAuthorityManagerAction extends UpdateAction {


    public RemoteDesignAuthorityManagerAction() {
        this.setName(Inter.getLocText("Fine-Designer_Remote_Design_Authority_Manager"));
        this.setSmallIcon(BaseUtils.readIcon("com/fr/design/remote/images/icon_Remote_Design_Permission_Manager_normal@1x.png"));
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        final AuthorityManagerPane managerPane = new AuthorityManagerPane();

        BasicDialog dialog = managerPane.showWindow(DesignerContext.getDesignerFrame());

//        if (!FRContext.getCurrentEnv().isLocalEnv()) {
//            try {
//                // 远程设计获取全部设计成员的权限列表
//                DesignAuthority[] authorities = EnvProxy.get(AuthorityOperator.class).getAuthorities();
//                if (authorities != null && authorities.length != 0) {
//                    managerPane.populate(authorities);
//                }
//            } catch (Exception exception) {
//                FineLoggerFactory.getLogger().error(exception.getMessage(), exception);
//            }
//        }
//
//        dialog.addDialogActionListener(new DialogActionAdapter() {
//            @Override
//            public void doOk() {
//                DesignAuthority[] authorities = managerPane.update();
//                if (!FRContext.getCurrentEnv().isLocalEnv()) {
//                    boolean success = false;
//                    try {
//                        success = EnvProxy.get(AuthorityOperator.class).updateAuthorities(authorities);
//                    } catch (Exception e) {
//                        FineLoggerFactory.getLogger().error(e.getMessage(), e);
//                    }
//                    FRContext.getLogger().info("update remote design authority: " + success);
//                }
//            }
//
//            @Override
//            public void doCancel() {
//                super.doCancel();
//            }
//        });
        dialog.setModal(true);
        dialog.setVisible(true);
    }
}
