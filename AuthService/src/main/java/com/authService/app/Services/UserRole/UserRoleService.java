package com.authService.app.Services.UserRole;

import com.authService.app.Models.UserRole;
import com.authService.app.Repositories.UserRoleRepository;
import com.authService.app.Services.UserRole.Interfaces.UserRoleServiceInterface;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRoleService implements UserRoleServiceInterface {

    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserRole getByRoleName(String roleName) {
        Optional<UserRole> role = userRoleRepository.findByName(roleName);
        if(role.isEmpty()){
            throw new RuntimeException("Desired role: "+roleName+" not found!!! Check initialization code and make sure desired role has a proper name");
        }
        return role.get();
    }
}
