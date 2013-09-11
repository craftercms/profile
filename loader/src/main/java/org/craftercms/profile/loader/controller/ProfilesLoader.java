package org.craftercms.profile.loader.controller;

import org.apache.log4j.PropertyConfigurator;
import org.craftercms.profile.loader.services.ProfileLoaderService;
import org.craftercms.profile.loader.exceptions.ProfileLoadException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import static java.lang.Thread.sleep;

/**
 * Created with IntelliJ IDEA.
 * @author Sandra O'Keeffe
 * Date: 9/4/13
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ProfilesLoader {

    private ProfileLoaderService profileLoaderService;

    /**
     * @param args
     *  Arguments are:
     *    arg0 - profileCount
     *    arg1 - tenantCount
     */
    public static void main (String[] args) {


        // load the spring context
        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/beans.xml");
        BeanFactory factory = context;

        // get bean to load the log4j properties
        ProfilesLoader profileLoader = (ProfilesLoader) factory.getBean("profilesLoader");


        if (args.length != 2) {
            System.out.println("Two arguments are required to execute the script:\n\t" +
                    " profileCount - an integer indicating how many profiles should be created\n\t"  +
                    " tenantCount - an integer indicating how many tenants should be created.\n" +
                    "Profiles will randomly assigned a tenant from the generated tenants.");
            System.exit(1);
        }

        // Process arguments
        int profileCount = -1;
        int tenantCount = -1;
        try {
            profileCount = Integer.parseInt(args[0]);
            tenantCount = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("Two arguments are required to execute the script:\n\t" +
                    " profileCount - an integer indicating how many profiles should be created\n\t"  +
                    " tenantCount - an integer indicating how many tenants should be created.\n" +
                    "Profiles will randomly assigned a tenant from the generated tenants.");
            System.exit(1);
        }

        profileLoader.profileLoaderService.setTenantCount(tenantCount);

        try {
            profileLoader.profileLoaderService.loadContent(profileCount);
        } catch (ProfileLoadException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    @Autowired
    public void setProfileLoaderService(ProfileLoaderService profileLoaderService) {
        this.profileLoaderService = profileLoaderService;
    }
}
