/*
 * Copyright(c) 2001-2010, FineReport Inc, All Rights Reserved.
 */
package com.fr.design.data.datapane.connect;

import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.data.impl.JNDIDatabaseConnection;
import com.fr.data.operator.DataOperator;
import com.fr.design.beans.BasicBeanPane;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.scrollruler.ModLineBorder;
import com.fr.design.utils.gui.GUICoreUtils;

import com.fr.log.FineLoggerFactory;
import com.fr.stable.EncodeConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Database Connection pane.
 */
public abstract class DatabaseConnectionPane<E extends com.fr.data.impl.Connection> extends BasicBeanPane<com.fr.data.impl.Connection> {

    private UILabel message;
    private UIButton okButton;
    private UIButton cancelButton;
    private JDialog dialog;
    private UILabel uiLabel;
    private String oirginalCharSet = null;
    private String newCharSet = null;

    // Database pane
    public DatabaseConnectionPane() {
        this.initComponents();
    }

    protected void initComponents() {
        message = new UILabel();
        uiLabel = new UILabel();
        okButton = new UIButton(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_OK"));
        cancelButton = new UIButton(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Cancel"));
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
        JPanel northPane = FRGUIPaneFactory.createY_AXISBoxInnerContainer_L_Pane();
        this.add(northPane, BorderLayout.NORTH);

        // 按钮.
        JPanel testPane = FRGUIPaneFactory.createNormalFlowInnerContainer_M_Pane();
        northPane.add(testPane, BorderLayout.NORTH);
        UIButton testButton = new UIButton(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Datasource_Test_Connection"));
        testPane.add(testButton);
        testButton.addActionListener(testConnectionActionListener);
        testPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 4));

        // Center
        northPane.add(mainPanel(), BorderLayout.CENTER);
    }

    protected abstract JPanel mainPanel();

    protected abstract boolean isFineBI();

    @Override
    public void populateBean(com.fr.data.impl.Connection ob) {
        this.oirginalCharSet = ob.getOriginalCharsetName();
        this.newCharSet = ob.getNewCharsetName();

        populateSubDatabaseConnectionBean((E) ob);
    }

    protected abstract void populateSubDatabaseConnectionBean(E ob);

    @Override
    public com.fr.data.impl.Connection updateBean() {
        E ob = updateSubDatabaseConnectionBean();

        ob.setOriginalCharsetName(this.oirginalCharSet);
        ob.setNewCharsetName(this.newCharSet);

        return ob;
    }

    protected abstract E updateSubDatabaseConnectionBean();

    ActionListener testConnectionActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {

            // Try the java connection.
            final SwingWorker connectionThread = new SwingWorker() {
                protected Object doInBackground() throws Exception {
                    try {
                        com.fr.data.impl.Connection database = DatabaseConnectionPane.this.updateBean();
                        boolean connect = DataOperator.getInstance().testConnection(database);
                        okButton.setEnabled(true);
                        message.setText(database.connectMessage(connect));
                        if (connect) {
                            uiLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
                            message.setText(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Datasource_Connection_Successfully"));
                        }else{
                            uiLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
                            message.setText(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Connection_Failed"));
                        }
                    } catch (Exception exp) {
                        FineLoggerFactory.getLogger().error(exp.getMessage(), exp);
                    }
                    return null;
                }
            };

            connectionThread.execute();
            initDialogPane();
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                    connectionThread.cancel(true);
                }
            });

            dialog.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    connectionThread.cancel(true);
                }
            });

            dialog.show();
            dialog.dispose();
        }
    };

    private void initDialogPane() {

        message.setText(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Datasource_Test_Connection") + "...");
        message.setBorder(BorderFactory.createEmptyBorder(8, 5, 0, 0));
        okButton.setEnabled(false);

        dialog = new JDialog((Dialog) SwingUtilities.getWindowAncestor(DatabaseConnectionPane.this), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Datasource_Test_Connection"), true);
        dialog.setSize(new Dimension(268, 118));
        okButton.setEnabled(false);
        JPanel jp = new JPanel();
        JPanel upPane = new JPanel();
        JPanel downPane = new JPanel();
        uiLabel = new UILabel(UIManager.getIcon("OptionPane.informationIcon"));

        upPane.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        upPane.add(uiLabel);
        upPane.add(message);
        downPane.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 0));
        downPane.add(okButton);
        downPane.add(cancelButton);
        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
        jp.add(upPane);
        jp.add(downPane);
        dialog.add(jp);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(DatabaseConnectionPane.this));
    }


    public static class JDBC extends DatabaseConnectionPane<JDBCDatabaseConnection> {
        private static JDBCDefPane jdbcDefPane = new JDBCDefPane();

        @Override
        protected JPanel mainPanel() {
            return jdbcDefPane;
        }

        @Override
        protected boolean isFineBI() {
            return false;
        }

        @Override
        protected void populateSubDatabaseConnectionBean(JDBCDatabaseConnection ob) {
            jdbcDefPane.populate(ob);
        }

        @Override
        protected JDBCDatabaseConnection updateSubDatabaseConnectionBean() {
            return jdbcDefPane.update();
        }

        @Override
        protected String title4PopupWindow() {
            return "JDBC";
        }
    }

    public static class JNDI extends DatabaseConnectionPane<JNDIDatabaseConnection> {
        private static JNDIDefPane jndiDefPane = new JNDIDefPane();

        @Override
        protected JPanel mainPanel() {
            return jndiDefPane;
        }

        @Override
        protected boolean isFineBI() {
            return false;
        }

        @Override
        protected void populateSubDatabaseConnectionBean(JNDIDatabaseConnection ob) {
            jndiDefPane.populate(ob);
        }

        @Override
        protected JNDIDatabaseConnection updateSubDatabaseConnectionBean() {
            return jndiDefPane.update();
        }

        @Override
        protected String title4PopupWindow() {
            return "JNDI";
        }
    }

}