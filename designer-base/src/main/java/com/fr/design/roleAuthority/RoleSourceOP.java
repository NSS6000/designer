package com.fr.design.roleAuthority;import com.fr.general.NameObject;import com.fr.design.gui.itree.refreshabletree.ExpandMutableTreeNode;import com.fr.design.gui.itree.refreshabletree.UserObjectOP;import java.util.*;/** * Author : daisy * Date: 13-8-30 * Time: 下午3:36 */public class RoleSourceOP implements UserObjectOP<RoleDataWrapper> {	private static final int REPORT_PLATEFORM_MANAGE = 0;	private static final int FS_MANAGE = 1;	public static int manageMode = -1;	public RoleSourceOP() {		super();	}	public List<Map<String, RoleDataWrapper>> init() {		//用于存放角色		List<Map<String, RoleDataWrapper>> allRoles = new ArrayList<Map<String, RoleDataWrapper>>();		Map<String, RoleDataWrapper> report_roles = new LinkedHashMap<String, RoleDataWrapper>();		Map<String, RoleDataWrapper> FS_roles = new LinkedHashMap<String, RoleDataWrapper>();		addReportRoles(report_roles);		addFSRoles(FS_roles);		allRoles.add(report_roles);		allRoles.add(FS_roles);		return allRoles;	}	/**	 * 获取报表平台的角色	 */	protected  void addReportRoles(Map<String, RoleDataWrapper> report_roles) {		RoleDataWrapper tdw = new RoleDataWrapper(com.fr.design.i18n.Toolkit.i18nText("M_Server-Platform_Manager"));		report_roles.put(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Role"), tdw);	}	/**	 * 获取数据决策系统的角色	 */	protected  void addFSRoles(Map<String, RoleDataWrapper> FS_roles) {		RoleDataWrapper tdw = new RoleDataWrapper(com.fr.design.i18n.Toolkit.i18nText("FS_Name"));		FS_roles.put(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Role"), tdw);	}	public boolean interceptButtonEnabled() {		return true;	}	/**	 * 移除名字是name的角色	 *	 * @param name	 */	public void removeAction(String name) {		//删除对应模式下的角色		switch (manageMode) {			case REPORT_PLATEFORM_MANAGE:				//删除报表平台的角色				break;			case FS_MANAGE:				//删除数据决策系统的角色				break;		}	}	public void addAction(String name) {		switch (manageMode) {			case REPORT_PLATEFORM_MANAGE:				//增加报表平台的角色				break;			case FS_MANAGE:				//增加数据决策系统的角色				break;		}	}	public void rename(String oldName, String newName) {		switch (manageMode) {			case REPORT_PLATEFORM_MANAGE:				//修改报表平台的角色				break;			case FS_MANAGE:				//修改数据决策系统的角色				break;		}	}	/**	 * 根据不同模式生成子节点	 *	 * @return	 */	@Override	public ExpandMutableTreeNode[] load() {		Map<String, RoleDataWrapper> report_roles = null;		Map<String, RoleDataWrapper> FS_roles = null;		if (this != null) {			report_roles = this.init().get(0);			FS_roles = this.init().get(1);		} else {			report_roles = Collections.emptyMap();			FS_roles = Collections.emptyMap();		}		List<ExpandMutableTreeNode> list = new ArrayList<ExpandMutableTreeNode>(); //所有的角色		List<ExpandMutableTreeNode> reportlist = new ArrayList<ExpandMutableTreeNode>(); //报表平台橘色		List<ExpandMutableTreeNode> FSlist = new ArrayList<ExpandMutableTreeNode>();   //数据决策系统角色		list.add(initReportRolseNode(report_roles));		addNodeToList(report_roles, reportlist);		list.add(initFSRolseNode(FS_roles));		addNodeToList(FS_roles, FSlist);		switch (manageMode) {			case REPORT_PLATEFORM_MANAGE:				return reportlist.toArray(new ExpandMutableTreeNode[reportlist.size()]);			case FS_MANAGE:				return FSlist.toArray(new ExpandMutableTreeNode[FSlist.size()]);			default:				return list.toArray(new ExpandMutableTreeNode[list.size()]);		}	}	protected void setDataMode(int i) {		manageMode = i;	}	protected  void addNodeToList(Map<String, RoleDataWrapper> roleMap, List<ExpandMutableTreeNode> roleList) {		ExpandMutableTreeNode[] roleNode = getNodeArrayFromMap(roleMap);		for (int i = 0; i < roleNode.length; i++) {			roleList.add(roleNode[i]);		}	}	protected ExpandMutableTreeNode initReportRolseNode(Map<String, RoleDataWrapper> report_roles) {		ExpandMutableTreeNode templateNode = new ExpandMutableTreeNode(new NameObject(com.fr.design.i18n.Toolkit.i18nText("M_Server-Platform_Manager"), REPORT_PLATEFORM_MANAGE), true);		templateNode.addChildTreeNodes(getNodeArrayFromMap(report_roles));		return templateNode;	}	protected  ExpandMutableTreeNode initFSRolseNode(Map<String, RoleDataWrapper> FS_roles) {		ExpandMutableTreeNode templateNode = new ExpandMutableTreeNode(new NameObject(com.fr.design.i18n.Toolkit.i18nText("FS_Name"), FS_MANAGE), true);		templateNode.addChildTreeNodes(getNodeArrayFromMap(FS_roles));		return templateNode;	}	protected  ExpandMutableTreeNode[] getNodeArrayFromMap(Map<String, RoleDataWrapper> map) {		List<ExpandMutableTreeNode> roleList = new ArrayList<ExpandMutableTreeNode>();		Iterator<Map.Entry<String, RoleDataWrapper>> entryIt = map.entrySet().iterator();		while (entryIt.hasNext()) {			Map.Entry<String, RoleDataWrapper> entry = entryIt.next();			String name = entry.getKey();			RoleDataWrapper t = entry.getValue();			ExpandMutableTreeNode newChildTreeNode = new ExpandMutableTreeNode(new NameObject(name, t));			roleList.add(newChildTreeNode);			newChildTreeNode.add(new ExpandMutableTreeNode());		}		return roleList.toArray(new ExpandMutableTreeNode[roleList.size()]);	}}