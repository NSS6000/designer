package com.fr.design.mainframe.alphafine.search.manager;

import com.fr.design.DesignerEnvManager;
import com.fr.design.mainframe.alphafine.AlphaFineConstants;
import com.fr.design.mainframe.alphafine.AlphaFineHelper;
import com.fr.design.mainframe.alphafine.CellType;
import com.fr.design.mainframe.alphafine.cell.model.ActionModel;
import com.fr.design.mainframe.alphafine.cell.model.MoreModel;
import com.fr.design.mainframe.alphafine.model.SearchResult;
import com.fr.design.mainframe.toolbar.UpdateActionManager;
import com.fr.design.mainframe.toolbar.UpdateActionModel;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.stable.StringUtils;

import java.util.List;

/**
 * Created by XiaXiang on 2017/3/27.
 */
public class ActionSearchManager implements AlphaFineSearchProcessor {
    private static ActionSearchManager actionSearchManager = null;
    private SearchResult filterModelList;
    private SearchResult lessModelList;
    private SearchResult moreModelList;
    private static final MoreModel TITLE_MODEL = new MoreModel(Inter.getLocText("FR-Designer_Set"), CellType.ACTION);

    public synchronized static ActionSearchManager getActionSearchManager() {
        if (actionSearchManager == null) {
            actionSearchManager = new ActionSearchManager();
        }
        return actionSearchManager;
    }

    @Override
    public synchronized SearchResult getLessSearchResult(String searchText) {
        filterModelList = new SearchResult();
        lessModelList = new SearchResult();
        moreModelList = new SearchResult();
        if (StringUtils.isBlank(searchText)) {
            lessModelList.add(TITLE_MODEL);
            return lessModelList;
        }
        if (DesignerEnvManager.getEnvManager().getAlphaFineConfigManager().isContainAction()) {
            List<UpdateActionModel> updateActions = UpdateActionManager.getUpdateActionManager().getUpdateActions();
            for (UpdateActionModel updateActionModel : updateActions) {
                if (StringUtils.isNotBlank(updateActionModel.getSearchKey())) {
                    if (updateActionModel.getSearchKey().contains(searchText) && updateActionModel.getAction().isEnabled()) {
                        filterModelList.add(new ActionModel(updateActionModel.getActionName(), updateActionModel.getParentName(), updateActionModel.getAction()));
                    }
                }
            }
            SearchResult result = new SearchResult();
            for (Object object : filterModelList) {
                if (!AlphaFineHelper.getFilterResult().contains(object)) {
                    result.add(object);
                }

            }
            if (result.size() < AlphaFineConstants.SHOW_SIZE + 1) {
                lessModelList.add(0, TITLE_MODEL);
                if (result.size() == 0) {
                    lessModelList.add(AlphaFineHelper.NO_RESULT_MODEL);
                } else {
                    lessModelList.addAll(result);
                }
            } else {
                lessModelList.add(0, new MoreModel(Inter.getLocText("FR-Designer_Set"), Inter.getLocText("FR-Designer_AlphaFine_ShowAll"), true, CellType.ACTION));
                lessModelList.addAll(result.subList(0, AlphaFineConstants.SHOW_SIZE));
                moreModelList.addAll(result.subList(AlphaFineConstants.SHOW_SIZE, result.size()));
            }

        }
        return lessModelList;
    }

    @Override
    public SearchResult getMoreSearchResult() {
        return moreModelList;
    }

    /**
     * 根据类名获取对象
     * @param actionName
     * @return
     */
    public static ActionModel getModelFromCloud(String actionName ) {
        List<UpdateActionModel> updateActions = UpdateActionManager.getUpdateActionManager().getUpdateActions();
        for (UpdateActionModel updateActionModel : updateActions) {
            if (ComparatorUtils.equals(actionName, updateActionModel.getClassName()) && updateActionModel.getAction().isEnabled()) {
                return new ActionModel(updateActionModel.getActionName(), updateActionModel.getParentName(), updateActionModel.getAction());
            }
        }
        return null;
    }
}