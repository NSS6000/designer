package com.fr.design.javascript;import com.fr.data.ClassSubmitJob;import com.fr.design.write.submit.CustomSubmitJobPane;import com.fr.design.beans.FurtherBasicBeanPane;import com.fr.js.CustomActionJavaScript;import java.awt.*;/** * Author : Shockway * Date: 13-8-12 * Time: 下午7:47 */public class CustomActionPane extends FurtherBasicBeanPane<CustomActionJavaScript> {	CustomSubmitJobPane classPane = new CustomSubmitJobPane();	public CustomActionPane() {		this.setLayout(new BorderLayout());		this.add(classPane, BorderLayout.CENTER);	}	/**	 * 判断界面是否为js 传入	 * @param ob 对象是否为js	 * @return 是否是js对象	 */	@Override	public boolean accept(Object ob) {		return ob instanceof CustomActionJavaScript;	}	/**	 * 标题	 * @return 标题	 */	@Override	public String title4PopupWindow() {		return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Submit_Type_Custom");	}	/**	 * 重置	 */	@Override	public void reset() {		this.classPane.reset();	}	@Override	public void populateBean(CustomActionJavaScript ob) {		classPane.populateBean(ob.getJob());	}	@Override	public CustomActionJavaScript updateBean() {		CustomActionJavaScript cs = new CustomActionJavaScript();		cs.setJob((ClassSubmitJob)classPane.updateBean());		return cs;	}}