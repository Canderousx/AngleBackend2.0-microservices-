package com.authService.app.Config.Init;
import com.authService.app.Models.Account;
import com.authService.app.Models.EnvironmentVariables;
import com.authService.app.Models.UserRole;
import com.authService.app.Repositories.AccountRepository;
import com.authService.app.Repositories.UserRoleRepository;
import com.authService.app.Services.UserRole.UserRoleService;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Initialization {

    private final Logger logger = LogManager.getLogger(Initialization.class);

    private final String[] rolesNames = {"ROLE_USER","ROLE_MODERATOR","ROLE_ADMIN"};

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EnvironmentVariables environmentVariables;

    @Autowired
    private UserRoleService userRoleService;



    private void rolesInitialization(){
        logger.info("Checking roles in a database...");
        for(String role : rolesNames){
            if(!userRoleRepository.existsByName(role)){
                logger.info("Setting up: "+role);
                UserRole newRole = new UserRole();
                newRole.setName(role);
                userRoleRepository.save(newRole);
                logger.info("role saved");
            }
        }
        logger.info("Roles has been initialized.");
    }

    private void adminInitialization(){
        logger.info("Checking admin account...");
        if(!accountRepository.existsByUsername(environmentVariables.getMtAdminName())){
            logger.info("Admin account not found... Initializing");
            Account admin = new Account();
            admin.setUsername(environmentVariables.getMtAdminName());
            admin.setEmail(environmentVariables.getMtAdminEmail());
            admin.setPassword(passwordEncoder.encode(environmentVariables.getMtAdminPassword()));
            admin.setConfirmed(true);
            admin.getRoles().add(userRoleService.getByRoleName("ROLE_ADMIN"));
            admin.getRoles().add(userRoleService.getByRoleName("ROLE_MODERATOR"));
            accountRepository.save(admin);
            environmentVariables.setMtAdminPassword("");
        }
        logger.info("Admin account initialized.");
    }



    @PostConstruct
    public void appInitialization(){
        logger.info("Initializing backend app.");
        rolesInitialization();
        adminInitialization();
        logger.info("App initialization finished.");
    }
}
