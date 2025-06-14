package com.fftmback.util;

import com.fftmback.domain.Role;
import com.fftmback.domain.RoleName;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

@UtilityClass
public class RoleUtils {

    private static final NavigableSet<RoleName> ROLE_HIERARCHY = new TreeSet<>((o1, o2) -> {
        if (o1.getPriority() > o2.getPriority()) {
            return 1;
        } else if (o1.getPriority() < o2.getPriority()) {
            return -1;
        }
        return 0;
    });

    static {
        ROLE_HIERARCHY.add(RoleName.OWNER);
        ROLE_HIERARCHY.add(RoleName.MEMBER_PLUS);
        ROLE_HIERARCHY.add(RoleName.MEMBER);
    }

    public static List<Role> getRoles(String roleName) {
        if (StringUtils.isBlank(roleName)) {
            return new ArrayList<>();
        }
        var role = RoleName.valueOf(roleName);
        return ROLE_HIERARCHY.headSet(role, true)
                .stream()
                .map(r -> new Role(r.name()))
                .toList();
    }
}
