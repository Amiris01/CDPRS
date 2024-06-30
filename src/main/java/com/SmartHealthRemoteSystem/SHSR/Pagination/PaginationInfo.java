package com.SmartHealthRemoteSystem.SHSR.Pagination;

import java.util.List;

public class PaginationInfo {
    
    private final List<?> dataToDisplay;
    private final int pageSize;
    private final int currentPage;
    private final int totalPage;
    private final int prevPage;
    private final int nextPage;

    public PaginationInfo(List<?> dataToDisplay, int pageSize, int currentPage, int totalPage, int prevPage, int nextPage) {
        this.dataToDisplay = dataToDisplay;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.prevPage = prevPage;
        this.nextPage = nextPage;
    }

    // Getters for the fields
    public List<?> getDataToDisplay() {
        return dataToDisplay;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getPrevPage() {
        return prevPage;
    }

    public int getNextPage() {
        return nextPage;
    }
}


