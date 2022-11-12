package ua.nix.balaniuk.javacodeset.util;

import org.junit.Test;
import ua.nix.balaniuk.javacodeset.dto.permission.UserPermissionsDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserPermissionsPolicyTest {

    @Test
    public void getUserPermissions_shodReturnAnyUserPermissionsDtoWithNotNullFields() {
        UserPermissionsDto userPermissionsDto =
                UserPermissionsPolicy.getUserPermissions(List.of("USER_ROLE"));
        assertNotNull(userPermissionsDto);
        assertThat(userPermissionsDto).hasNoNullFieldsOrProperties();
    }
}
