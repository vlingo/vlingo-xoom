// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom;

import io.vlingo.actors.Configuration;
import io.vlingo.actors.Grid;
import io.vlingo.cluster.model.Properties;
import io.vlingo.lattice.grid.GridClusterProperties;

public class Boot {

    private static String resolvedNodeName;
    private static Properties clusterProperties;
    private static final int defaultPort = 18080;

    public static Grid start(final String worldName) throws Exception {
        return start(worldName, null);
    }

    public static Grid start(final String worldName, final String nodeName) throws Exception {
        return start(worldName, nodeName, null);
    }

    public static Grid start(final String worldName, final String nodeName, final Properties customProperties) throws Exception {
        if(customProperties == null) {
            System.out.println("Unable to find vlingo-cluster.properties. Using default grid cluster settings.");
            clusterProperties = GridClusterProperties.oneNode();
        } else {
            clusterProperties = customProperties;
        }

        resolvedNodeName = resolveNodeName(nodeName);

        final Configuration configuration = Configuration.define();
        final Grid grid = Grid.start(worldName, configuration, clusterProperties, resolvedNodeName);

        return grid;
    }

    public static int serverPort() {
        return clusterProperties.getInteger(resolvedNodeName, "server.port", defaultPort);
    }

    private static String resolveNodeName(final String nodeName) {
        if(nodeName == null || nodeName.isEmpty()) {
            return clusterProperties.seedNodes().get(0);
        }
        return nodeName;
    }

}