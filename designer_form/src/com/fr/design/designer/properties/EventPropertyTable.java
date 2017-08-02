package com.fr.design.designer.properties;

import com.fr.design.actions.UpdateAction;
import com.fr.design.gui.controlpane.NameableCreator;
import com.fr.design.gui.controlpane.UIListControlPane;
import com.fr.design.gui.frpane.ListenerUpdatePane;
import com.fr.design.gui.ilist.ListModelElement;
import com.fr.design.gui.itoolbar.UIToolbar;
import com.fr.design.javascript.EmailPane;
import com.fr.design.javascript.JavaScriptActionPane;
import com.fr.design.mainframe.FormDesigner;
import com.fr.design.menu.MenuDef;
import com.fr.design.menu.ShortCut;
import com.fr.design.menu.ToolBarDef;
import com.fr.design.widget.EventCreator;
import com.fr.design.write.submit.DBManipulationPane;
import com.fr.design.editor.ValueEditorPaneFactory;
import com.fr.design.designer.creator.XCreator;
import com.fr.form.event.Listener;
import com.fr.design.form.javascript.FormEmailPane;
import com.fr.form.ui.Widget;
import com.fr.general.Inter;
import com.fr.general.NameObject;
import com.fr.js.JavaScriptImpl;
import com.fr.stable.Nameable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class EventPropertyTable extends UIListControlPane {

	private ShortCut[] shorts;
	private XCreator creator;
	private ToolBarDef toolbarDef;
	private UIToolbar toolbar;
	private FormDesigner designer;

	public EventPropertyTable(FormDesigner designer) {
		super();
		this.designer = designer;
	}

    @Override
    public String getAddItemText() {
        return Inter.getLocText("FR-Designer_Add_Event");
    }

	@Override
	protected void initComponentPane() {
		toolbarDef = new ToolBarDef();
		shorts = new ShortCut[] {new AddItemMenuDef()};
		for (ShortCut sj : shorts) {
			toolbarDef.addShortCut(sj);
		}
		toolbar = ToolBarDef.createJToolBar();
		toolbarDef.updateToolBar(toolbar);

		super.initComponentPane();
	}

	/**
	 * 指定索引添加对象
	 * @param nameObject 对象名
	 * @param index  索引
	 */
	public void addNameObject(NameObject nameObject, int index) {
		DefaultListModel model = (DefaultListModel) nameableList.getModel();

        model.add(index, new ListModelElement(nameObject));
		nameableList.setSelectedIndex(index);
		nameableList.ensureIndexIsVisible(index);

		nameableList.repaint();
	}

	public static class WidgetEventListenerUpdatePane extends ListenerUpdatePane {

		@Override
		protected JavaScriptActionPane createJavaScriptActionPane() {
			return new JavaScriptActionPane() {

				@Override
				protected DBManipulationPane createDBManipulationPane() {
					return new DBManipulationPane(ValueEditorPaneFactory.formEditors());
				}

				@Override
				protected String title4PopupWindow() {
					return Inter.getLocText("Set_Callback_Function");
				}
				@Override
				protected EmailPane initEmaiPane() {
					return new FormEmailPane();
				}
				@Override
				public boolean isForm() {
					return  true;
				}

				protected String[] getDefaultArgs() {
					return new String[0];
				}

			};
		}

		@Override
		protected boolean supportCellAction() {
			return false;
		}
	}

	/*
	 * 增加项的MenuDef
	 */
	protected class AddItemMenuDef extends MenuDef {
		public AddItemMenuDef() {
			this.setName(Inter.getLocText("Add"));
			this.setMnemonic('A');
			this.setIconPath("/com/fr/design/images/control/addPopup.png");
		}

		public void populate(String[] eventNames) {
			this.clearShortCuts();
			for (int i = 0; i < eventNames.length; i++) {
				final String eventname = eventNames[i];

				this.addShortCut(new UpdateAction() {
					{
						this.setName(switchLang(eventname));
					}

					public void actionPerformed(ActionEvent e) {
						NameObject nameable = new NameObject(createUnrepeatedName(switchLang(eventname)), new Listener(
								eventname,new JavaScriptImpl()));

						EventPropertyTable.this.addNameObject(nameable, EventPropertyTable.this.nameableList.getModel()
								.getSize());
						updateWidgetListener(creator);
					}
				});
			}
		}
	}
	
	private String switchLang(String eventName)	{
		return Inter.getLocText("Event-" + eventName);
	}

	/**
	 * 刷新
	 */
	public void refresh() {
		int selectionSize = designer.getSelectionModel().getSelection().size();
		if (selectionSize == 0 || selectionSize == 1) {
			this.creator = selectionSize == 0 ? designer.getRootComponent() : designer.getSelectionModel()
					.getSelection().getSelectedCreator();
		} else {
			this.creator = null;
			((DefaultListModel) nameableList.getModel()).removeAllElements();
			checkButtonEnabled();
			return;
		}
		Widget widget = creator.toData();

		refreshNameableCreator(EventCreator.createEventCreator(widget.supportedEvents(), WidgetEventListenerUpdatePane.class));

        ArrayList<NameObject> nameObjectList = new ArrayList<>();
		for (int i = 0, size = widget.getListenerSize(); i < size; i++) {
			Listener listener = widget.getListener(i);
			if (!listener.isDefault()) {
                nameObjectList.add(i, new NameObject(switchLang(listener.getEventName()) + (i + 1), listener));
            }
		}
        populate(nameObjectList.toArray(new NameObject[widget.getListenerSize()]));
		checkButtonEnabled();
		this.repaint();
    }

	/**
	 * 更新控件事件
	 * @param creator 控件
	 */
	public void updateWidgetListener(XCreator creator) {
        (creator.toData()).clearListeners();
        Nameable[] res = this.update();
        for (int i = 0; i < res.length; i++) {
            NameObject nameObject = (NameObject)res[i];
            (creator.toData()).addListener((Listener) nameObject.getObject());
        }

        designer.fireTargetModified();
		checkButtonEnabled();
	}

	@Override
	protected String title4PopupWindow() {
		return "Event";
	}

	@Override
	public NameableCreator[] createNameableCreators() {
		return new NameableCreator[]{
				new EventCreator(Widget.EVENT_STATECHANGE, WidgetEventListenerUpdatePane.class)
		};
	}

	@Override
	public void saveSettings() {
        if (isPopulating) {
            return;
        }
        updateWidgetListener(creator);
    }
}