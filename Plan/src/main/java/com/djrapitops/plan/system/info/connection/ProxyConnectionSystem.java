/*
 * License is provided in the jar as LICENSE also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/LICENSE
 */
package com.djrapitops.plan.system.info.connection;

import com.djrapitops.plan.api.exceptions.connection.NoServersException;
import com.djrapitops.plan.api.exceptions.database.DBOpException;
import com.djrapitops.plan.system.database.DBSystem;
import com.djrapitops.plan.system.info.InfoSystem;
import com.djrapitops.plan.system.info.request.*;
import com.djrapitops.plan.system.info.server.Server;
import com.djrapitops.plan.system.info.server.ServerInfo;
import com.djrapitops.plan.system.webserver.WebServer;
import com.djrapitops.plugin.logging.L;
import com.djrapitops.plugin.logging.error.ErrorHandler;
import dagger.Lazy;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * ConnectionSystem for proxy servers.
 *
 * @author Rsl1122
 */
@Singleton
public class ProxyConnectionSystem extends ConnectionSystem {

    private final DBSystem dbSystem;
    private final Lazy<WebServer> webServer;
    private final ErrorHandler errorHandler;
    private final WebExceptionLogger webExceptionLogger;

    private long latestServerMapRefresh;

    @Inject
    public ProxyConnectionSystem(
            DBSystem dbSystem,
            Lazy<WebServer> webServer,
            ConnectionLog connectionLog,
            InfoRequests infoRequests,
            Lazy<InfoSystem> infoSystem,
            ServerInfo serverInfo,
            ErrorHandler errorHandler,
            WebExceptionLogger webExceptionLogger
    ) {
        super(connectionLog, infoRequests, infoSystem, serverInfo);
        this.dbSystem = dbSystem;
        this.webServer = webServer;
        this.errorHandler = errorHandler;
        this.webExceptionLogger = webExceptionLogger;
        latestServerMapRefresh = 0;
    }

    private void refreshServerMap() {
        if (latestServerMapRefresh < System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(15L)) {
            try {
                bukkitServers = dbSystem.getDatabase().fetch().getBukkitServers();
                latestServerMapRefresh = System.currentTimeMillis();
            } catch (DBOpException e) {
                errorHandler.log(L.ERROR, this.getClass(), e);
            }
        }
    }

    @Override
    protected Server selectServerForRequest(InfoRequest infoRequest) throws NoServersException {
        refreshServerMap();
        Server server = null;
        if (infoRequest instanceof CacheRequest
                || infoRequest instanceof GenerateInspectPageRequest
                || infoRequest instanceof GenerateInspectPluginsTabRequest) {
            // Run locally
            return serverInfo.getServer();
        } else if (infoRequest instanceof GenerateAnalysisPageRequest) {
            UUID serverUUID = ((GenerateAnalysisPageRequest) infoRequest).getServerUUID();
            server = bukkitServers.get(serverUUID);
        }
        if (server == null) {
            throw new NoServersException("Proper server is not available to process request: " + infoRequest.getClass().getSimpleName());
        }
        return server;
    }

    @Override
    public void sendWideInfoRequest(WideRequest infoRequest) throws NoServersException {
        refreshServerMap();
        if (bukkitServers.isEmpty()) {
            throw new NoServersException("No Servers available to make wide-request: " + infoRequest.getClass().getSimpleName());
        }
        for (Server server : bukkitServers.values()) {
            webExceptionLogger.logIfOccurs(this.getClass(), () -> sendInfoRequest(infoRequest, server));
        }
        // Quick hack for Bungee Plugins Tab
        if (infoRequest instanceof GenerateInspectPluginsTabRequest) {
            webExceptionLogger.logIfOccurs(this.getClass(), infoRequest::runLocally);
        }
    }

    @Override
    public boolean isServerAvailable() {
        return true;
    }

    @Override
    public String getMainAddress() {
        return webServer.get().getAccessAddress();
    }

    @Override
    public void enable() {
        super.enable();
        refreshServerMap();
    }

}