package com.febfes.fftmback.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class SortUtils {

    public static Order getOrderFromParam(String sortParam) {
        Pattern pattern = Pattern.compile("([\\-+]?)(\\w+)");
        Matcher matcher = pattern.matcher(sortParam);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Sort parameter doesn't match pattern");
        }
        String direction = matcher.group(1);
        String property = matcher.group(2);
        return new Order(getDirection(direction), property);
    }

    public static List<Order> getOrderFromParams(String[] sortParams) {
        return Arrays.stream(sortParams)
                .map(SortUtils::getOrderFromParam)
                .toList();
    }

    private static Sort.Direction getDirection(String direction) {
        return direction.equals("+") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }
}
