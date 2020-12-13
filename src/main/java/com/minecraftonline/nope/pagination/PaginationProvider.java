package com.minecraftonline.nope.pagination;

import org.spongepowered.common.service.pagination.NopePagination;

public interface PaginationProvider {
  NopePagination getOrCreatePagination();
}
