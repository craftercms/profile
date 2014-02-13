package org.craftercms.profile.repositories;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.craftercms.profile.constants.ProfileConstants;
import org.junit.Test;

/**
 * Created by cortiz on 1/28/14.
 */
public class Dump {

    @Test
    public void testName() throws Exception {
        String defaultFields = "         {\n" + "        \"_id\" :1,\n" + "        \"userName\" : 1," +
            "\n" + "        \"password\" : 1,\n" + "        \"active\" : 1,\n" + "        \"created\" : 1," +
            "\n" + "        \"modified\" : 1,\n" + "        \"tenantName\" : 1,\n" + "        \"email\" : 1," +
            "\n" + "        \"verify\" :1,\n" + "        \"roles\":1\n" + "        }";
        final List attributes = Arrays.asList("apples", "bananas", "oranges", "hippos", "ducks", "lions");

        System.out.println(builder.toString());
    }
}
