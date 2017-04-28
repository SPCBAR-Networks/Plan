/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.java.main.java.com.djrapitops.plan.data.handling.info;

import java.util.UUID;
import main.java.com.djrapitops.plan.data.UserData;
import main.java.com.djrapitops.plan.data.handling.info.HandlingInfo;
import main.java.com.djrapitops.plan.data.handling.info.InfoType;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Rsl1122
 */
public class HandlingInfoTest {
    
    /**
     *
     */
    public HandlingInfoTest() {
    }

    /**
     *
     */
    @Test
    public void testGetUuid() {
        UUID uuid = UUID.fromString("45b0dfdb-f71d-4cf3-8c21-27c9d4c651db");
        HandlingInfo i = new HandlingInfo(uuid, InfoType.CHAT, 10L ) {
            @Override
            public boolean process(UserData data) {
                return true;
            }
        };
        assertEquals(uuid,i.getUuid());
    }

    /**
     *
     */
    @Test
    public void testGetType() {
        UUID uuid = UUID.fromString("45b0dfdb-f71d-4cf3-8c21-27c9d4c651db");
        HandlingInfo i = new HandlingInfo(uuid, InfoType.CHAT, 10L ) {
            @Override
            public boolean process(UserData data) {
                return true;
            }
        };
        assertEquals(InfoType.CHAT,i.getType());
    }

    /**
     *
     */
    @Test
    public void testGetTime() {
        UUID uuid = UUID.fromString("45b0dfdb-f71d-4cf3-8c21-27c9d4c651db");
        HandlingInfo i = new HandlingInfo(uuid, InfoType.CHAT, 10L ) {
            @Override
            public boolean process(UserData data) {
                return true;
            }
        };
        assertEquals(10L,i.getTime());
    }
}
