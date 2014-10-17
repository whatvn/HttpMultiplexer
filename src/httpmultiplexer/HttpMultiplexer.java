/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package httpmultiplexer;

/**
 *
 * @author hungnguyen
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import utils.Config;
import utils.LogUtil;
import java.lang.management.ManagementFactory;
import org.apache.log4j.Logger;
import org.eclipse.jetty.jmx.MBeanContainer;

import org.eclipse.jetty.plus.servlet.ServletHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import httpmultiplexer.httpproxy.ProxyServlet;

public class HttpMultiplexer
{

    public static void initialize()
    {
        //goi tat ca cac ham initialize cua cac business model theo thu tu
        //neu co local cache thi phai warmup local cache o muc do toi thieu
        //de tranh bi hieu ung dogpile tuc la tap trung vao 1 gia tri du lieu 
        //ko co tren cache va phai di lay o mot cho ko tin cay
    }

    public static void reinitialize()
    {
        //goi tat ca cac ham reinitialize cua cac business model theo thu tu
        //chi xay ra khi co su thay doi ve config hoac 1 vai dieu kien dac biet
    }

    public static void main(String[] args) throws Exception
    {
        LogUtil.init();
        int portListen = Integer.valueOf(System.getProperty("zport"));
        if (portListen == 0)
        {
            System.exit(-1);
        }
        //Noi sang he thong config center de lay config moi nhat
        //Khoi tao 1 tien trinh con de theo doi su thay doi cua cau hinh
        //Trong truong hop ko ket noi duoc den config center se co gang retry lai
        //neu sau 1 so lan se chay o che do safe mode tuc la chay config local
        //To do: 

        //ConfigClient.getConfig(LocalConfiguration.CONF_PATH);

        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        // Setup JMX
        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        mbContainer.addBean(Logger.getLogger(HttpMultiplexer.class));
        server.addBean(mbContainer);
        server.getContainer().addEventListener(mbContainer);



        int minThreads = Config.getParamInt("jetty_threadpool", "minthread", 200);
        int maxThreads = Config.getParamInt("jetty_threadpool", "maxthread", 2000);
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(minThreads);
        threadPool.setMaxThreads(maxThreads);
        server.setThreadPool(threadPool);
        connector.setPort(portListen);
        connector.setMaxIdleTime(30000);
        connector.setRequestHeaderSize(8192);
        server.setConnectors(new Connector[]
                {
                    connector
                });

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(ProxyServlet.class, "/*");
        //bat dau start server
        server.start();

        //ket thuc o day
        server.join();
    }
}