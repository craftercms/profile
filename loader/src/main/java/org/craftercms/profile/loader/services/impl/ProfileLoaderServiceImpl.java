package org.craftercms.profile.loader.services.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.loader.exceptions.ProfileLoadException;
import org.craftercms.profile.loader.services.ProfileLoaderService;
import org.craftercms.profile.loader.services.ProfileWrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * @author Sandra O'Keeffe
 * Date: 9/5/13
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ProfileLoaderServiceImpl implements ProfileLoaderService {


    ProfileWrapperService profileWrapperService;
    private int tenantCount;
    private String[] tenants;

    @Override
    public void loadContent(int profileCount)
            throws ProfileLoadException {


        try {

            // create tenants
            int tenantsCreated = 0;
            for (String tenant : tenants) {

                profileWrapperService.createTenant(tenant);
                tenantsCreated++;

                if (tenantsCreated%100 == 0) {
                    System.out.println(tenantsCreated + " tenants created...");
                }
            }
            System.out.println("Tenants created successfully.");

            // create profiles
            int profilesCreated = 0;
            for (int i=0; i<profileCount; i++) {
                profileWrapperService.createProfile(tenants[randomFromInterval(1, tenants.length)-1]);
                profilesCreated++;

                if (profilesCreated%100 == 0) {
                    System.out.println(profilesCreated + " profiles created...");
                }

            }
            System.out.println("Profiles created successfully.");

        // assuming if this is thrown, all others will fail
        } catch (AppAuthenticationFailedException e) {
            System.err.println(e.getStackTrace());
            throw new ProfileLoadException(e);
        }
    }

    public void generateTenantNames() {

        tenants = new String[tenantCount];

        for (int i=0; i<tenantCount; i++) {
            tenants[i] =  RandomStringUtils.randomAlphabetic(randomFromInterval(8, 20));

        }



    }

    public int randomFromInterval(int from, int to) {
        return (int) Math.floor(Math.random()*(to-from+1)+from);
    }


    public void setTenantCount(int tenantCount) {
        this.tenantCount = tenantCount;

        // generate this number of tenants
        generateTenantNames();
    }


    @Value("${crafter.profile.loader.tenant.count}")
    public void setTenantCount(String tenantCount) {
        this.tenantCount = Integer.parseInt(tenantCount);

        // generate this number of tenants
        generateTenantNames();
    }

    public ProfileWrapperService getProfileWrapperService() {
        return profileWrapperService;
    }

    @Autowired
    public void setProfileWrapperService(ProfileWrapperService profileWrapperService) {
        this.profileWrapperService = profileWrapperService;
    }
}
