package com.fr.design.actions.community;

import com.fr.base.BaseUtils;
import com.fr.base.FRContext;
import com.fr.design.actions.UpdateAction;
import com.fr.design.menu.MenuKeySet;
import com.fr.general.CloudCenter;

import com.fr.log.FineLoggerFactory;
import com.fr.stable.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BugAction extends UpdateAction
{

	public BugAction()
	{ this.setMenuKeySet(BUG);
    this.setName(getMenuKeySet().getMenuName());
    this.setMnemonic(getMenuKeySet().getMnemonic());
    this.setSmallIcon(BaseUtils.readIcon("/com/fr/design/images/bug.png"));
	
}

@Override
public void actionPerformed(ActionEvent arg0)
{
	 String url = CloudCenter.getInstance().acquireUrlByKind("bbs.bugs");
     if (StringUtils.isEmpty(url)) {
         FRContext.getLogger().info("The URL is empty!");
         return;
     }
     try {
         Desktop.getDesktop().browse(new URI(url));
     } catch (IOException exp) {
         JOptionPane.showMessageDialog(null, com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Set_Default_Browser"));
         FineLoggerFactory.getLogger().error(exp.getMessage(), exp);
     } catch (URISyntaxException exp) {
         FineLoggerFactory.getLogger().error(exp.getMessage(), exp);
     } catch (Exception exp) {
         FineLoggerFactory.getLogger().error(exp.getMessage(), exp);
         FineLoggerFactory.getLogger().error("Can not open the browser for URL:  " + url);
     }

}
 public static final MenuKeySet BUG = new MenuKeySet() {
  
       @Override
       public String getMenuName() {
           return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Community_Bug");
       }

       @Override
       public KeyStroke getKeyStroke() {
           return null;
       }

	@Override
	public char getMnemonic()
	{
		
		return 'U';
	}
   };

}
