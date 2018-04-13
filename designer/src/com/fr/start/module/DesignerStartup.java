package com.fr.start.module;

import com.fr.design.DesignerEnvManager;
import com.fr.design.utils.DesignUtils;
import com.fr.general.ComparatorUtils;
import com.fr.module.Activator;
import com.fr.stable.CoreActivator;
import com.fr.stable.ProductConstants;
import com.fr.stable.module.ModuleListener;
import com.fr.start.Designer;
import com.fr.start.EnvSwitcher;
import com.fr.start.ReportSplashPane;
import com.fr.start.SplashWindow;
import com.fr.startup.activators.BasicActivator;

import java.io.File;

/**
 * Created by juhaoyu on 2018/1/8.
 */
public class DesignerStartup extends Activator {
    
    private static final int MESSAGE_PORT = 51462;
    
    private static final int DEBUG_PORT = 51463;
    
    @Override
    public void start() {
        
        startSub(PreStartActivator.class);
        
        if (checkMultiStart()) {
            return;
        }
        //启动基础部分
        startSub(BasicActivator.class);
        //启动画面
        SplashWindow splashWindow = createSplashWindow();
        String[] args = getModule().upFindSingleton(StartupArgs.class).get();
        Designer designer = new Designer(args);
        //启动env
        startSub(DesignerEnvProvider.class);
        //启动各个模块
        getSub(CoreActivator.class).start();
        getSub("designer").start();
        getRoot().getSingleton(EnvSwitcher.class).switch2LastEnv();
        //启动设计器界面
        designer.show(args);
        //启动画面结束
        splashWindow.setVisible(false);
        splashWindow.dispose();
        startSub(StartFinishActivator.class);
    }
    
    private SplashWindow createSplashWindow() {
        
        ReportSplashPane reportSplashPane = new ReportSplashPane();
        SplashWindow splashWindow = new SplashWindow(reportSplashPane);
        getModule().setSingleton(ModuleListener.class, reportSplashPane.getModuleListener());
        return splashWindow;
    }
    
    private boolean checkMultiStart() {
        
        if (isDebug()) {
            setDebugEnv();
        } else {
            DesignUtils.setPort(getStartPort());
        }
        // 如果端口被占用了 说明程序已经运行了一次,也就是说，已经建立一个监听服务器，现在只要给服务器发送命令就好了
        if (DesignUtils.isStarted()) {
            DesignUtils.clientSend(getModule().upFindSingleton(StartupArgs.class).get());
            return true;
        }
        return false;
    }
    
    private int getStartPort() {
        
        return MESSAGE_PORT;
    }
    
    
    //在VM options里加入-Ddebug=true激活
    private boolean isDebug() {
        
        return ComparatorUtils.equals("true", System.getProperty("debug"));
    }
    
    
    //端口改一下，环境配置文件改一下。便于启动两个设计器，进行对比调试
    private void setDebugEnv() {
        
        DesignUtils.setPort(DEBUG_PORT);
        DesignerEnvManager.setEnvFile(new File(ProductConstants.getEnvHome() + File.separator + ProductConstants.APP_NAME + "Env_debug.xml"));
    }
    
    
    @Override
    public void stop() {
    
    }
}