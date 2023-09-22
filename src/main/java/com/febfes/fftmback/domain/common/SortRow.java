//package com.febfes.fftmback.domain.common;
//
//import jakarta.validation.constraints.NotBlank;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.Getter;
//import org.springframework.data.domain.Sort;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Getter
//public class SortRow {
//
//    @NotBlank(message = "Property can't be blank when sorting")
//    private String property;
//
//    private Sort.Direction direction;
//
//    public SortRow(String property, String direction) {
//        this.property = property;
//        this.direction = direction.equals("+") ? Sort.Direction.ASC : Sort.Direction.DESC;
//    }
//
//    public static SortRow getSortRowFromParam(String sortParam) {
//        Pattern pattern = Pattern.compile("([\\-+]?)(\\w+)");
//        Matcher matcher = pattern.matcher(sortParam);
//        if (!matcher.find()) {
//            throw new IllegalArgumentException("Sort parameter doesn't match pattern");
//        }
//        String direction = matcher.group(1);
//        String property = matcher.group(2);
//        return new SortRow(property, direction);
//    }
//
//    public static List<SortRow> getSortRowsFromParam(String[] sortParams) {
//        List<SortRow> sortRows = new ArrayList<>();
//        for (String sortParam : sortParams) {
//            sortRows.add(getSortRowFromParam(sortParam));
//        }
//        return sortRows;
//    }
//}
