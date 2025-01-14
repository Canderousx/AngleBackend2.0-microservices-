package com.authService.app.Services.UserRole.Interfaces;

import com.authService.app.Models.UserRole;

public interface UserRoleServiceInterface {

    UserRole getByRoleName(String roleName);
}
