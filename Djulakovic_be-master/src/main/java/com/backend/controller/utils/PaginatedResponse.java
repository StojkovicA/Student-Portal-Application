package com.backend.controller.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PaginatedResponse<T, P> {

    private final List<T> data;
    private final long totalElements;

    private final P extras;
}
