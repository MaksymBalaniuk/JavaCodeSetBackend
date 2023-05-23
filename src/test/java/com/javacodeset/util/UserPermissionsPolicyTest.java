package com.javacodeset.util;

import com.javacodeset.dto.permission.UserPermissionsDto;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserPermissionsPolicyTest {

    @Test
    public void getUserPermissions_shouldReturnAnyUserPermissionsDtoWithNotNullFields() {
        UserPermissionsDto userPermissionsDto =
                UserPermissionsPolicy.getUserPermissions(List.of("USER_ROLE"));
        assertNotNull(userPermissionsDto);
        assertThat(userPermissionsDto).hasNoNullFieldsOrProperties();
    }
}
