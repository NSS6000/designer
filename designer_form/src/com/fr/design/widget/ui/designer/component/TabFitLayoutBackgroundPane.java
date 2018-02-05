package com.fr.design.widget.ui.designer.component;

import com.fr.design.gui.ilable.UILabel;
import com.fr.design.mainframe.widget.accessibles.AccessibleTabBackgroundEditor;
import com.fr.design.widget.component.BackgroundCompPane;
import com.fr.form.ui.container.cardlayout.WTabFitLayout;
import com.fr.general.Background;
import com.fr.general.Inter;

/**
 * Created by ibm on 2017/8/8.
 */
public class TabFitLayoutBackgroundPane extends BackgroundCompPane<WTabFitLayout> {

    public TabFitLayoutBackgroundPane(){

    }

    @Override
    protected void initBackgroundEditor(){
        initalBackgroundEditor = new AccessibleTabBackgroundEditor();
        overBackgroundEditor = new AccessibleTabBackgroundEditor();
        clickBackgroundEditor = new AccessibleTabBackgroundEditor();
    }

    @Override
    protected UILabel getClickLabel(){
        return new UILabel(Inter.getLocText("FR-Designer_Background_Select"));
    }

    @Override
    public void update(WTabFitLayout tabFitLayout){
        int selectIndex = backgroundHead.getSelectedIndex();
        if(selectIndex == 0){
            tabFitLayout.setCustomStyle(false);
            tabFitLayout.setInitialBackground(null);
            tabFitLayout.setOverBackground(null);
            tabFitLayout.setClickBackground(null);
        }else{
            tabFitLayout.setCustomStyle(true);
            tabFitLayout.setInitialBackground((Background) initalBackgroundEditor.getValue());
            tabFitLayout.setOverBackground((Background) overBackgroundEditor.getValue());
            tabFitLayout.setClickBackground((Background)clickBackgroundEditor.getValue());
        }
        switchCard();
    }

    @Override
    public void populate(WTabFitLayout tabFitLayout){
        if(!tabFitLayout.isCustomStyle()){
            backgroundHead.setSelectedIndex(0);
            initalBackgroundEditor.setValue(null);
            overBackgroundEditor.setValue(null);
            clickBackgroundEditor.setValue(null);
        }else{
            backgroundHead.setSelectedIndex(1);
            initalBackgroundEditor.setValue(tabFitLayout.getInitialBackground());
            overBackgroundEditor.setValue(tabFitLayout.getOverBackground());
            clickBackgroundEditor.setValue(tabFitLayout.getClickBackground());
        }
        switchCard();
    }

    protected UILabel createUILable(){
        return new UILabel(Inter.getLocText("FR-Designer_Style"));
    }

    protected  String title4PopupWindow() {
        return "tabFitBackground";
    }


}
