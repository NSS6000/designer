package com.fr.design.mainframe.templateinfo;

import com.fr.base.FRContext;
import com.fr.base.io.IOFile;
import com.fr.design.DesignerEnvManager;
import com.fr.design.mainframe.DesignerContext;
import com.fr.design.mainframe.JTemplate;
import com.fr.env.RemoteEnv;
import com.fr.general.*;
import com.fr.general.http.HttpClient;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.stable.*;
import com.fr.stable.xml.*;
import com.fr.third.javax.xml.stream.XMLStreamException;
import com.fr.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 做模板的过程和耗时收集，辅助类
 * Created by plough on 2017/2/21.
 */
public class TemplateInfoCollector<T extends IOFile> implements XMLReadable, XMLWriter {
    private static final String FILE_NAME = "tpl.info";
    private static TemplateInfoCollector instance;
    private Map<String, HashMap<String, Object>> templateInfoList;
    private String designerOpenDate;  //设计器最近一次打开日期
    private static final int VALID_CELL_COUNT = 5;  // 有效报表模板的格子数
    private static final int VALID_WIDGET_COUNT = 5;  // 有效报表模板的控件数
    private static final int COMPLETE_DAY_COUNT = 15;  // 判断模板是否完成的天数
    private static final int ONE_THOUSAND = 1000;
    static final long serialVersionUID = 2007L;
    private static final String XML_DESIGNER_OPEN_DATE = "DesignerOpenDate";
    private static final String XML_TEMPLATE_INFO_LIST = "TemplateInfoList";
    private static final String XML_TEMPLATE_INFO = "TemplateInfo";
    private static final String XML_PROCESS_MAP = "ProcessMap";
    private static final String XML_CONSUMING_MAP = "ConsumingMap";
    private static final String ATTR_DAY_COUNT = "dayCount";
    private static final String ATTR_TEMPLATE_ID = "templateID";
    /*
    * "process":"","float_count":0,"widget_count":1,"cell_count":0,"block_count":0,"report_type":2
    * */
    private static final String ATTR_PROCESS = "process";
    private static final String ATTR_FLOAT_COUNT = "floatCount";
    private static final String ATTR_WIDGET_COUNT = "widgetCount";
    private static final String ATTR_CELL_COUNT = "cellCount";
    private static final String ATTR_BLOCK_COUNT = "blockCount";
    private static final String ATTR_REPORT_TYPE = "reportType";
    /*
    * "activitykey":"2e0ea413-fa9c241e0-9723-4354fce51e81","jar_time":"2017.04.21.10.41.10.806",
    * "create_time":"2017-04-26 10:27","templateID":"0aa19027-e298-4d43-868d-45232b879d0e",
    * "uuid":"476ca2cc-f789-4c5d-8e89-ef146580775c","time_consume":36,"version":"8.0",
    * "username":"ueeHq6
    * */
    private static final String ATTR_ACTIVITYKEY = "activitykey";
    private static final String ATTR_JAR_TIME = "jarTime";
    private static final String ATTR_CREATE_TIME = "createTime";
    private static final String ATTR_UUID = "uuid";
    private static final String ATTR_TIME_CONSUME = "timeConsume";
    private static final String ATTR_VERSION = "version";
    private static final String ATTR_USERNAME = "username";



    @SuppressWarnings("unchecked")
    private TemplateInfoCollector() {
        templateInfoList = new HashMap<>();
        setDesignerOpenDate();
    }

    /**
     * 把设计器最近打开日期设定为当前日期
     */
    private void setDesignerOpenDate() {
        designerOpenDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }

    /**
     * 判断今天是否第一次打开设计器
     */
    private boolean designerOpenFirstTime() {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        return !ComparatorUtils.equals(today, designerOpenDate);
    }

    /**
     * 获取缓存文件存放路径
     */
    private static File getInfoFile() {
        return new File(StableUtils.pathJoin(ProductConstants.getEnvHome(), FILE_NAME));
    }

    public static TemplateInfoCollector getInstance() {
        if (instance == null) {
            instance = new TemplateInfoCollector();
            readXMLFile(instance, getInfoFile());
        }
        return instance;
    }

    private static void readXMLFile(XMLReadable xmlReadable, File xmlFile){
        if (xmlFile == null || !xmlFile.exists()) {
            return;
        }
        String charset = EncodeConstants.ENCODING_UTF_8;
        try {
            String fileContent = getFileContent(xmlFile);
            InputStream xmlInputStream = new ByteArrayInputStream(fileContent.getBytes(charset));
            InputStreamReader inputStreamReader = new InputStreamReader(xmlInputStream, charset);
            XMLableReader xmlReader = XMLableReader.createXMLableReader(inputStreamReader);

            if (xmlReader != null) {
                xmlReader.readXMLObject(xmlReadable);
            }
            xmlInputStream.close();
        } catch (FileNotFoundException e) {
            FRContext.getLogger().error(e.getMessage());
        } catch (IOException e) {
            FRContext.getLogger().error(e.getMessage());
        } catch (XMLStreamException e) {
            FRContext.getLogger().error(e.getMessage());
        }

    }

    private static String getFileContent(File xmlFile) throws FileNotFoundException, UnsupportedEncodingException{
        InputStream is = new FileInputStream(xmlFile);
        return IOUtils.inputStream2String(is);
    }

    private boolean shouldCollectInfo() {
        if (FRContext.getCurrentEnv() instanceof RemoteEnv) {  // 远程设计不收集数据
            return false;
        }
        return DesignerEnvManager.getEnvManager().isJoinProductImprove() && FRContext.isChineseEnv();
    }

    public void appendProcess(String log) {
        if (!shouldCollectInfo()) {
            return;
        }
        // 获取当前编辑的模板
        JTemplate jt = DesignerContext.getDesignerFrame().getSelectedJTemplate();
        // 追加过程记录
        jt.appendProcess(log);
    }

    /**
     * 加载已经存储的模板过程
     */
    @SuppressWarnings("unchecked")
    public String loadProcess(T t) {
        HashMap<String, Object> processMap = (HashMap<String, Object>) templateInfoList.get(t.getTemplateID()).get("processMap");
        return (String)processMap.get("process");
    }

    /**
     * 根据模板ID是否在收集列表中，判断是否需要收集当前模板的信息
     */
    public boolean inList(T t) {
        return templateInfoList.containsKey(t.getTemplateID());
    }

    /**
     * 将包含所有信息的对象保存到文件
     */
    private void saveInfo() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLTools.writeOutputStreamXML(this, out);
            out.flush();
            out.close();
            String fileContent = new String(out.toByteArray(), EncodeConstants.ENCODING_UTF_8);
            writeContentToFile(fileContent, getInfoFile());
        } catch (Exception ex) {
            FRLogger.getLogger().error(ex.getMessage());
        }
    }

    /**
     * 将文件内容写到输出流中
     */
    private static void writeContentToFile(String fileContent, File file){
        BufferedWriter bw = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, EncodeConstants.ENCODING_UTF_8);
            bw = new BufferedWriter(osw);
            bw.write(fileContent);
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage());
        } finally {
            if(bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                }
            }
        }

    }

    /**
     * 更新 day_count：打开设计器却未编辑模板的连续日子
     */
    private void addDayCount() {
        if (designerOpenFirstTime()) {
            for (String key : templateInfoList.keySet()) {
                HashMap<String, Object> templateInfo = templateInfoList.get(key);
                int dayCount = (int)templateInfo.get("day_count") + 1;
                templateInfo.put("day_count", dayCount);
            }
            setDesignerOpenDate();
        }
    }

    /**
     * 收集模板信息。如果之前没有记录，则新增；如果已有记录，则更新。
     * 同时将最新数据保存到文件中。
     */
    @SuppressWarnings("unchecked")
    public void collectInfo(T t, JTemplate jt, long openTime, long saveTime) {
        if (!shouldCollectInfo()) {
            return;
        }

        HashMap<String, Object> templateInfo;

        long timeConsume = ((saveTime - openTime) / ONE_THOUSAND);  // 制作模板耗时（单位：s）
        String templateID = t.getTemplateID();

        if (inList(t)) { // 已有记录
            templateInfo = templateInfoList.get(t.getTemplateID());
            // 更新 conusmingMap
            HashMap<String, Object> consumingMap = (HashMap<String, Object>) templateInfo.get("consumingMap");
            timeConsume += (long)consumingMap.get("time_consume");  // 加上之前的累计编辑时间
            consumingMap.put("time_consume", timeConsume);
        }
        else {  // 新增
            templateInfo = new HashMap<>();
            templateInfo.put("consumingMap", getNewConsumingMap(templateID, openTime, timeConsume));
        }

        // 直接覆盖 processMap
        templateInfo.put("processMap", getProcessMap(templateID, jt));

        // 保存模板时，让 day_count 归零
        templateInfo.put("day_count", 0);


        templateInfoList.put(templateID, templateInfo);

        saveInfo();  // 每次更新之后，都同步到暂存文件中
    }

    private HashMap<String, Object> getNewConsumingMap(String templateID, long openTime, long timeConsume) {
        HashMap<String, Object> consumingMap = new HashMap<>();

        String username = DesignerEnvManager.getEnvManager().getBBSName();
        String uuid = DesignerEnvManager.getEnvManager().getUUID();
        String activitykey = DesignerEnvManager.getEnvManager().getActivationKey();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
        String jarTime = GeneralUtils.readBuildNO();
        String version = ProductConstants.VERSION;
        consumingMap.put("username", username);
        consumingMap.put("uuid", uuid);
        consumingMap.put("activitykey", activitykey);
        consumingMap.put("templateID", templateID);
        consumingMap.put("create_time", createTime);
        consumingMap.put("time_consume", timeConsume);
        consumingMap.put("jar_time", jarTime);
        consumingMap.put("version", version);

        return consumingMap;
    }

    private HashMap<String, Object> getProcessMap(String templateID, JTemplate jt) {
        HashMap<String, Object> processMap = new HashMap<>();

        processMap.put("templateID", templateID);
        processMap.put("process", jt.getProcess());

        TemplateProcessInfo info = jt.getProcessInfo();
        processMap.put("report_type", info.getReportType());
        processMap.put("cell_count", info.getCellCount());
        processMap.put("float_count", info.getFloatCount());
        processMap.put("block_count", info.getBlockCount());
        processMap.put("widget_count", info.getWidgetCount());

        return processMap;
    }

    /**
     * 发送本地模板信息到服务器
     */
    public void sendTemplateInfo() {
        addDayCount();
        String consumingUrl = SiteCenter.getInstance().acquireUrlByKind("tempinfo.consuming") + "/single";
        String processUrl = SiteCenter.getInstance().acquireUrlByKind("tempinfo.process") + "/single";
        ArrayList<HashMap<String, String>> completeTemplatesInfo = getCompleteTemplatesInfo();
        for (HashMap<String, String> templateInfo : completeTemplatesInfo) {
            String jsonConsumingMap = templateInfo.get("jsonConsumingMap");
            String jsonProcessMap = templateInfo.get("jsonProcessMap");
            if (sendSingleTemplateInfo(consumingUrl, jsonConsumingMap) && sendSingleTemplateInfo(processUrl, jsonProcessMap)) {
                // 清空记录
                removeFromTemplateInfoList(templateInfo.get("templateID"));
            }
        }
        saveInfo();
    }

    private boolean sendSingleTemplateInfo(String url, String content) {
        HashMap<String, String> para = new HashMap<>();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        para.put("token", CodeUtils.md5Encode(date, "", "MD5"));
        para.put("content", content);
        HttpClient httpClient = new HttpClient(url, para, true);
        httpClient.setTimeout(5000);
        httpClient.asGet();

        if (!httpClient.isServerAlive()) {
            return false;
        }

        String res =  httpClient.getResponseText();
        boolean success;
        try {
            success = ComparatorUtils.equals(new JSONObject(res).get("status"), "success");
        } catch (Exception ex) {
            success = false;
        }
        return success;
    }

    /**
     * 返回已完成的模板信息
     */
    @SuppressWarnings("unchecked")
    private ArrayList<HashMap<String, String>> getCompleteTemplatesInfo() {
        ArrayList<HashMap<String, String>> completeTemplatesInfo = new ArrayList<>();
        ArrayList<String> testTemplateKeys = new ArrayList<>();  // 保存测试模板的key
        for (String key : templateInfoList.keySet()) {
            HashMap<String, Object> templateInfo = templateInfoList.get(key);
            if ((int)templateInfo.get("day_count") <= COMPLETE_DAY_COUNT) {  // 未完成模板
                continue;
            }
            if (isTestTemplate(templateInfo)) {
                testTemplateKeys.add(key);
                continue;
            }
            HashMap<String, Object> consumingMap = (HashMap<String, Object>) templateInfo.get("consumingMap");
            HashMap<String, Object> processMap = (HashMap<String, Object>) templateInfo.get("processMap");
            String jsonConsumingMap = new JSONObject(consumingMap).toString();
            String jsonProcessMap = new JSONObject(processMap).toString();
            HashMap<String, String> jsonTemplateInfo = new HashMap<>();
            jsonTemplateInfo.put("jsonConsumingMap", jsonConsumingMap);
            jsonTemplateInfo.put("jsonProcessMap", jsonProcessMap);
            jsonTemplateInfo.put("templateID", key);
            completeTemplatesInfo.add(jsonTemplateInfo);
        }
        // 删除测试模板
        for (String key : testTemplateKeys) {
            removeFromTemplateInfoList(key);
        }
        return completeTemplatesInfo;
    }

    private void removeFromTemplateInfoList(String key) {
        templateInfoList.remove(key);
    }

    @SuppressWarnings("unchecked")
    private boolean isTestTemplate(HashMap<String, Object> templateInfo) {
        HashMap<String, Object> processMap = (HashMap<String, Object>) templateInfo.get("processMap");
        int reportType = (int)processMap.get("report_type");
        int cellCount = (int)processMap.get("cell_count");
        int floatCount = (int)processMap.get("float_count");
        int blockCount = (int)processMap.get("block_count");
        int widgetCount = (int)processMap.get("widget_count");
        boolean isTestTemplate = false;
        if (reportType == 0) {  // 普通报表
            isTestTemplate = cellCount <= VALID_CELL_COUNT && floatCount <= 1 && widgetCount <= VALID_WIDGET_COUNT;
        } else if (reportType == 1) {  // 聚合报表
            isTestTemplate = blockCount <= 1 && widgetCount <= VALID_WIDGET_COUNT;
        } else {  // 表单(reportType == 2)
            isTestTemplate = widgetCount <= 1;
        }
        return isTestTemplate;
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    private static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    private static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            try {
                String name = reader.getTagName();
                if (XML_DESIGNER_OPEN_DATE.equals(name)) {
                    this.designerOpenDate = reader.getElementValue();
                } else if(XML_TEMPLATE_INFO_LIST.equals(name)){
//                    JSONObject jsonObject = new JSONObject(reader.getElementValue());
//                    Map<String, Object> map = jsonToMap(jsonObject);
//                    for (Map.Entry<String, Object> entry : map.entrySet()) {
//                        if (entry.getValue() instanceof HashMap) {
//                            this.templateInfoList.put(entry.getKey(), (HashMap<String, Object>) entry.getValue());
//                        }
//                    }
                    readTemplateInfoList(reader);
                }
            } catch (Exception ex) {
                // 什么也不做，使用默认值
            }
        }
    }

    private void readTemplateInfoList(XMLableReader reader) {
        reader.readXMLObject(new XMLReadable() {
            public void readXML(XMLableReader reader) {
                if (XML_TEMPLATE_INFO.equals(reader.getTagName())) {
                    TemplateInfo templateInfo = new TemplateInfo();
                    reader.readXMLObject(templateInfo);
                    templateInfoList.put(templateInfo.getTemplateID(), templateInfo.getTemplateInfo());
                }
            }
        });
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG("TplInfo");

        writer.startTAG(XML_DESIGNER_OPEN_DATE);
        writer.textNode(designerOpenDate);
        writer.end();

//        writer.startTAG(XML_TEMPLATE_INFO_LIST);
//        writer.textNode(new JSONObject(templateInfoList).toString());
//        writer.end();
        writeTemplateInfoList(writer);

        writer.end();
    }

    private void writeTemplateInfoList(XMLPrintWriter writer){
        //启停
        writer.startTAG(XML_TEMPLATE_INFO_LIST);
        for (String templateID : templateInfoList.keySet()) {
            new TemplateInfo(templateInfoList.get(templateID)).writeXML(writer);
        }
//        for (int i = 0; i < templateInfoList.size(); i++) {
//            startStop.get(i).writeXML(writer);
//        }
        writer.end();
    }

    private class TemplateInfo implements XMLReadable, XMLWriter {

        private int dayCount;
        private String templateID;
        private HashMap<String, Object> processMap = new HashMap<>();
        private HashMap<String, Object> consumingMap = new HashMap<>();

        @SuppressWarnings("unchecked")
        public TemplateInfo(HashMap<String, Object> templateInfo) {
            this.dayCount = (int)templateInfo.get("day_count");
            this.processMap = (HashMap<String, Object>) templateInfo.get("processMap");
            this.consumingMap = (HashMap<String, Object>) templateInfo.get("consumingMap");
            this.templateID = (String) processMap.get("templateID");
        }

        public TemplateInfo() {}

//        public int getDayCount() {
//            return dayCount;
//        }
        public String getTemplateID() {
            return templateID;
        }

//        public void setStartDate(String startDate) {
//            this.startDate = startDate;
//        }

        public HashMap<String, Object> getTemplateInfo() {
            HashMap<String, Object> templateInfo = new HashMap<>();
            templateInfo.put("processMap", processMap);
            templateInfo.put("consumingMap", consumingMap);
            templateInfo.put("day_count", dayCount);
            return templateInfo;
        }

//        public HashMap<String, Object> getProcessMap() {
//            return processMap;
//        }
//
//        public HashMap<String, Object> getConsumingMap() {
//            return consumingMap;
//        }

//        public void setStopDate(String endDate) {
//            this.stopDate = endDate;
//        }

        public void writeXML(XMLPrintWriter writer) {
            writer.startTAG(XML_TEMPLATE_INFO);
            if (StringUtils.isNotEmpty(templateID)) {
                writer.attr(ATTR_TEMPLATE_ID, this.templateID);
            }
            if (dayCount >= 0) {
                writer.attr(ATTR_DAY_COUNT, this.dayCount);
            }
            writeProcessMap(writer);
            writeConsumingMap(writer);

            writer.end();
        }

        private void writeProcessMap(XMLPrintWriter writer) {
            writer.startTAG(XML_PROCESS_MAP);
            writer.attr(ATTR_PROCESS, (String)processMap.get("process"));
            writer.attr(ATTR_FLOAT_COUNT, (int)processMap.get("float_count"));
            writer.attr(ATTR_WIDGET_COUNT, (int)processMap.get("widget_count"));
            writer.attr(ATTR_CELL_COUNT, (int)processMap.get("cell_count"));
            writer.attr(ATTR_BLOCK_COUNT, (int)processMap.get("block_count"));
            writer.attr(ATTR_REPORT_TYPE, (int)processMap.get("report_type"));
            writer.end();
        }

        private void writeConsumingMap(XMLPrintWriter writer) {
            writer.startTAG(XML_CONSUMING_MAP);
            writer.attr(ATTR_ACTIVITYKEY, (String)consumingMap.get("activitykey"));
            writer.attr(ATTR_JAR_TIME, (String)consumingMap.get("jar_time"));
            writer.attr(ATTR_CREATE_TIME, (String)consumingMap.get("create_time"));
            writer.attr(ATTR_UUID, (String)consumingMap.get("uuid"));
            writer.attr(ATTR_TIME_CONSUME, (long)consumingMap.get("time_consume"));
            writer.attr(ATTR_VERSION, (String)consumingMap.get("version"));
            writer.attr(ATTR_USERNAME, (String)consumingMap.get("username"));
            writer.end();
        }

        public void readXML(XMLableReader reader) {
            if (!reader.isChildNode()) {
                dayCount = reader.getAttrAsInt(ATTR_DAY_COUNT, 0);
                templateID = reader.getAttrAsString(ATTR_TEMPLATE_ID, StringUtils.EMPTY);
            } else {
                try {
                    String name = reader.getTagName();
                    if (XML_PROCESS_MAP.equals(name)) {
                        processMap.put("process", reader.getAttrAsString(ATTR_PROCESS, StringUtils.EMPTY));
                        processMap.put("float_count", reader.getAttrAsInt(ATTR_FLOAT_COUNT, 0));
                        processMap.put("widget_count", reader.getAttrAsInt(ATTR_WIDGET_COUNT, 0));
                        processMap.put("cell_count", reader.getAttrAsInt(ATTR_CELL_COUNT, 0));
                        processMap.put("block_count", reader.getAttrAsInt(ATTR_BLOCK_COUNT, 0));
                        processMap.put("report_type", reader.getAttrAsInt(ATTR_REPORT_TYPE, 0));
                        processMap.put("templateID", templateID);
                    } else if(XML_CONSUMING_MAP.equals(name)){
                        consumingMap.put("activitykey", reader.getAttrAsString(ATTR_ACTIVITYKEY, StringUtils.EMPTY));
                        consumingMap.put("jar_time", reader.getAttrAsString(ATTR_JAR_TIME, StringUtils.EMPTY));
                        consumingMap.put("create_time", reader.getAttrAsString(ATTR_CREATE_TIME, StringUtils.EMPTY));
                        consumingMap.put("templateID", templateID);
                        consumingMap.put("uuid", reader.getAttrAsString(ATTR_UUID, StringUtils.EMPTY));
                        consumingMap.put("time_consume", reader.getAttrAsLong(ATTR_TIME_CONSUME, 0));
                        consumingMap.put("version", reader.getAttrAsString(ATTR_VERSION, "8.0"));
                        consumingMap.put("username", reader.getAttrAsString(ATTR_USERNAME, StringUtils.EMPTY));
                    }
                } catch (Exception ex) {
                    // 什么也不做，使用默认值
                }
            }
        }

    }


    public static void main(String[] args) {
        TemplateInfoCollector tic = TemplateInfoCollector.getInstance();
        tic.sendTemplateInfo();
    }
}
