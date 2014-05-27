/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
//package org.craftercms.profile.management.util;
//
//import java.io.Serializable;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Scope;
//import org.springframework.context.annotation.ScopedProxyMode;
//import org.springframework.stereotype.Component;
//
///**
// * @author David Escalante
// */
//
//@Component
//@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
//public class TenantPaging implements Serializable {
//    /**
//     *
//     */
//    private static final long serialVersionUID = 1L;
//
//    private static final int PAGE_SIZE = 10;
//
//    private int start;
//    private int end;
//    private String sortBy;
//    private String sortOrder;
//
//    private int pageSize;
//
//    private long total;
//
//    public int getStart() {
//        return start;
//    }
//
//    public void setStart(int start) {
//        this.start = start;
//    }
//
//    public int getEnd() {
//        return end;
//    }
//
//    public void setEnd(int end) {
//        this.end = end;
//    }
//
//    public int getPageSize() {
//        return pageSize;
//    }
//
//    @Value("${crafter.profile.tenant.page.size}")
//    public void setPageSize(int pageSize) {
//        this.pageSize = pageSize;
//        this.start = 0;
//        this.end = pageSize - 1;
//    }
//
//    public void next() {
//        if ((end + pageSize) <= (total - 1)) {
//            end += pageSize;
//            start += pageSize;
//        } else if (end < (total - 1)) {
//            end = (int)total - 1;
//            start += pageSize;
//        }
//    }
//
//    public void previous() {
//        if (end == (total - 1)) {
//            end = start - 1;
//            start -= pageSize;
//
//        } else if ((start - pageSize) > 0) {
//            end -= pageSize;
//            start -= pageSize;
//        } else if (start > 0) {
//            start = 0;
//            end = pageSize - 1;
//        }
//    }
//
//    public long getTotal() {
//        return total;
//    }
//
//    public void setTotal(long total) {
//        this.total = total;
//        if ((this.total < pageSize)) {
//            this.end = (int)total;
//        }
//    }
//
//    @Value("${crafter.profile.tenant.sort.by}")
//    public void setSortBy(String sortBy) {
//        this.sortBy = sortBy;
//    }
//
//    @Value("${crafter.profile.tenant.sort.order}")
//    public void setSortOrder(String order) {
//        this.sortOrder = order;
//    }
//
//    public String getSortBy() {
//        return sortBy;
//    }
//
//    public String getSortOrder() {
//        return sortOrder;
//    }
//}
