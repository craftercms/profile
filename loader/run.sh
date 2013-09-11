#!/bin/bash
# creates 10 tenants and 1000000 records
java -cp conf/*:lib/*:crafter-profile-loader-2.2.2-SNAPSHOT.jar org.craftercms.profile.loader.controller.ProfilesLoader 1000000 10