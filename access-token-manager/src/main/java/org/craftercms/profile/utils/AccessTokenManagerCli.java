/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.profile.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.TenantAction;
import org.craftercms.profile.api.TenantPermission;
import org.craftercms.profile.repositories.AccessTokenRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Simple command-line app to generate encrypted {@link org.craftercms.profile.api.AccessToken}s.
 *
 * @author avasquez
 */
public class AccessTokenManagerCli {

    private static final String CONTEXT_PATH = "crafter/profile/access-token-manager-cli-context.xml";

    private BufferedReader stdIn;
    private PrintWriter stdOut;
    private AccessTokenRepository repository;
    private ObjectMapper objectMapper;
    private Options options;
    private CommandLineParser cmdLineParser;

    public AccessTokenManagerCli(BufferedReader stdIn, PrintWriter stdOut, AccessTokenRepository repository,
                                 ObjectMapper objectMapper) {
        this.stdIn = stdIn;
        this.stdOut = stdOut;
        this.repository = repository;
        this.objectMapper = objectMapper;

        options = new Options();
        options.addOption("help", false, "Prints this message");
        options.addOption("add", false, "Adds a new access token");
        options.addOption("remove", true, "Removes the access token with the given ID");
        options.addOption("list", false, "Lists all access tokens, in JSON format");
        options.addOption("listpretty", false, "As list, but printed in pretty format");

        cmdLineParser = new BasicParser();
    }

    public void run(String... args) {
        try {
            CommandLine cmd = cmdLineParser.parse(options, args);

            if (cmd.hasOption("add")) {
                addToken();
            } else if (cmd.hasOption("remove")) {
                String tokenId = cmd.getOptionValue("remove");
                if (tokenId != null) {
                    removeToken(tokenId);
                } else {
                    dieWithHelpInfo("ERROR: No token ID specified for remove option");
                }
            } else if (cmd.hasOption("list")) {
                printTokensAsJson(false);
            } else if (cmd.hasOption("listpretty")) {
                printTokensAsJson(true);
            } else if (cmd.hasOption("help")) {
                printHelp();
            } else {
                dieWithHelpInfo("ERROR: Unrecognized command line option");
            }
        } catch (org.apache.commons.cli.ParseException e) {
            die("ERROR: Command line parsing failed", e);
        }
    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp(stdOut, HelpFormatter.DEFAULT_WIDTH, "java -jar access-token-manager.jar", null, options,
                HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null, true);

        stdOut.flush();
    }

    private void addToken() {
        AccessToken token = null;
        try {
            token = readAccessToken();
        } catch (IOException e) {
            die("ERROR: Unable to read from input", e);
        }

        try {
            repository.save(token);

            stdOut.println("New token created and saved to DB: " + token);
            stdOut.flush();
        } catch (MongoDataException e) {
            die("ERROR: Unable to save token " + token + " to DB", e);
        }
    }

    private void removeToken(String tokenId) {
        try {
            repository.removeById(tokenId);

            stdOut.println("Token with ID '" + tokenId + "' deleted from the DB");
            stdOut.flush();
        } catch (MongoDataException e) {
            die("ERROR: Unable to delete token with ID '" + tokenId + "' from DB", e);
        }
    }

    private void printTokensAsJson(boolean pretty) {
        try {
            Iterable<AccessToken> tokens = repository.findAll();
            String serializedTokens;

            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            if (pretty) {
                serializedTokens = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tokens);
            } else {
                serializedTokens = objectMapper.writeValueAsString(tokens);
            }

            stdOut.println(serializedTokens);
            stdOut.flush();
        } catch (MongoDataException e) {
            die("ERROR: Unable to retrieve access tokens from DB", e);
        } catch (JsonProcessingException e) {
            die("ERROR: Unable to serialize access tokens as JSON", e);
        }
    }

    private AccessToken readAccessToken() throws IOException {
        String application = readLineCheckingForEmpty("Enter application name: ");
        Date expirationDate = readExpirationDate();
        List<TenantPermission> permissions = readTenantPermissions();

        AccessToken token = new AccessToken();
        token.setId(UUID.randomUUID().toString());
        token.setApplication(application);
        token.setExpiresOn(expirationDate);
        token.setTenantPermissions(permissions);

        return token;
    }

    private String readLineCheckingForEmpty(String prompt) throws IOException {
        String in = "";

        while (StringUtils.isEmpty(in)) {
            stdOut.print(prompt);
            stdOut.flush();

            in = stdIn.readLine();
            if (StringUtils.isEmpty(in)) {
                stdOut.println("ERROR: No input received");
                stdOut.flush();
            }
        }

        return in;
    }

    private Date readExpirationDate() throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        while (true) {
            String dateStr = readLineCheckingForEmpty("Enter the expiration date of the token (mm/dd/yy): ");
            try {
                return dateFormat.parse(dateStr);
            } catch (ParseException e) {
                stdOut.println("ERROR: Invalid date format (expected format is mm/dd/yy)");
                stdOut.flush();
            }
        }
    }

    private List<TenantPermission> readTenantPermissions() throws IOException {
        List<TenantPermission> permissions = new ArrayList<>();

        stdOut.println("***** Enter tenant permissions *****");
        stdOut.flush();

        String addPermission;

        do {
            permissions.add(readPermission());

            stdOut.print("Add another permission (Y/n)? ");
            stdOut.flush();

            addPermission = stdIn.readLine();
            if (StringUtils.isEmpty(addPermission)) {
                addPermission = "n";
            }
        } while (addPermission.equalsIgnoreCase("y") || addPermission.equalsIgnoreCase("yes"));

        stdOut.println("***********************************");
        stdOut.flush();

        return permissions;
    }

    private TenantPermission readPermission() throws IOException {
        while (true) {
            String tenant = readLineCheckingForEmpty("Enter tenant name (use * for any tenant): ");
            String[] actions = readLineCheckingForEmpty("Enter allowed actions, separated by comma (valid actions " +
                    "are " + StringUtils.join(TenantAction.values(), ", ") + " or * for any action): ").split(
                    "\\s*,\\s*");
            boolean validActions = true;

            if (!ArrayUtils.contains(actions, "*")) {
                for (String action : actions) {
                    if (!EnumUtils.isValidEnum(TenantAction.class, action)) {
                        stdOut.println("ERROR: Unrecognized tenant action '" + action + "'");
                        stdOut.flush();

                        validActions = false;
                    }
                }
            }

            if (validActions) {
                TenantPermission permission = new TenantPermission(tenant);
                permission.setAllowedActions(new HashSet<>(Arrays.asList(actions)));

                return permission;
            }
        }
    }

    private void die(String message, Throwable e) {
        stdOut.println(message);
        stdOut.flush();

        e.printStackTrace(stdOut);

        System.exit(1);
    }

    private void dieWithHelpInfo(String message) {
        stdOut.println(message);
        stdOut.flush();

        printHelp();

        System.exit(1);
    }

    public static void main(String... args) {
        ApplicationContext context = getApplicationContext();
        AccessTokenRepository repository = context.getBean(AccessTokenRepository.class);
        ObjectMapper objectMapper = context.getBean(ObjectMapper.class);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter stdOut = new PrintWriter(System.out);
        AccessTokenManagerCli cli = new AccessTokenManagerCli(stdIn, stdOut, repository, objectMapper);

        cli.run(args);
    }

    public static ApplicationContext getApplicationContext() {
        ApplicationContext context = new ClassPathXmlApplicationContext(CONTEXT_PATH);

        return context;
    }

}
