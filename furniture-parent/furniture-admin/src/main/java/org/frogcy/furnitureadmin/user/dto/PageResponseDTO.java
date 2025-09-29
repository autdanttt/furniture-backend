package org.frogcy.furnitureadmin.user.dto;

import lombok.*;

import java.lang.reflect.Type;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageResponseDTO<T> {
    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
